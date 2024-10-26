package com.SWP391.KoiXpress.Service;

import com.SWP391.KoiXpress.Entity.Boxes;
import com.SWP391.KoiXpress.Exception.BoxException;
import com.SWP391.KoiXpress.Model.request.Box.CreateBoxRequest;
import com.SWP391.KoiXpress.Model.response.Box.CreateBoxResponse;
import com.SWP391.KoiXpress.Repository.BoxRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoxService {

    @Autowired
    BoxRepository boxRepository;

    @Autowired
    ModelMapper modelMapper;

    public CreateBoxResponse create(CreateBoxRequest createBoxRequest){
        Boxes boxes = new Boxes();
        boxes.setType(createBoxRequest.getType());
        boxes.setVolume(createBoxRequest.getVolume());
        boxes.setPrice(createBoxRequest.getPrice());
        boxes.setAvailable(true);
        boxRepository.save(boxes);
        return modelMapper.map(boxes, CreateBoxResponse.class);
    }

    public void delete(long id){
        Boxes boxes = findBoxById(id);
        boxes.setAvailable(false);
        boxRepository.save(boxes);
    }

    public List<Boxes> getAllBox(){
        return boxRepository.findAll();
    }

    private Boxes findBoxById(long id){
        Boxes boxes = boxRepository.findBoxesById(id);
        if(boxes != null){
            return boxes;
        }else{
            throw new BoxException("Box doesn't exist");
        }
    }
}
