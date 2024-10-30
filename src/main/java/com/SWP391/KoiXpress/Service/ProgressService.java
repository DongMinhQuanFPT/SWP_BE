package com.SWP391.KoiXpress.Service;

import com.SWP391.KoiXpress.Entity.*;
import com.SWP391.KoiXpress.Entity.Enum.HealthFishStatus;
import com.SWP391.KoiXpress.Entity.Enum.OrderStatus;
import com.SWP391.KoiXpress.Entity.Enum.ProgressStatus;
import com.SWP391.KoiXpress.Exception.OrderException;
import com.SWP391.KoiXpress.Exception.ProgressException;
import com.SWP391.KoiXpress.Model.request.Progress.ProgressRequest;
import com.SWP391.KoiXpress.Model.request.Progress.UpdateProgressRequest;
import com.SWP391.KoiXpress.Model.response.Progress.DeleteProgressResponse;
import com.SWP391.KoiXpress.Model.response.Progress.ProgressResponse;
import com.SWP391.KoiXpress.Model.response.Progress.UpdateProgressResponse;
import com.SWP391.KoiXpress.Repository.OrderRepository;
import com.SWP391.KoiXpress.Repository.ProgressRepository;
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
        if (orders.getOrderStatus() == OrderStatus.SHIPPING) {
            List<ProgressResponse> progresses = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                Progresses progress = new Progresses();
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

    public List<ProgressResponse> findProgressesByOrderId(long orderId){
        Orders orders = orderService.getOrderById(orderId);
        List<Progresses> progresses = progressRepository.findProgressesByOrdersId(orders.getId()).orElseThrow(()->new ProgressException("Order do not have progress yet"));
        return progresses.stream().map(progress -> modelMapper.map(progress, ProgressResponse.class)).toList();
    }

    public List<ProgressResponse> trackingOrder(UUID trackingOrder) {
        List<Progresses> progresses = progressRepository.findProgressesByTrackingOrderAndStatusNotNull(trackingOrder);
        if (progresses != null) {
            return progresses.stream().map(progress -> modelMapper.map(progress, ProgressResponse.class)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public UpdateProgressResponse update(long id, UpdateProgressRequest updateProgressRequest) {
        Progresses oldProgresses = progressRepository.findById(id).orElseThrow(()-> new ProgressException("Can not find progress"));
        Orders orders = orderRepository.findById(oldProgresses.getOrders().getId()).orElseThrow(()-> new OrderException("Can not find order"));
        WareHouses wareHouses = wareHouseRepository.findWareHousesByLocation(orders.getNearWareHouse());

        if (wareHouses == null || !wareHouses.isAvailable()) {
            throw new ProgressException("Cannot update - Warehouse unavailable.");
        }

        // Cập nhật các trường chính
        oldProgresses.setDateProgress(new Date());
        oldProgresses.setHealthFishStatus(updateProgressRequest.getHealthFishStatus());

        // Kiểm tra tình trạng sức khỏe cá
        if (updateProgressRequest.getHealthFishStatus() == HealthFishStatus.UNHEALTHY) {
            oldProgresses.setProgressStatus(ProgressStatus.CANCELED);
        } else {
            oldProgresses.setImage(updateProgressRequest.getImage());
            oldProgresses.setProgressStatus(updateProgressRequest.getProgressStatus());
            if(updateProgressRequest.getProgressStatus() == ProgressStatus.WAREHOUSING){
                oldProgresses.setWareHouses(wareHouses);
            }
            if(updateProgressRequest.getProgressStatus() == ProgressStatus.CANCELED){
                orders.setOrderStatus(OrderStatus.CANCELED);
            }
            if (updateProgressRequest.getProgressStatus() == ProgressStatus.HANDED_OVER) {
                orders.setOrderStatus(OrderStatus.DELIVERED);

                EmailDetail emailDetail = new EmailDetail();
                emailDetail.setSubject("Thank You");
                emailDetail.setUsers(orders.getUsers());
                emailDetail.setLink("#");

                emailService.sendEmailThankYou(emailDetail);

                orderRepository.save(orders); // Lưu cập nhật đơn hàng
            }

            oldProgresses.setInProgress(updateProgressRequest.getProgressStatus() != null);
        }

        // Cập nhật thông tin kho hàng và lưu progress

        wareHouseRepository.save(wareHouses);
        progressRepository.save(oldProgresses);

        return modelMapper.map(oldProgresses, UpdateProgressResponse.class);
    }


    public DeleteProgressResponse delete(long id) {
        Progresses progresses = progressRepository.findProgressesById(id);
        progresses.setInProgress(false);
        progresses.setProgressStatus(ProgressStatus.CANCELED);
        progressRepository.save(progresses);
        return modelMapper.map(progresses, DeleteProgressResponse.class);
    }

}