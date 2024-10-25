package com.SWP391.KoiXpress.Service;

import com.SWP391.KoiXpress.Entity.*;
import com.SWP391.KoiXpress.Entity.Enum.HealthFishStatus;
import com.SWP391.KoiXpress.Entity.Enum.OrderStatus;
import com.SWP391.KoiXpress.Entity.Enum.ProgressStatus;
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
        Progresses oldProgresses = progressRepository.findProgressesById(id);
        Orders orders = orderRepository.findOrdersById(oldProgresses.getOrders().getId());
        WareHouses wareHouses = wareHouseRepository.findWareHousesByLocation(orders.getNearWareHouse());

        if (wareHouses != null && wareHouses.isAvailable()) {
            oldProgresses.setDateProgress(new Date());
            oldProgresses.setHealthFishStatus(updateProgressRequest.getHealthFishStatus());
            if(updateProgressRequest.getHealthFishStatus()== HealthFishStatus.UNHEALTHY){
                oldProgresses.setProgressStatus(ProgressStatus.CANCELED);
                progressRepository.save(oldProgresses);
                return modelMapper.map(oldProgresses, UpdateProgressResponse.class);
            }
            oldProgresses.setImage(updateProgressRequest.getImage());
            oldProgresses.setProgressStatus(updateProgressRequest.getProgressStatus());
            if(updateProgressRequest.getProgressStatus() != null){
                oldProgresses.setInProgress(true);
            }
            oldProgresses.setWareHouses(wareHouses);
            wareHouseRepository.save(wareHouses);
            progressRepository.save(oldProgresses);
            return modelMapper.map(oldProgresses, UpdateProgressResponse.class);
        }
        throw new ProgressException("Can not update");
    }

    public DeleteProgressResponse delete(long id) {
        Progresses progresses = progressRepository.findProgressesById(id);
        progresses.setInProgress(false);
        progresses.setProgressStatus(ProgressStatus.CANCELED);
        progressRepository.save(progresses);
        return modelMapper.map(progresses, DeleteProgressResponse.class);
    }

}