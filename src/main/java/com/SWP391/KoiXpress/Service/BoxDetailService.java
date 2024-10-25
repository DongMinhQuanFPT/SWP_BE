package com.SWP391.KoiXpress.Service;

import com.SWP391.KoiXpress.Entity.Boxes;
import com.SWP391.KoiXpress.Entity.BoxDetails;
import com.SWP391.KoiXpress.Entity.OrderDetails;
import com.SWP391.KoiXpress.Model.response.Box.AllBoxDetailResponse;
import com.SWP391.KoiXpress.Model.response.Box.CreateBoxDetailResponse;
import com.SWP391.KoiXpress.Exception.BoxException;
import com.SWP391.KoiXpress.Model.response.Paging.PagedResponse;
import com.SWP391.KoiXpress.Repository.BoxDetailRepository;
import com.SWP391.KoiXpress.Repository.BoxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BoxDetailService {

    @Autowired
    BoxDetailRepository boxDetailRepository;

    @Autowired
    BoxRepository boxRepository;


    static final Map<String, Double> FISH_SIZES = new HashMap<>();

    static {
        FISH_SIZES.put("19.9", 10.0);
        FISH_SIZES.put("20-25", 15.0);
        FISH_SIZES.put("25.1-30", 20.0);
        FISH_SIZES.put("30.1-40", 30.0);
        FISH_SIZES.put("40.1-44", 40.0);
        FISH_SIZES.put("44.1-50", 50.0);
        FISH_SIZES.put("50.1-55", 60.0);
        FISH_SIZES.put("55.1-65", 70.0);
        FISH_SIZES.put("65.1-70", 75.0);
        FISH_SIZES.put("70.1-75", 80.0);
        FISH_SIZES.put("75.1-80", 90.0);
        FISH_SIZES.put("80.1-83", 100.0);
    }

    public double getFishVolume(int quantity, double size) {
        if (quantity <= 0) {
            throw new BoxException("Invalid box ");

        }
        if (size < 19.9 || size > 83) {
            throw new BoxException("Invalid fish size (20-83).");
        }

        double total = 0;
        for (Map.Entry<String, Double> entry : FISH_SIZES.entrySet()) {
            String sizeRange = entry.getKey();
            double volume = entry.getValue();

            String[] bounds = sizeRange.split("-");
            if (bounds.length == 1) { // Single number
                if (Double.parseDouble(bounds[0]) == size) {
                    total = volume * quantity;
                }
            } else { // Range
                double lower = Double.parseDouble(bounds[0]);
                double upper = Double.parseDouble(bounds[1]);

                if (size >= lower && size <= upper) {
                    total = volume * quantity;
                }
            }
        }
        return total;
    }

    public Map<String, Object> calculateBox(Map<Double, Integer> fishSizeQuantityMap) {
        double remainVolume = 0;
        double totalVolume = 0;
        double totalPrice = 0;
        double remainingSpaceInSmallBox = 0;
        int totalCount = 0;

        // Duyệt qua từng cặp kích thước-số lượng và cộng dồn thể tích tổng
        for (Map.Entry<Double, Integer> entry : fishSizeQuantityMap.entrySet()) {
            double fishSize = entry.getKey();
            int quantity = entry.getValue();
            totalVolume += getFishVolume(quantity, fishSize);
            remainVolume += getFishVolume(quantity, fishSize);
        }

        List<Boxes> boxes = boxRepository.findAll(Sort.by(Sort.Order.desc("volume")));
        Boxes smallestBoxes = boxes.get(boxes.size()-1);

        Map<String, Integer> boxCount = new LinkedHashMap<>();

        for(Boxes box : boxes){
            boxCount.put(box.getType(),0);
        }

        for(Boxes box : boxes){
            int count = (int) (remainVolume / box.getVolume());
            remainVolume -= count * box.getVolume();
            boxCount.put(box.getType(), boxCount.get(box.getType())+ count);
            if (remainVolume == 0) {
                break;
            }
        }
        if(remainVolume > 0 && remainVolume < smallestBoxes.getVolume()){
            boxCount.put(smallestBoxes.getType(), boxCount.get(smallestBoxes.getType()) + 1);
            remainingSpaceInSmallBox = smallestBoxes.getVolume() - remainVolume;
        }

        for (Boxes box : boxes) {
            totalPrice += box.getPrice() * boxCount.get(box.getType());
            totalCount += boxCount.get(box.getType());
        }
        Map<String, Object> boxDetails = new LinkedHashMap<>();

        for(Map.Entry<String, Integer> entry : boxCount.entrySet()){
            String boxType = entry.getKey();
            int quantityBox = entry.getValue();
            boxDetails.put(boxType,quantityBox);
        }
        boxDetails.put("totalCount", totalCount);
        boxDetails.put("totalVolume", totalVolume);
        boxDetails.put("totalPrice",totalPrice);
        boxDetails.put("remainingVolume", remainingSpaceInSmallBox);

        return boxDetails;
    }

    public List<String> suggestFishSizes(double remainVolume){
        List<String> suggestions = new ArrayList<>();
        List<Map.Entry<String, Double>> sortedFishSizes = FISH_SIZES.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .toList();

        for (Map.Entry<String, Double> entry : sortedFishSizes) {
            double volume = entry.getValue();
            if (volume <= remainVolume) {
                int fishQuantity = (int) (remainVolume / volume);
                suggestions.add("Quantity can be added: " + fishQuantity + ", Size: "+ entry.getKey());
            }
        }


        return suggestions;
    }

    public Map<String, Object> calculateBoxAndSuggestFishSizes(Map<Double, Integer> fishSizeQuantityMap) {
        Map<String, Object> boxDetails = calculateBox(fishSizeQuantityMap);
        double remainVolume = (double) boxDetails.get("remainingVolume");
        List<String> suggestions = suggestFishSizes(remainVolume);
        // Thêm gợi ý vào chi tiết hộp
        boxDetails.put("suggestions", suggestions);
        return boxDetails;
    }

    public CreateBoxDetailResponse createBox(Map<Double, Integer> fishSizeQuantityMap, OrderDetails orderDetails){
        try{
            Map<String, Object> boxDetails = calculateBox(fishSizeQuantityMap);
            List<BoxDetails> boxDetailsList = new ArrayList<>();
            double totalVolume = (double) boxDetails.get("totalVolume");
            double totalPrice = (double) boxDetails.get("totalPrice");
            int totalCount = (int) boxDetails.get("totalCount");
            for(Map.Entry<String, Object> entry : boxDetails.entrySet()){
                String boxType = entry.getKey();
                Object value = entry.getValue();
                if (boxType.equals("totalVolume") || boxType.equals("totalPrice") || boxType.equals("remainingVolume") || boxType.equals("totalCount")) {
                    continue;
                }
                Integer quantityBox = (Integer) value;
                Boxes boxes = boxRepository.findBoxesByType(boxType);
                if(boxes != null){
                    BoxDetails boxDetail = new BoxDetails();
                    boxDetail.setBoxes(boxes);
                    boxDetail.setQuantity(quantityBox);
                    boxDetail.setOrderDetails(orderDetails);
                    boxDetailRepository.save(boxDetail);
                    boxDetailsList.add(boxDetail);
                }
            }
            CreateBoxDetailResponse createBoxDetailResponse = new CreateBoxDetailResponse();
            createBoxDetailResponse.setBoxDetails(boxDetailsList);
            createBoxDetailResponse.setTotalPrice(totalPrice);
            createBoxDetailResponse.setTotalVolume(totalVolume);
            createBoxDetailResponse.setTotalCount(totalCount);

            return createBoxDetailResponse;
        }catch(Exception e){
            throw new BoxException("BoxDetail cant create");
        }
    }

    public PagedResponse<AllBoxDetailResponse> getAllBox(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<BoxDetails> boxDetails = boxDetailRepository.findAll(pageRequest);

        if (boxDetails.isEmpty()) {
            return new PagedResponse<>(Collections.emptyList(), page, size, 0, 0, true);
        }
        List<AllBoxDetailResponse> boxDetailResponses = boxDetails.stream()
                .map(boxDetail -> new AllBoxDetailResponse(
                        boxDetail.getId(),
                        boxDetail.getQuantity(),
                        boxDetail.getOrderDetails(),
                        boxDetail.getBoxes()
                ))
                .collect(Collectors.toList());
        return new PagedResponse<>(boxDetailResponses, page, size, boxDetails.getTotalElements(), boxDetails.getTotalPages(), boxDetails.isLast());
    }
}
