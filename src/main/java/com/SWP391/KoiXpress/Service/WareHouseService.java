package com.SWP391.KoiXpress.Service;

import com.SWP391.KoiXpress.Entity.WareHouses;
import com.SWP391.KoiXpress.Model.request.WareHouse.CreateWareHouseRequest;
import com.SWP391.KoiXpress.Model.response.WareHouse.CreateWarehouseResponse;
import com.SWP391.KoiXpress.Repository.OrderRepository;
import com.SWP391.KoiXpress.Repository.WareHouseRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WareHouseService {

    @Autowired
    WareHouseRepository wareHouseRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ModelMapper modelMapper;

    public CreateWarehouseResponse create(CreateWareHouseRequest createWareHouseRequest){
        WareHouses wareHouses = new WareHouses();
        wareHouses.setLocation(createWareHouseRequest.getLocation());
        wareHouseRepository.save(wareHouses);
        return modelMapper.map(wareHouses, CreateWarehouseResponse.class);
    }

    public void delete(long id){
        WareHouses wareHouses = wareHouseRepository.findWaresHouseById(id);
        wareHouses.setAvailable(false);
        wareHouseRepository.save(wareHouses);
    }

    public List<WareHouses> getAllWareHouseAvailable(){
        return wareHouseRepository.findByIsAvailableTrue();
    }

    public List<WareHouses> getAllWareHouseNotAvailable(){
        return wareHouseRepository.findByIsAvailableFalse();
    }
}
