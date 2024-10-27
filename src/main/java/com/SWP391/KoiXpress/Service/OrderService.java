package com.SWP391.KoiXpress.Service;


import com.SWP391.KoiXpress.Entity.*;
import com.SWP391.KoiXpress.Entity.Enum.*;
import com.SWP391.KoiXpress.Exception.EntityNotFoundException;
import com.SWP391.KoiXpress.Exception.NotFoundException;
import com.SWP391.KoiXpress.Exception.OrderException;
import com.SWP391.KoiXpress.Model.request.Order.OrderDetailRequest;
import com.SWP391.KoiXpress.Model.request.Order.CreateOrderRequest;
import com.SWP391.KoiXpress.Model.request.Order.UpdateOrderRequest;
import com.SWP391.KoiXpress.Model.response.Box.CreateBoxDetailResponse;
import com.SWP391.KoiXpress.Model.response.Order.*;
import com.SWP391.KoiXpress.Model.response.Paging.PagedResponse;
import com.SWP391.KoiXpress.Model.response.User.UserResponse;
import com.SWP391.KoiXpress.Repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;




@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    BoxDetailService boxDetailService;

    @Autowired
    GeoCodingService geoCodingService;

    @Autowired
    RoutingService routingService;

    @Autowired
    WareHouseRepository wareHouseRepository;

    @Autowired
    OrderDetailRepository orderDetailRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    EmailService emailService;

    // Create
    public CreateOrderResponse create(CreateOrderRequest createOrderRequest) throws Exception {
        Users users = authenticationService.getCurrentUser();
        Orders orders = new Orders();
        double totalPrice = 0;
        double totalVolume = 0;
        int totalBox = 0;
        int totalQuantityFish = 0;

        orders.setUsers(users);

        orders.setOrderDate(new Date());

        orders.setDeliveryDate(Date.from(LocalDate.now().plusWeeks(4).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        String destinationLocation = createOrderRequest.getDestinationLocation();

        String originLocation = createOrderRequest.getOriginLocation();

        double[] originCoords = geoCodingService.geocoding(originLocation);
        double[] destination = geoCodingService.geocoding(destinationLocation);

        String routeOD = routingService.getFormattedRoute(originCoords[0], originCoords[1], destination[0], destination[1]);
        double distanceOD = extractDistance(routeOD);
        orders.setTotalDistance(distanceOD);

        //
        List<String> wareHouseRepositoryAllLocation = wareHouseRepository.findAllLocation();
        double minDistance = Double.MAX_VALUE;
        String nearestWareHouse = null;
        for (String wareHouse : wareHouseRepositoryAllLocation) {
            double[] wareHouseCoords = geoCodingService.geocoding(wareHouse);
            String routeInfo = routingService.getFormattedRoute(originCoords[0], originCoords[1], wareHouseCoords[0], wareHouseCoords[1]);
            double distance = extractDistance(routeInfo);

            if (distance < minDistance && distance != -1) {
                minDistance = distance; // Cập nhật khoảng cách nhỏ nhất
                nearestWareHouse = wareHouse; // Cập nhật kho gần nhất
            }
        }
        orders.setNearWareHouse(nearestWareHouse);
        //
        orders.setRecipientInfo(createOrderRequest.getRecipientInfo());
        orders.setOriginLocation(createOrderRequest.getOriginLocation());
        orders.setDestinationLocation(createOrderRequest.getDestinationLocation());
        orders.setMethodTransPort(createOrderRequest.getMethodTransPort());
        orders.setCustomerNotes(createOrderRequest.getCustomerNotes());
        orders.setOrderStatus(OrderStatus.PENDING);
        orders.setDescribeOrder(createOrderRequest.getDescribeOrder());
        orderRepository.save(orders);

        List<OrderDetails> orderDetails = new ArrayList<>();
        for (OrderDetailRequest orderDetailRequest : createOrderRequest.getOrderDetailRequestList()) {
            Map<Double, Integer> fishSizeQuantityMap = Map.of(orderDetailRequest.getSizeOfFish(), orderDetailRequest.getNumberOfFish());
            OrderDetails orderDetail = new OrderDetails();

            orderDetail.setOrders(orders);
            orderDetail.setPriceOfFish(orderDetailRequest.getPriceOfFish());
            orderDetail.setNameFarm(orderDetailRequest.getNameFarm());
            orderDetail.setFarmAddress(orderDetailRequest.getFarmAddress());
            orderDetail.setOrigin(orderDetailRequest.getOrigin());
            orderDetail.setInspectionDate(Date.from(LocalDate.now().minusYears(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            orderDetail.setFishSpecies(orderDetailRequest.getFishSpecies());
            orderDetail.setNumberOfFish(orderDetailRequest.getNumberOfFish());
            orderDetail.setSizeOfFish(orderDetailRequest.getSizeOfFish());
            orderDetail.setHealthFishStatus(HealthFishStatus.HEALTHY);

            orderDetailRepository.save(orderDetail);
            CreateBoxDetailResponse boxDetails = boxDetailService.createBox(fishSizeQuantityMap, orderDetail);

            orderDetail.setBoxDetails(boxDetails.getBoxDetails());
            orderDetail.setPrice(boxDetails.getTotalPrice());
            orderDetail.setTotalBox(boxDetails.getTotalCount());
            orderDetail.setTotalVolume(boxDetails.getTotalVolume());

            orderDetails.add(orderDetail);

        }
        for (OrderDetails orderDetail : orderDetails) {
            totalPrice += orderDetail.getPrice(); // Cộng dồn giá
            totalBox += orderDetail.getTotalBox(); // Cộng dồn số lượng
            totalVolume += orderDetail.getTotalVolume();
            totalQuantityFish += orderDetail.getNumberOfFish();// Cộng dồn thể tích
        }
        orders.setOrderDetails(orderDetails);
        orders.setTotalBoxPrice(totalPrice);
        orders.setTotalPrice(totalPrice);
        orders.setTotalBox(totalBox);
        orders.setTotalVolume(totalVolume);
        orders.setTotalQuantity(totalQuantityFish);
        orderRepository.save(orders);
        double calculatePrice = orders.calculatePrice();
        orders.setTotalPrice(calculatePrice);
        orderRepository.save(orders);
        return modelMapper.map(orders, CreateOrderResponse.class);
    }


    public String orderPaymentUrl(long orderId) throws Exception {
        Orders orders = orderRepository.findOrdersById(orderId);
        if (orders.getOrderStatus() == OrderStatus.AWAITING_PAYMENT) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            LocalDateTime createDate = LocalDateTime.now();
            String formattedCreateDate = createDate.format(formatter);

            String orderID = String.valueOf(orders.getId());
            double totalPrice = orders.getTotalPrice() * 100;
            String amount = String.valueOf((int) totalPrice);

            String tmnCode = "U3CV658K";
            String secretKey = "O061SWJB8ISCTPWUPLZG152JU6MT1EVU";
            String vnpUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
            String returnUrl = "http://localhost:5173/success?orderID=" + orders.getId();
            String currCode = "VND";

            Map<String, String> vnpParams = new TreeMap<>();
            vnpParams.put("vnp_Version", "2.1.0");
            vnpParams.put("vnp_Command", "pay");
            vnpParams.put("vnp_TmnCode", tmnCode);
            vnpParams.put("vnp_Locale", "vn");
            vnpParams.put("vnp_CurrCode", currCode);
            vnpParams.put("vnp_TxnRef", orderID);
            vnpParams.put("vnp_OrderInfo", "Thanh toan cho ma GD: " + orders.getId());
            vnpParams.put("vnp_OrderType", "other");
            vnpParams.put("vnp_Amount", amount);

            vnpParams.put("vnp_ReturnUrl", returnUrl);
            vnpParams.put("vnp_CreateDate", formattedCreateDate);
            vnpParams.put("vnp_IpAddr", "128.199.178.23");

            StringBuilder signDataBuilder = new StringBuilder();
            for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
                signDataBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()));
                signDataBuilder.append("=");
                signDataBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
                signDataBuilder.append("&");
            }
            signDataBuilder.deleteCharAt(signDataBuilder.length() - 1); // Remove last '&'

            String signData = signDataBuilder.toString();
            String signed = generateHMAC(secretKey, signData);

            vnpParams.put("vnp_SecureHash", signed);

            StringBuilder urlBuilder = new StringBuilder(vnpUrl);
            urlBuilder.append("?");
            for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
                urlBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()));
                urlBuilder.append("=");
                urlBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
                urlBuilder.append("&");
            }
            urlBuilder.deleteCharAt(urlBuilder.length() - 1); // Remove last '&'

            return urlBuilder.toString();
        }
        throw new OrderException("Order can not payment yet");
    }


    public void createTransactions(long orderId){
        Orders orders = orderRepository.findById(orderId)
                .orElseThrow(()-> new NotFoundException("Can not found order"));

        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setSubject("Thank You");
        emailDetail.setUsers(orders.getUsers());
        emailDetail.setLink("http://transportkoifish.online");
        emailService.sendEmailThankYou(emailDetail);

        Payments payments = new Payments();
        payments.setOrders(orders);
        payments.setCreatePayment(new Date());
        payments.setPaymentMethod(PaymentMethod.BANK_TRANSFER);


        Set<Transactions> setTransaction = new HashSet<>();

        //Customer transaction
        Transactions transactionVNPAYtoCUSTOMER = new Transactions();
        Users customer = authenticationService.getCurrentUser();
        transactionVNPAYtoCUSTOMER.setFrom(null);
        transactionVNPAYtoCUSTOMER.setTo(customer);
        transactionVNPAYtoCUSTOMER.setPayments(payments);
        transactionVNPAYtoCUSTOMER.setTransactionStatus(TransactionStatus.SUCCESS);
        transactionVNPAYtoCUSTOMER.setDescription("VNPAY to Customer");
        long newPoint = customer.getLoyaltyPoint() + (long)(orders.getTotalPrice()*0.10);
        customer.setLoyaltyPoint(newPoint);
        setTransaction.add(transactionVNPAYtoCUSTOMER);

        Transactions transactionCUSTOMERtoMANAGER = new Transactions();
        Users manager = userRepository.findUsersByRole(Role.MANAGER);
        transactionCUSTOMERtoMANAGER.setFrom(customer);
        transactionCUSTOMERtoMANAGER.setTo(manager);
        transactionCUSTOMERtoMANAGER.setPayments(payments);
        transactionCUSTOMERtoMANAGER.setTransactionStatus(TransactionStatus.SUCCESS);
        transactionCUSTOMERtoMANAGER.setDescription("Customer to Manager");
        float balance = manager.getBalance() + (long)(orders.getTotalPrice());
        manager.setBalance(balance);
        setTransaction.add(transactionCUSTOMERtoMANAGER);


        payments.setTransactions(setTransaction);
        orders.setOrderStatus(OrderStatus.PAID);
        orderRepository.save(orders);
        userRepository.save(manager);
        userRepository.save(customer);
        paymentRepository.save(payments);
    }


    private String generateHMAC(String secretKey, String signData) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSha512 = Mac.getInstance("HmacSHA512");
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmacSha512.init(keySpec);
        byte[] hmacBytes = hmacSha512.doFinal(signData.getBytes(StandardCharsets.UTF_8));

        StringBuilder result = new StringBuilder();
        for (byte b : hmacBytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }


    public PagedResponse<AllOrderByCurrentResponse> getAllOrdersByCurrentUser(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Users users = authenticationService.getCurrentUser();
        Page<Orders> orders = orderRepository.findOrdersByUsers(users, pageRequest);

        List<AllOrderByCurrentResponse> responses = orders.stream()
                .map(order -> {
                    AllOrderByCurrentResponse response = modelMapper.map(order, AllOrderByCurrentResponse.class);

                    double distancePrice = order.calculateDistancePrice();
                    double discountPrice = order.calculateDiscountPrice();
                    response.setDistancePrice(distancePrice);
                    response.setDiscountPrice(discountPrice);

                    return response;
                })
                .filter(order -> order.getOrderStatus() != OrderStatus.CANCELED)
                .sorted(Comparator.comparing(AllOrderByCurrentResponse::getOrderDate))
                .collect(Collectors.toList());

        Page<AllOrderByCurrentResponse> responsePage = new PageImpl<>(responses, pageRequest, orders.getTotalElements());

        return new PagedResponse<>(
                responsePage.getContent(),
                page,
                size,
                responsePage.getTotalElements(),
                responsePage.getTotalPages(),
                responsePage.isLast()
        );
    }


    public List<AllOrderByCurrentResponse> getAllOrdersDeliveredByCurrentUser() {
        Users users = authenticationService.getCurrentUser();
        List<Orders> orders = orderRepository.findOrdersByUsers(users);
        return orders.stream()
                .map(order -> modelMapper.map(order, AllOrderByCurrentResponse.class))
                .filter(order -> order.getOrderStatus() == OrderStatus.DELIVERED)
                .sorted(Comparator.comparing(AllOrderByCurrentResponse::getOrderDate))
                .collect(Collectors.toList());
    }

    //Sale update
    public UpdateOrderResponse updateBySale(long id, UpdateOrderRequest updateOrderRequest) {
        Orders oldOrders = getOrderById(id);
        if (oldOrders.getOrderStatus() == OrderStatus.PENDING) {
            oldOrders.setOrderStatus(updateOrderRequest.getOrderStatus());
            orderRepository.save(oldOrders);
            if(updateOrderRequest.getOrderStatus() == OrderStatus.ACCEPTED){
                oldOrders.setOrderStatus(OrderStatus.AWAITING_PAYMENT);
            }
            Orders newOrders = orderRepository.save(oldOrders);
            return modelMapper.map(newOrders, UpdateOrderResponse.class);
        } else {
            throw new OrderException("Can not update");
        }
    }

    //deli update
    public UpdateOrderResponse updateOrderByDelivery(long id, UpdateOrderRequest updateOrderRequest) {
        return orderRepository.findById(id)
                .filter(order -> order.getOrderStatus() == OrderStatus.PAID)
                .map(order -> {
                    order.setOrderStatus(updateOrderRequest.getOrderStatus());
                    return orderRepository.save(order);
                })
                .map(saveOrder -> modelMapper.map(saveOrder, UpdateOrderResponse.class))
                .orElseThrow(() -> new OrderException("can not update"));
    }

    //list nhung order dang pending
    public List<AllOrderResponse> getListOrderPending() {
        List<Orders> orders = orderRepository.findOrdersByOrderStatus(OrderStatus.PENDING);
        if (orders != null) {
            return orders.stream()
                    .sorted(Comparator.comparing(Orders::getOrderDate))  // Sắp xếp theo orderDate
                    .map(order -> {
                        UserResponse userResponse = modelMapper.map(order.getUsers(), UserResponse.class);
                        AllOrderResponse orderResponse = modelMapper.map(order, AllOrderResponse.class);
                        orderResponse.setEachUserResponse(userResponse);
                        return orderResponse;
                    })
                    .collect(Collectors.toList());
        }
        throw new OrderException("Can found list");
    }

    public List<AllOrderResponse> getListOrderAwaitingPayment() {
        List<Orders> orders = orderRepository.findOrdersByOrderStatus(OrderStatus.AWAITING_PAYMENT);
        if (orders != null) {
            return orders.stream()
                    .sorted(Comparator.comparing(Orders::getOrderDate))  // Sắp xếp theo orderDate
                    .map(order -> {
                        UserResponse userResponse = modelMapper.map(order.getUsers(), UserResponse.class);
                        AllOrderResponse orderResponse = modelMapper.map(order, AllOrderResponse.class);
                        orderResponse.setEachUserResponse(userResponse);
                        return orderResponse;
                    })
                    .collect(Collectors.toList());
        }
        throw new OrderException("Can found list");
    }

    public List<AllOrderResponse> getListOrderPaid() {
        List<Orders> orders = orderRepository.findOrdersByOrderStatus(OrderStatus.PAID);
        if (orders != null) {
            return orders.stream()
                    .sorted(Comparator.comparing(Orders::getOrderDate))  // Sắp xếp theo orderDate
                    .map(order -> {
                        UserResponse userResponse = modelMapper.map(order.getUsers(), UserResponse.class);
                        AllOrderResponse orderResponse = modelMapper.map(order, AllOrderResponse.class);
                        orderResponse.setEachUserResponse(userResponse);
                        return orderResponse;
                    })
                    .collect(Collectors.toList());
        }
        throw new OrderException("Can found list");
    }

    public List<AllOrderResponse> getListOrderRejected() {
        List<Orders> orders = orderRepository.findOrdersByOrderStatus(OrderStatus.REJECTED);
        if (orders != null) {
            return orders.stream()
                    .sorted(Comparator.comparing(Orders::getOrderDate))  // Sắp xếp theo orderDate
                    .map(order -> {
                        UserResponse userResponse = modelMapper.map(order.getUsers(), UserResponse.class);
                        AllOrderResponse orderResponse = modelMapper.map(order, AllOrderResponse.class);
                        orderResponse.setEachUserResponse(userResponse);
                        return orderResponse;
                    })
                    .collect(Collectors.toList());
        }
        throw new OrderException("Can found list");
    }

    public List<AllOrderResponse> getListOrderShipping() {
        List<Orders> orders = orderRepository.findOrdersByOrderStatus(OrderStatus.SHIPPING);
        if (orders != null) {
            return orders.stream()
                    .sorted(Comparator.comparing(Orders::getOrderDate))  // Sắp xếp theo orderDate
                    .map(order -> {
                        UserResponse userResponse = modelMapper.map(order.getUsers(), UserResponse.class);
                        AllOrderResponse orderResponse = modelMapper.map(order, AllOrderResponse.class);
                        orderResponse.setEachUserResponse(userResponse);
                        return orderResponse;
                    })
                    .collect(Collectors.toList());
        }
        throw new OrderException("Can found list");
    }


    public List<AllOrderResponse> getListOrderDelivered() {
        List<Orders> orders = orderRepository.findOrdersByOrderStatus(OrderStatus.DELIVERED);
        if (orders != null) {
            return orders.stream()
                    .sorted(Comparator.comparing(Orders::getOrderDate))  // Sắp xếp theo orderDate
                    .map(order -> {
                        UserResponse userResponse = modelMapper.map(order.getUsers(), UserResponse.class);
                        AllOrderResponse orderResponse = modelMapper.map(order, AllOrderResponse.class);
                        orderResponse.setEachUserResponse(userResponse);
                        return orderResponse;
                    })
                    .collect(Collectors.toList());
        }
        throw new OrderException("Can found list");
    }

    //

    //Delete

    public DeleteOrderResponse delete(long id) {
        Orders oldOrders = getOrderById(id);
        if (oldOrders.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new OrderException("Order are delivered, can delete");
        }
        oldOrders.setOrderStatus(OrderStatus.CANCELED);
        orderRepository.save(oldOrders);
        return modelMapper.map(oldOrders, DeleteOrderResponse.class);
    }


    public Orders getOrderById(long id) {
        Orders oldOrders = orderRepository.findOrdersById(id);
        if (oldOrders == null || oldOrders.getOrderStatus() == OrderStatus.CANCELED) {
            throw new EntityNotFoundException("Order not found");
        }
        return oldOrders;
    }


    public CreateOrderResponse getEachOrderById(long id) {
        Orders orders = orderRepository.findOrdersById(id);
        if (orders == null) {
            throw new NotFoundException("Order not found");
        }
        return modelMapper.map(orders, CreateOrderResponse.class);
    }

    public List<AllOrderResponse> getAll(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Orders> orders = orderRepository.findAll(pageRequest);
        return orders.stream().sorted(Comparator.comparing(Orders::getOrderDate)).map(order -> {
            UserResponse userResponse = modelMapper.map(order.getUsers(), UserResponse.class);
            AllOrderResponse orderResponse = modelMapper.map(order, AllOrderResponse.class);
            orderResponse.setEachUserResponse(userResponse);
            return orderResponse;
        }).collect(Collectors.toList());
    }




    private double extractDistance(String routeInfo) {
        String[] lines = routeInfo.split("\n");
        String totalDistance = null;
        for (String line : lines) {
            if (line.startsWith("Total Distance: ")) {
                totalDistance = line;
                break;
            }
        }
        if (totalDistance != null) {
            String[] parts = totalDistance.split(":");
            if (parts.length > 1) {
                String distanceValue = parts[1].trim();
                return Double.parseDouble(distanceValue.split(" ")[0]);
            }
        }
        throw new EntityNotFoundException("Cant find distance");
    }

}