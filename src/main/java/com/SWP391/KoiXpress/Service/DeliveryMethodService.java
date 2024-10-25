package com.SWP391.KoiXpress.Service;

import com.SWP391.KoiXpress.Entity.DeliveryMethods;
import com.SWP391.KoiXpress.Exception.NotFoundException;
import com.SWP391.KoiXpress.Model.request.DeliveryMethod.CreateDeliveryMethodRequest;
import com.SWP391.KoiXpress.Model.request.DeliveryMethod.UpdateDeliveryMethodRequest;
import com.SWP391.KoiXpress.Repository.DeliveryMethodRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveryMethodService {

    @Autowired
    DeliveryMethodRepository deliveryMethodRepository;

    @Autowired
    ModelMapper modelMapper;

    public DeliveryMethods create(CreateDeliveryMethodRequest createDeliveryMethodRequest){
        DeliveryMethods deliveryMethods = modelMapper.map(createDeliveryMethodRequest, DeliveryMethods.class);
        return deliveryMethodRepository.save(deliveryMethods);
    }
    public DeliveryMethods update(long id, UpdateDeliveryMethodRequest updateDeliveryMethodRequest){
        DeliveryMethods oldDelivery = findDeliveryById(id);
        oldDelivery.setTypeVehicle(updateDeliveryMethodRequest.getTypeVehicle());
        oldDelivery.setPrice(updateDeliveryMethodRequest.getPrice());
        return deliveryMethodRepository.save(oldDelivery);
    }
    public void delete(long id){
        DeliveryMethods deliveryMethods = findDeliveryById(id);
        deliveryMethodRepository.delete(deliveryMethods);
    }

    public List<DeliveryMethods> getAll(){
        List<DeliveryMethods> deliveryMethods = deliveryMethodRepository.findAll();
        return deliveryMethods;
    }

    private DeliveryMethods findDeliveryById(long id){
        DeliveryMethods deliveryMethods = deliveryMethodRepository.findDeliveryMethodsById(id);
        if(deliveryMethods != null){
            return deliveryMethods;
        }
        else{
            throw new NotFoundException("Can not found DeliveryMethod");
        }
    }
}
