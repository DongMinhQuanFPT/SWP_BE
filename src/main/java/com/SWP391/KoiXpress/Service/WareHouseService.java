package com.SWP391.KoiXpress.Service;

import com.SWP391.KoiXpress.Entity.EmailDetail;
import com.SWP391.KoiXpress.Entity.Enum.OrderStatus;
import com.SWP391.KoiXpress.Entity.Orders;
import com.SWP391.KoiXpress.Entity.WareHouses;
import com.SWP391.KoiXpress.Exception.NotFoundException;
import com.SWP391.KoiXpress.Exception.OrderException;
import com.SWP391.KoiXpress.Exception.WareHouseException;
import com.SWP391.KoiXpress.Model.request.WareHouse.CreateWareHouseRequest;
import com.SWP391.KoiXpress.Model.response.WareHouse.AllWareHouseResponse;
import com.SWP391.KoiXpress.Model.response.WareHouse.CreateWarehouseResponse;
import com.SWP391.KoiXpress.Model.response.WareHouse.UpdateWareHouseRequest;
import com.SWP391.KoiXpress.Repository.OrderRepository;
import com.SWP391.KoiXpress.Repository.WareHouseRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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

    public CreateWarehouseResponse create(CreateWareHouseRequest createWareHouseRequest){
        WareHouses wareHouses = new WareHouses();
        wareHouses.setLocation(createWareHouseRequest.getLocation());
        wareHouses.setMaxCapacity(createWareHouseRequest.getMaxCapacity());
        wareHouseRepository.save(wareHouses);
        return modelMapper.map(wareHouses, CreateWarehouseResponse.class);
    }

    public boolean delete(long id){
        WareHouses wareHouses = wareHouseRepository.findWaresHouseById(id);
        if(wareHouses.getCurrentCapacity() == 0){
            wareHouses.setAvailable(false);
            wareHouseRepository.save(wareHouses);
            return true;
        }
        return false;
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

        if(!wareHouse.getLocation().equals(order.getNearWareHouse())){
            throw new WareHouseException("Warehouse location does not match the order's nearest warehouse");
        }

        if(order.getOrderStatus()!= OrderStatus.PAID){
            throw new WareHouseException("Not eligible for booking");
        }

        int slot = order.getTotalBox() + wareHouse.getCurrentCapacity();

        if (slot <= wareHouse.getMaxCapacity()) {

            wareHouse.setCurrentCapacity(slot);
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
}

