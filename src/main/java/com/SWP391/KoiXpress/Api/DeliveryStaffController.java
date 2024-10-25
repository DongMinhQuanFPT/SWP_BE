package com.SWP391.KoiXpress.Api;

import com.SWP391.KoiXpress.Model.request.Order.UpdateOrderRequest;
import com.SWP391.KoiXpress.Model.request.Progress.ProgressRequest;
import com.SWP391.KoiXpress.Model.request.Progress.UpdateProgressRequest;
import com.SWP391.KoiXpress.Model.response.Order.UpdateOrderResponse;
import com.SWP391.KoiXpress.Model.response.Progress.DeleteProgressResponse;
import com.SWP391.KoiXpress.Model.response.Progress.ProgressResponse;
import com.SWP391.KoiXpress.Model.response.Progress.UpdateProgressResponse;
import com.SWP391.KoiXpress.Service.OrderService;
import com.SWP391.KoiXpress.Service.ProgressService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/delivery")
@CrossOrigin("*")
@SecurityRequirement(name="api")
@PreAuthorize("hasAuthority('DELIVERING_STAFF')")
public class DeliveryStaffController {

    @Autowired
    ProgressService progressService;

    @Autowired
    OrderService orderService;

    @PostMapping("/progress")
    public ResponseEntity<List<ProgressResponse>> create(@Valid @RequestBody ProgressRequest progressRequest){
        List<ProgressResponse> progresses = progressService.create(progressRequest);
        return ResponseEntity.ok(progresses);
    }

    @PutMapping("{progressId}")
    public ResponseEntity<UpdateProgressResponse> update(@PathVariable long progressId,@Valid @RequestBody UpdateProgressRequest updateProgressRequest){
        UpdateProgressResponse updateProgressResponse = progressService.update(progressId, updateProgressRequest);
        return ResponseEntity.ok(updateProgressResponse);
    }

    @DeleteMapping("{progressId}")
    public ResponseEntity<DeleteProgressResponse> delete(@PathVariable long progressId){
        DeleteProgressResponse deleteProgressResponse = progressService.delete(progressId);
        return ResponseEntity.ok(deleteProgressResponse);
    }

    @GetMapping("/progress/{orderId}")
    public ResponseEntity<List<ProgressResponse>> getProgressByOrderId(@PathVariable long orderId){
        List<ProgressResponse> progressResponses = progressService.findProgressesByOrderId(orderId);
        return ResponseEntity.ok(progressResponses);
    }

    @PutMapping("/order/{orderId}")
    public ResponseEntity<UpdateOrderResponse> updateOrderByDelivery(@PathVariable long orderId, @Valid @RequestBody UpdateOrderRequest updateOrderRequest){
        UpdateOrderResponse updateOrderByDelivery= orderService.updateOrderByDelivery(orderId, updateOrderRequest);
        return ResponseEntity.ok(updateOrderByDelivery);
    }

}
