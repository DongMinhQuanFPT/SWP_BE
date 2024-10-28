package com.SWP391.KoiXpress.Service;

import com.SWP391.KoiXpress.Entity.Boxes;
import com.SWP391.KoiXpress.Exception.BoxException;
import com.SWP391.KoiXpress.Model.request.Box.CreateBoxRequest;
import com.SWP391.KoiXpress.Model.request.Box.UpdateBoxRequest;
import com.SWP391.KoiXpress.Model.response.Box.CreateBoxResponse;
import com.SWP391.KoiXpress.Repository.BoxRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
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

    public Boxes update(long id, UpdateBoxRequest updateBoxRequest){
        Boxes boxes = findBoxById(id);
        boxes.setType(updateBoxRequest.getType());
        boxes.setVolume(updateBoxRequest.getVolume());
        boxes.setPrice(updateBoxRequest.getPrice());
        boxRepository.save(boxes);
        return boxes;
    }

    public List<Boxes> getAllBox(){
        return boxRepository.findAll().stream()
                .sorted(Comparator.comparing(Boxes::getVolume))
                .toList();
    }

    public List<Boxes> getAvailableBox(){
        return boxRepository.findAll()
                .stream()
                .filter(Boxes::isAvailable)
                .sorted(Comparator.comparing(Boxes::getVolume))
                .toList();
    }

    public List<Boxes> getNotAvailableBox(){
        return boxRepository.findAll()
                .stream()
                .filter(boxes -> !boxes.isAvailable())
                .sorted(Comparator.comparing(Boxes::getVolume))
                .toList();
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
