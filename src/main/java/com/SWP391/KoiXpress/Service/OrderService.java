package com.SWP391.KoiXpress.Service;


import com.SWP391.KoiXpress.Entity.*;
import com.SWP391.KoiXpress.Entity.Enum.*;
import com.SWP391.KoiXpress.Exception.EntityNotFoundException;
import com.SWP391.KoiXpress.Exception.NotFoundException;
import com.SWP391.KoiXpress.Exception.OrderException;
import com.SWP391.KoiXpress.Exception.ProgressException;
import com.SWP391.KoiXpress.Model.request.Order.OrderDetailRequest;
import com.SWP391.KoiXpress.Model.request.Order.CreateOrderRequest;
import com.SWP391.KoiXpress.Model.request.Order.UpdateOrderRequest;
import com.SWP391.KoiXpress.Model.response.Box.CreateBoxDetailResponse;
import com.SWP391.KoiXpress.Model.response.Order.*;
import com.SWP391.KoiXpress.Model.response.Paging.PagedResponse;
import com.SWP391.KoiXpress.Model.response.Payment.PaymentResponse;
import com.SWP391.KoiXpress.Model.response.Progress.ProgressResponse;
import com.SWP391.KoiXpress.Model.response.Transaction.AllTransactionResponse;
import com.SWP391.KoiXpress.Model.response.User.UserResponse;
import com.SWP391.KoiXpress.Repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    @Autowired
    ProgressService progressService;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    ProgressRepository progressRepository;

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
        double[] destinationCoords = geoCodingService.geocoding(destinationLocation);

        String route = routingService.getDistanceFormattedRoute(originCoords[0], originCoords[1], destinationCoords[0], destinationCoords[1]);
        double totalDistance = routingService.extractDistance(route);
        orders.setTotalDistance(totalDistance);

        //
        List<String> wareHouseRepositoryAllLocation = wareHouseRepository.findAllAvailableLocations();
        double minDistance = Double.MAX_VALUE;
        String nearestWareHouse = null;
        for (String wareHouse : wareHouseRepositoryAllLocation) {
            double[] wareHouseCoords = geoCodingService.geocoding(wareHouse);
            String routeInfo = routingService.getDistanceFormattedRoute(originCoords[0], originCoords[1], wareHouseCoords[0], wareHouseCoords[1]);
            double distance = routingService.extractDistance(routeInfo);

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
            CreateBoxDetailResponse response = boxDetailService.createBox(fishSizeQuantityMap, orderDetail);

            orderDetail.setBoxDetails(response.getBoxDetails());
            orderDetail.setPrice(response.getTotalPrice());
            orderDetail.setTotalBox(response.getTotalCount());
            orderDetail.setTotalVolume(response.getTotalVolume());

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
            String secretKey = "G6O2N8KWLPP93KY4E6TZHB99Q0AJUTRG";
            String vnpUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
            String returnUrl = "http://transportkoifish.online/success?orderID=" + orders.getId();
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
            vnpParams.put("vnp_IpAddr", "14.225.220.122");

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

    public void createTransactionsPaymentSuccess(long orderId){
        Orders orders = orderRepository.findById(orderId)
                .orElseThrow(()-> new NotFoundException("Can not found order"));

        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setSubject("Thank You");
        emailDetail.setUsers(orders.getUsers());
        emailDetail.setLink("http://transportkoifish.online");
        emailDetail.setCreateDate(new Date());
        emailService.sendEmailThankYou(emailDetail);

        Payments payments = new Payments();
        payments.setOrders(orders);
        payments.setCreatePayment(new Date());
        payments.setPaymentMethod(PaymentMethod.BANK_TRANSFER);


        Set<Transactions> setTransaction = new HashSet<>();

        //Customer transaction
        Transactions transactionCustomerRewardPoints = new Transactions();
        Users customer = authenticationService.getCurrentUser();
        transactionCustomerRewardPoints.setFrom(null);
        transactionCustomerRewardPoints.setTo(customer);
        transactionCustomerRewardPoints.setPayments(payments);
        transactionCustomerRewardPoints.setTransactionStatus(TransactionStatus.SUCCESS);
        transactionCustomerRewardPoints.setDescription("Customer reward points");
        long newPoint = customer.getLoyaltyPoint() + (long)(orders.getTotalPrice()*0.00001);
        customer.setLoyaltyPoint(newPoint);
        setTransaction.add(transactionCustomerRewardPoints);

        Transactions transactionCUSTOMERtoMANAGER = new Transactions();
        Users manager = userRepository.findUsersByRoleAndIsDeleted(Role.MANAGER,false);
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

    public void createTransactionsRefund(long orderId){
        Orders orders = orderRepository.findById(orderId)
                .orElseThrow(()-> new NotFoundException("Can not found order"));
        Payments payments = new Payments();
        payments.setOrders(orders);
        payments.setCreatePayment(new Date());
        payments.setPaymentMethod(PaymentMethod.BANK_TRANSFER);


        Set<Transactions> setTransaction = new HashSet<>();

        Transactions transactionRefundToCustomer = new Transactions();
        Users customer = authenticationService.getCurrentUser();
        transactionRefundToCustomer.setFrom(null);
        transactionRefundToCustomer.setTo(customer);
        transactionRefundToCustomer.setPayments(payments);
        transactionRefundToCustomer.setTransactionStatus(TransactionStatus.SUCCESS);
        transactionRefundToCustomer.setDescription("Refund to Customer");
        long newPoint = customer.getLoyaltyPoint() + (long)(orders.getTotalPrice()*0.00001);
        customer.setLoyaltyPoint(newPoint);
        setTransaction.add(transactionRefundToCustomer);

        payments.setTransactions(setTransaction);
        orders.setOrderStatus(OrderStatus.PAID);
        orderRepository.save(orders);
        userRepository.save(customer);
        paymentRepository.save(payments);
    }


    public List<AllTransactionResponse> getAllTransaction() {
        List<Transactions> transactions = transactionRepository.findAll();
        return transactions
                .stream()
                .map(transaction -> {
                    AllTransactionResponse response = modelMapper.map(transaction, AllTransactionResponse.class);
                    Payments payment = transaction.getPayments();
                    Orders order = payment.getOrders();
                    PaymentResponse paymentResponse = modelMapper.map(payment, PaymentResponse.class);
                    OrderResponse orderResponse = modelMapper.map(order, OrderResponse.class);
                    orderResponse.setTotalPrice(order.getTotalPrice());
                    paymentResponse.setOrderResponse(orderResponse);
                    response.setPayments(paymentResponse);
                    return response;
                })
                .filter(response -> response.getDescription().equals("Customer to Manager"))
                .toList();
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
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Users users = authenticationService.getCurrentUser();

        List<OrderStatus> excludedStatuses = Arrays.asList(OrderStatus.DELIVERED);
        Page<Orders> orders = orderRepository.findOrdersByUsers(users, excludedStatuses, pageRequest);

        List<AllOrderByCurrentResponse> responses = orders.stream()
                .map(order -> {
                    AllOrderByCurrentResponse response = modelMapper.map(order, AllOrderByCurrentResponse.class);
                    List<ProgressResponse> progresses = progressService.trackingOrder(order.getTrackingOrder());
                    if (progresses == null) {
                        progresses = new ArrayList<>();
                    }

                    double distancePrice = order.calculateDistancePrice();
                    double discountPrice = order.calculateDiscountPrice();
                    response.setDistancePrice(distancePrice);
                    response.setDiscountPrice(discountPrice);
                    response.setProgresses(progresses);
                    return response;
                })

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

    public PagedResponse<AllOrderByCurrentResponse> getAllOrdersDeliveredByCurrentUser(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Users currentUser = authenticationService.getCurrentUser();

        Page<Orders> orders = orderRepository.findOrdersByUsersAndStatus(currentUser, OrderStatus.DELIVERED, pageRequest);

        List<AllOrderByCurrentResponse> responses = orders.stream()
                .map(order -> {
                    AllOrderByCurrentResponse response = modelMapper.map(order, AllOrderByCurrentResponse.class);
                    List<ProgressResponse> progresses = progressService.trackingOrder(order.getTrackingOrder());
                    if (progresses == null) {
                        progresses = new ArrayList<>();
                    }

                    double distancePrice = order.calculateDistancePrice();
                    double discountPrice = order.calculateDiscountPrice();
                    response.setDistancePrice(distancePrice);
                    response.setDiscountPrice(discountPrice);
                    response.setProgresses(progresses);
                    return response;
                })
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

    public UpdateOrderResponse updateBySale(long id, UpdateOrderRequest updateOrderRequest) {
        Orders oldOrders = getOrderById(id);
        if (oldOrders.getOrderStatus() == OrderStatus.PENDING) {
            oldOrders.setOrderStatus(updateOrderRequest.getOrderStatus());
            orderRepository.save(oldOrders);

            if (updateOrderRequest.getOrderStatus() == OrderStatus.ACCEPTED) {
                oldOrders.setOrderStatus(OrderStatus.AWAITING_PAYMENT);
            }

            if(updateOrderRequest.getOrderStatus() == OrderStatus.REJECTED){
                oldOrders.setFailure_reason("Sale_Staff refuse the order, due to information not correct");
            }

            Orders newOrders = orderRepository.save(oldOrders);


            return modelMapper.map(newOrders, UpdateOrderResponse.class);
        } else {
            throw new OrderException("Cannot update");
        }
    }

    public UpdateOrderResponse updateOrderByDelivery(long id, UpdateOrderRequest updateOrderRequest) {
        Orders orders = orderRepository.findById(id)
                .filter(order -> order.getOrderStatus() == OrderStatus.BOOKING)
                .orElseThrow(() -> new OrderException("Cannot update"));
        orders.setOrderStatus(updateOrderRequest.getOrderStatus());
        Orders updatedOrder = orderRepository.save(orders);
        return modelMapper.map(updatedOrder, UpdateOrderResponse.class);
    }

    public PagedResponse<AllOrderResponse> getListOrderPending(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Orders> ordersPage = orderRepository.findOrdersByOrderStatus(OrderStatus.PENDING, pageRequest);

        List<AllOrderResponse> responses = ordersPage.stream()
                .map(order -> {
                    UserResponse userResponse = modelMapper.map(order.getUsers(), UserResponse.class);
                    AllOrderResponse orderResponse = modelMapper.map(order, AllOrderResponse.class);
                    orderResponse.setEachUserResponse(userResponse);
                    return orderResponse;
                })
                .collect(Collectors.toList());

        return new PagedResponse<>(
                responses,
                page,
                size,
                ordersPage.getTotalElements(),
                ordersPage.getTotalPages(),
                ordersPage.isLast()
        );
    }

    public PagedResponse<AllOrderResponse> getListOrderBooking(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Orders> ordersPage = orderRepository.findOrdersByOrderStatus(OrderStatus.BOOKING, pageRequest);

        List<AllOrderResponse> responses = ordersPage.stream()
                .map(order -> {
                    UserResponse userResponse = modelMapper.map(order.getUsers(), UserResponse.class);
                    AllOrderResponse orderResponse = modelMapper.map(order, AllOrderResponse.class);
                    orderResponse.setEachUserResponse(userResponse);
                    return orderResponse;
                })
                .collect(Collectors.toList());

        return new PagedResponse<>(
                responses,
                page,
                size,
                ordersPage.getTotalElements(),
                ordersPage.getTotalPages(),
                ordersPage.isLast()
        );
    }

    public PagedResponse<AllOrderResponse> getListOrderAwaitingPayment(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Orders> ordersPage = orderRepository.findOrdersByOrderStatus(OrderStatus.AWAITING_PAYMENT, pageRequest);

        List<AllOrderResponse> responses = ordersPage.stream()
                .map(order -> {
                    UserResponse userResponse = modelMapper.map(order.getUsers(), UserResponse.class);
                    AllOrderResponse orderResponse = modelMapper.map(order, AllOrderResponse.class);
                    orderResponse.setEachUserResponse(userResponse);
                    return orderResponse;
                })
                .collect(Collectors.toList());

        return new PagedResponse<>(
                responses,
                page,
                size,
                ordersPage.getTotalElements(),
                ordersPage.getTotalPages(),
                ordersPage.isLast()
        );
    }

    public PagedResponse<AllOrderResponse> getListOrderPaid(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Orders> ordersPage = orderRepository.findOrdersByOrderStatus(OrderStatus.PAID, pageRequest);

        List<AllOrderResponse> responses = ordersPage.stream()
                .map(order -> {
                    UserResponse userResponse = modelMapper.map(order.getUsers(), UserResponse.class);
                    AllOrderResponse orderResponse = modelMapper.map(order, AllOrderResponse.class);
                    orderResponse.setEachUserResponse(userResponse);
                    return orderResponse;
                })
                .collect(Collectors.toList());

        return new PagedResponse<>(
                responses,
                page,
                size,
                ordersPage.getTotalElements(),
                ordersPage.getTotalPages(),
                ordersPage.isLast()
        );
    }

    public PagedResponse<AllOrderResponse> getListOrderRejected(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Orders> ordersPage = orderRepository.findOrdersByOrderStatus(OrderStatus.REJECTED, pageRequest);

        List<AllOrderResponse> responses = ordersPage.stream()
                .map(order -> {
                    UserResponse userResponse = modelMapper.map(order.getUsers(), UserResponse.class);
                    AllOrderResponse orderResponse = modelMapper.map(order, AllOrderResponse.class);
                    orderResponse.setEachUserResponse(userResponse);
                    return orderResponse;
                })
                .collect(Collectors.toList());

        return new PagedResponse<>(
                responses,
                page,
                size,
                ordersPage.getTotalElements(),
                ordersPage.getTotalPages(),
                ordersPage.isLast()
        );
    }

    public PagedResponse<AllOrderResponse> getListOrderShipping(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Orders> ordersPage = orderRepository.findOrdersByOrderStatus(OrderStatus.SHIPPING, pageRequest);

        List<AllOrderResponse> responses = ordersPage.stream()
                .map(order -> {
                    UserResponse userResponse = modelMapper.map(order.getUsers(), UserResponse.class);
                    AllOrderResponse orderResponse = modelMapper.map(order, AllOrderResponse.class);
                    orderResponse.setEachUserResponse(userResponse);
                    return orderResponse;
                })
                .collect(Collectors.toList());

        return new PagedResponse<>(
                responses,
                page,
                size,
                ordersPage.getTotalElements(),
                ordersPage.getTotalPages(),
                ordersPage.isLast()
        );
    }

    public PagedResponse<AllOrderResponse> getListOrderDelivered(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Orders> ordersPage = orderRepository.findOrdersByOrderStatus(OrderStatus.DELIVERED, pageRequest);

        List<AllOrderResponse> responses = ordersPage.stream()
                .map(order -> {
                    UserResponse userResponse = modelMapper.map(order.getUsers(), UserResponse.class);
                    AllOrderResponse orderResponse = modelMapper.map(order, AllOrderResponse.class);
                    orderResponse.setEachUserResponse(userResponse);
                    return orderResponse;
                })
                .collect(Collectors.toList());

        return new PagedResponse<>(
                responses,
                page,
                size,
                ordersPage.getTotalElements(),
                ordersPage.getTotalPages(),
                ordersPage.isLast()
        );
    }

    public PagedResponse<AllOrderCanceledResponse> getListOrderCanceled(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Orders> ordersPage = orderRepository.findOrdersByOrderStatus(OrderStatus.CANCELED, pageRequest);

        List<AllOrderCanceledResponse> responses = ordersPage.stream()
                .map(order -> {
                    UserResponse userResponse = modelMapper.map(order.getUsers(), UserResponse.class);
                    AllOrderCanceledResponse orderResponse = modelMapper.map(order, AllOrderCanceledResponse.class);
                    List<Progresses> progresses = progressRepository.findProgressesByOrdersId(orderResponse.getId())
                            .orElseThrow(()->new ProgressException("Can not found"));
                    String reason = progresses.stream().map(Progresses::getFailure_reason).filter(Objects::nonNull).findFirst().orElse(null);
                    if(reason!=null){
                        orderResponse.setFailure_reason(reason);
                    }
                    orderResponse.setEachUserResponse(userResponse);
                    return orderResponse;
                })
                .collect(Collectors.toList());

        return new PagedResponse<>(
                responses,
                page,
                size,
                ordersPage.getTotalElements(),
                ordersPage.getTotalPages(),
                ordersPage.isLast()
        );
    }

    public void delete(long id) {
        Orders oldOrders = getOrderById(id);
        if (oldOrders.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new OrderException("Order are delivered, can not delete");
        }
        oldOrders.setFailure_reason("Customer canceled");
        oldOrders.setOrderStatus(OrderStatus.CANCELED);
        orderRepository.save(oldOrders);
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

    public PagedResponse<AllOrderResponse> getAll(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<Orders> ordersPage = orderRepository.findAll(pageRequest);

        List<AllOrderResponse> responses = ordersPage.stream()
                .map(order -> {
                    UserResponse userResponse = modelMapper.map(order.getUsers(), UserResponse.class);
                    AllOrderResponse orderResponse = modelMapper.map(order, AllOrderResponse.class);
                    orderResponse.setEachUserResponse(userResponse);
                    return orderResponse;
                })
                .collect(Collectors.toList());

        return new PagedResponse<>(
                responses,
                page,
                size,
                ordersPage.getTotalElements(),
                ordersPage.getTotalPages(),
                ordersPage.isLast()
        );
    }

}