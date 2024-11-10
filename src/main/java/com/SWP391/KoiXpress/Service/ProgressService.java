package com.SWP391.KoiXpress.Service;

import com.SWP391.KoiXpress.Entity.*;
import com.SWP391.KoiXpress.Entity.Enum.*;
import com.SWP391.KoiXpress.Exception.NotFoundException;
import com.SWP391.KoiXpress.Exception.OrderException;
import com.SWP391.KoiXpress.Exception.ProgressException;
import com.SWP391.KoiXpress.Model.request.Progress.DeleteProgressRequest;
import com.SWP391.KoiXpress.Model.request.Progress.ProgressRequest;
import com.SWP391.KoiXpress.Model.request.Progress.UpdateProgressRequest;
import com.SWP391.KoiXpress.Model.response.Progress.ProgressResponse;
import com.SWP391.KoiXpress.Model.response.Progress.UpdateProgressResponse;
import com.SWP391.KoiXpress.Repository.OrderRepository;
import com.SWP391.KoiXpress.Repository.ProgressRepository;
import com.SWP391.KoiXpress.Repository.UserRepository;
import com.SWP391.KoiXpress.Repository.WareHouseRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProgressService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderService orderService;

    @Autowired
    ProgressRepository progressRepository;

    @Autowired
    WareHouseRepository wareHouseRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    EmailService emailService;

    @Autowired
    WareHouseService wareHouseService;


    public List<ProgressResponse> create(ProgressRequest progressRequest) {
        Orders orders = orderService.getOrderById(progressRequest.getOrderId());
        List<Progresses> existProgressesOrder = progressRepository.findProgressesByOrdersId(orders.getId()).orElseThrow();
        if (!existProgressesOrder.isEmpty()) {
            throw new ProgressException("Order already in another Progress");
        }
        if (orders.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new ProgressException("Order had been delivered");
        }
        if (orders.getOrderStatus() == OrderStatus.BOOKING) {
            List<ProgressResponse> progresses = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                Progresses progress = new Progresses();
                orders.setOrderStatus(OrderStatus.SHIPPING);
                progress.setOrders(orders);
                progressRepository.save(progress);
                ProgressResponse response = new ProgressResponse();
                response.setId(progress.getId());
                progresses.add(response);
            }
            return progresses;
        }
        throw new ProgressException("Order is not ready to ship");
    }

    public List<ProgressResponse> findProgressesByOrderId(long orderId) {
        Orders orders = orderService.getOrderById(orderId);
        List<Progresses> progresses = progressRepository.findProgressesByOrdersId(orders.getId()).orElseThrow(() -> new ProgressException("Order do not have progress yet"));
        return progresses.stream().map(progress -> modelMapper.map(progress, ProgressResponse.class)).toList();
    }

    public List<ProgressResponse> trackingOrder(UUID trackingOrder) {
        List<Progresses> progresses = progressRepository.findProgressesByTrackingOrderAndStatusNotNull(trackingOrder);
            return progresses.stream()
                    .map(progress -> modelMapper.map(progress, ProgressResponse.class))
                    .collect(Collectors.toList());
    }


    public UpdateProgressResponse update(long id, UpdateProgressRequest updateProgressRequest) {

        // Retrieve the existing progress entry, or throw an exception if not found
        Progresses oldProgresses = progressRepository.findById(id)
                .orElseThrow(() -> new ProgressException("Cannot find progress"));

        // Retrieve the associated order, or throw an exception if not found
        Orders orders = orderRepository.findById(oldProgresses.getOrders().getId())
                .orElseThrow(() -> new OrderException("Cannot find order"));

        // Retrieve the delivery user, or throw an exception if not found
        Users delivery = userRepository.findUsersByIdAndRoleAndIsDeleted(id, Role.DELIVERING_STAFF, false)
                .orElseThrow(() -> new NotFoundException("Cannot find delivery staff"));

        // Retrieve warehouse based on order's nearby warehouse
        WareHouses wareHouses = wareHouseRepository.findWareHousesByLocation(orders.getNearWareHouse());

        // Check warehouse availability
        if (wareHouses == null || !wareHouses.isAvailable()) {
            throw new ProgressException("Cannot update - Warehouse unavailable.");
        }

        // Update basic progress details
        oldProgresses.setDateProgress(new Date());
        oldProgresses.setImage(updateProgressRequest.getImage());
        oldProgresses.setProgressStatus(updateProgressRequest.getProgressStatus());
        oldProgresses.setInProgress(updateProgressRequest.getProgressStatus() != null);

        // Handle different progress statuses
        switch (updateProgressRequest.getProgressStatus()) {
            case ON_SITE:
                handleOnSiteProgress(oldProgresses, orders);
                break;

            case FISH_CHECKED:
                handleFishCheckedProgress(oldProgresses, orders, updateProgressRequest);
                break;

            case WAREHOUSING:
                handleWarehousingProgress(oldProgresses, orders, wareHouses);
                break;

            case EN_ROUTE:
                handleEnRouteProgress(oldProgresses, orders, wareHouses, delivery);
                break;

            case HANDED_OVER:
                handleHandedOverProgress(orders);
                break;

            default:
                throw new ProgressException("Invalid progress status");
        }

        // Save updates to order, warehouse, and progress
        orderRepository.save(orders);
        wareHouseRepository.save(wareHouses);
        progressRepository.save(oldProgresses);

        // Map updated progress to response
        return modelMapper.map(oldProgresses, UpdateProgressResponse.class);
    }

    private void handleOnSiteProgress(Progresses progress, Orders order) {
        progress.setFrom_Location(null);
        progress.setTo_Location(order.getOriginLocation());
        progress.setVehicleType(VehicleType.GO_TO_ORIGIN_LOCATION);
    }

    private void handleFishCheckedProgress(Progresses progress, Orders order, UpdateProgressRequest request) {
        progress.setHealthFishStatus(request.getHealthFishStatus());
        if (request.getHealthFishStatus() == HealthFishStatus.UNHEALTHY) {
            progress.setProgressStatus(ProgressStatus.CANCELED);
            order.setOrderStatus(OrderStatus.CANCELED);
        }
    }

    private void handleWarehousingProgress(Progresses progress, Orders order, WareHouses warehouse) {
        progress.setFrom_Location(order.getOriginLocation());
        progress.setTo_Location(warehouse.getLocation());
        progress.setVehicleType(VehicleType.TRANSPORT_TO_WAREHOUSE);
        progress.setWareHouses(warehouse);
    }

    private void handleEnRouteProgress(Progresses progress, Orders order, WareHouses warehouse, Users delivery) {
        progress.setFrom_Location(warehouse.getLocation());
        progress.setTo_Location(order.getRecipientInfo());
        progress.setDelivery_name(delivery.getFullname());
        progress.setDelivery_phone(delivery.getPhone());
        progress.setVehicleType(VehicleType.DELIVERY_TO_RECEIVER);
        warehouse.setCurrentCapacity(warehouse.getCurrentCapacity() - order.getTotalBox());
        progress.setWareHouses(warehouse);
    }

    private void handleHandedOverProgress(Orders order) {
        order.setOrderStatus(OrderStatus.DELIVERED);
        sendThankYouEmail(order);
    }

    private void sendThankYouEmail(Orders order) {
        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setSubject("Thank You");
        emailDetail.setUsers(order.getUsers());
        emailDetail.setLink("#"); // Placeholder for actual link
        emailService.sendEmailThankYou(emailDetail);
    }

    public void delete(long id, DeleteProgressRequest reason) {
        Progresses progresses = progressRepository.findProgressesById(id);
        progresses.setInProgress(false);
        progresses.setProgressStatus(ProgressStatus.CANCELED);
        progresses.setFailure_reason(reason.getReason());
        progresses.getOrders().setOrderStatus(OrderStatus.CANCELED);
        progressRepository.save(progresses);
    }

}