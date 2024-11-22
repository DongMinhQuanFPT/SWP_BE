package com.SWP391.KoiXpress.Service;

import com.SWP391.KoiXpress.Entity.EmailDetail;
import com.SWP391.KoiXpress.Entity.Enum.OrderStatus;
import com.SWP391.KoiXpress.Entity.Enum.ProgressStatus;
import com.SWP391.KoiXpress.Entity.Orders;
import com.SWP391.KoiXpress.Entity.Progresses;
import com.SWP391.KoiXpress.Entity.WareHouses;
import com.SWP391.KoiXpress.Exception.NotFoundException;
import com.SWP391.KoiXpress.Exception.OrderException;
import com.SWP391.KoiXpress.Exception.ProgressException;
import com.SWP391.KoiXpress.Exception.WareHouseException;
import com.SWP391.KoiXpress.Model.request.WareHouse.CreateWareHouseRequest;
import com.SWP391.KoiXpress.Model.response.Order.AllOrderResponse;
import com.SWP391.KoiXpress.Model.response.WareHouse.AllWareHouseResponse;
import com.SWP391.KoiXpress.Model.response.WareHouse.CreateWarehouseResponse;
import com.SWP391.KoiXpress.Model.response.WareHouse.UpdateWareHouseRequest;
import com.SWP391.KoiXpress.Model.response.WareHouse.WareHouseContainOrderResponse;
import com.SWP391.KoiXpress.Repository.OrderRepository;
import com.SWP391.KoiXpress.Repository.ProgressRepository;
import com.SWP391.KoiXpress.Repository.WareHouseRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class WareHouseService {

    @Autowired
    WareHouseRepository wareHouseRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    EmailService emailService;

    @Autowired
    ProgressRepository progressRepository;

    @Autowired
    RoutingService routingService;

    @Autowired
    GeoCodingService geoCodingService;

    public CreateWarehouseResponse create(CreateWareHouseRequest createWareHouseRequest){
        WareHouses wareHouses = new WareHouses();
        wareHouses.setLocation(createWareHouseRequest.getLocation());
        wareHouses.setMaxCapacity(createWareHouseRequest.getMaxCapacity());
        wareHouseRepository.save(wareHouses);
        return modelMapper.map(wareHouses, CreateWarehouseResponse.class);
    }

    public boolean delete(long id) throws Exception {
        WareHouses wareHouses = wareHouseRepository.findWaresHouseById(id);
        if (wareHouses.getCurrentCapacity() > 0 || wareHouses.getBookingCapacity() > 0) {
            return false;
        }
        wareHouses.setAvailable(false);
        wareHouseRepository.save(wareHouses);

        List<Orders> ordersList = orderRepository.findOrdersByNearWareHouse(wareHouses.getLocation());
        if (ordersList == null || ordersList.isEmpty()) {
            return true;
        }
        double[] currentWarehouseCoords = geoCodingService.geocoding(wareHouses.getLocation());
        List<String> wareHouseAllLocation = wareHouseRepository.findAllAvailableLocationsExcludingCurrent(wareHouses.getId());
        double minDistance = Double.MAX_VALUE;
        String wareHouseReplace = null;
        for (String location : wareHouseAllLocation) {
            double[] wareHouseCoords = geoCodingService.geocoding(location);
            String route = routingService.getDistanceFormattedRoute(currentWarehouseCoords[0], currentWarehouseCoords[1], wareHouseCoords[0], wareHouseCoords[1]);
            double distance = routingService.extractDistance(route);
            if (distance < minDistance && distance != -1) {
                minDistance = distance;
                wareHouseReplace = location;
            }
        }
        for (Orders orders : ordersList) {
            orders.setNearWareHouse(wareHouseReplace);
        }
        orderRepository.saveAll(ordersList);

        return true;
    }

    public void update(UpdateWareHouseRequest request){
        WareHouses wareHouses = wareHouseRepository.findWaresHouseById(request.getId());
        wareHouses.setMaxCapacity(request.getMaxCapacity());
        wareHouses.setAvailable(request.isAvailable());
        wareHouseRepository.save(wareHouses);
    }

    public List<AllWareHouseResponse> getAllWareHouseAvailable(){
        List<WareHouses> wareHouses = wareHouseRepository.findByIsAvailableTrue();
        return wareHouses
                .stream()
                .map(wareHouse->modelMapper.map(wareHouse, AllWareHouseResponse.class))
                .toList();
    }

    public List<AllWareHouseResponse> getAllWareHouseNotAvailable(){
        List<WareHouses> wareHouses = wareHouseRepository.findByIsAvailableFalse();
        return wareHouses
                .stream()
                .map(wareHouse->modelMapper.map(wareHouse, AllWareHouseResponse.class))
                .toList();
    }

    public boolean bookingSlot(long id, long orderId) {
        WareHouses wareHouse = wareHouseRepository.findById(id)
                .orElseThrow(()->new NotFoundException("Can not found WareHouse"));

        if (!wareHouse.isAvailable()) {
            throw new WareHouseException("WareHouse are not Available");
        }

        Orders order = orderRepository.findById(orderId)
                .orElseThrow(()->new OrderException("Can not found Order"));

        if(order.getWareHouses() != null){
            throw new OrderException("Order already book");
        }

        if(!wareHouse.getLocation().equals(order.getNearWareHouse())){
            throw new WareHouseException("Warehouse location does not match the order's nearest warehouse");
        }

        if(order.getOrderStatus() != OrderStatus.PAID){
            throw new WareHouseException("Not eligible for booking");
        }

        int slot = order.getTotalBox() + wareHouse.getBookingCapacity();

        if (slot <= wareHouse.getMaxCapacity()) {

            wareHouse.setBookingCapacity(slot);
            order.setWareHouses(wareHouse);
            order.setOrderStatus(OrderStatus.BOOKING);

            orderRepository.save(order);

            EmailDetail emailDetail = new EmailDetail();
            emailDetail.setSubject("Order Tracking Code");
            emailDetail.setUsers(order.getUsers());
            emailDetail.setCreateDate(new Date());
            emailDetail.setLink("http://transportkoifish.online/order-search");
            emailDetail.setOrderTracking(order.getTrackingOrder());
            emailService.sendEmailTrackingOrder(emailDetail);

            wareHouseRepository.save(wareHouse);

            return true;
        }
        return false;
    }

    public WareHouseContainOrderResponse getListOrderBookingInWareHouse(long wareHouseId){

        List<Orders> orderShippingInProgress = orderRepository.findByWareHouses_Id(wareHouseId)
                .stream()
                .filter(order -> {
                    List<Progresses> responseProgress = progressRepository.findProgressesByOrdersId(order.getId())
                            .orElse(Collections.emptyList());
                    if(responseProgress.isEmpty()){
                        return false;
                    }
                    Progresses latestProgress = responseProgress.get(responseProgress.size()-3);

                    return latestProgress == null;
                })
                .toList();
        List<Orders> orderBookingAndShippingNotInProgress = orderRepository.findByWareHouses_Id(wareHouseId)
                .stream()
                .filter(orders -> orders.getOrderStatus() == OrderStatus.BOOKING || orders.getOrderStatus() == OrderStatus.SHIPPING)
                .toList();

        List<Orders> order = Stream.concat(orderShippingInProgress.stream(), orderBookingAndShippingNotInProgress.stream())
                .sorted(Comparator.comparing(Orders::getOrderDate))
                .toList();

        int totalBox = order.stream().mapToInt(Orders::getTotalBox).sum();
        WareHouses wareHouses = wareHouseRepository.findWaresHouseById(wareHouseId);
        if(!wareHouses.isAvailable()){
            throw new WareHouseException("WareHouse not available");
        }
        WareHouseContainOrderResponse wareHouseContainOrderResponse = new WareHouseContainOrderResponse();
        wareHouseContainOrderResponse.setLocationWareHouse(wareHouses.getLocation());
        if(wareHouses.isAvailable()) {
            Set<AllOrderResponse> orderResponses = order.stream()
                    .map(orders -> modelMapper.map(orders, AllOrderResponse.class))
                    .collect(Collectors.toSet());


            wareHouseContainOrderResponse.setDescription("Orders have been booking in warehouse");
            wareHouseContainOrderResponse.setTotalBox(totalBox);
            wareHouseContainOrderResponse.setOrders(orderResponses);

            return wareHouseContainOrderResponse;
        }
        return wareHouseContainOrderResponse;
    }

    public WareHouseContainOrderResponse getListOrderInWareHouse(long wareHouseId) {
        List<Orders> responseOrder = orderRepository.findOrdersByWareHouseIdThroughProgress(wareHouseId)
                .stream()
                .filter(order -> {
                    List<Progresses> responseProgress = progressRepository.findProgressesByOrdersId(order.getId())
                            .orElseThrow(() -> new ProgressException("Progress not created yet"));
                    boolean hasCanceled = responseProgress.stream()
                            .anyMatch(progress -> progress.getProgressStatus() == ProgressStatus.CANCELED);
                    if (hasCanceled) {
                        return false;
                    }
                    List<Progresses> filteredProgress = responseProgress.stream()
                            .filter(progress -> progress.getProgressStatus() != ProgressStatus.CANCELED)
                            .toList();
                    Progresses lastProgress = filteredProgress.get(filteredProgress.size() - 1);
                    return lastProgress.getProgressStatus() == null;
                })
                .sorted(Comparator.comparing(Orders::getOrderDate))
                .toList();
        int totalBox = responseOrder.stream().mapToInt(Orders::getTotalBox).sum();
        WareHouses wareHouses = wareHouseRepository.findWaresHouseById(wareHouseId);
        if(!wareHouses.isAvailable()){
            throw new WareHouseException("WareHouse not available");
        }
        WareHouseContainOrderResponse wareHouseContainOrderResponse = new WareHouseContainOrderResponse();
        wareHouseContainOrderResponse.setLocationWareHouse(wareHouses.getLocation());
        if(wareHouses.isAvailable()) {
            Set<AllOrderResponse> orderResponses = responseOrder.stream()
                    .map(order -> modelMapper.map(order, AllOrderResponse.class))
                    .collect(Collectors.toSet());


            wareHouseContainOrderResponse.setDescription("Orders have arrived at the Warehouse");
            wareHouseContainOrderResponse.setTotalBox(totalBox);
            wareHouseContainOrderResponse.setOrders(orderResponses);

            return wareHouseContainOrderResponse;
        }
        return wareHouseContainOrderResponse;
    }

    public String suggestWareHouse(long orderId){
        Orders orders = orderRepository.findOrdersById(orderId);
        WareHouses wareHouses = wareHouseRepository.findWareHousesByLocation(orders.getNearWareHouse());
        if(!wareHouses.isAvailable()){
            throw new WareHouseException("WareHouse is not available");
        }
        return wareHouses.getLocation();
    }
}

