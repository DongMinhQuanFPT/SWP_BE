package com.SWP391.KoiXpress.Controller;

import com.SWP391.KoiXpress.Exception.ProgressException;
import com.SWP391.KoiXpress.Model.request.Order.UpdateOrderRequest;
import com.SWP391.KoiXpress.Model.request.Progress.DeleteProgressRequest;
import com.SWP391.KoiXpress.Model.request.Progress.ProgressRequest;
import com.SWP391.KoiXpress.Model.request.Progress.UpdateProgressRequest;
import com.SWP391.KoiXpress.Model.request.Vehicle.CreateVehicleRequest;
import com.SWP391.KoiXpress.Model.request.Vehicle.LoadOrderToVehicleRequest;
import com.SWP391.KoiXpress.Model.request.Vehicle.UpdateVehicleRequest;
import com.SWP391.KoiXpress.Model.response.Order.AllOrderResponse;
import com.SWP391.KoiXpress.Model.response.Order.UpdateOrderResponse;
import com.SWP391.KoiXpress.Model.response.Progress.ProgressResponse;
import com.SWP391.KoiXpress.Model.response.Progress.UpdateProgressResponse;
import com.SWP391.KoiXpress.Model.response.User.EachUserResponse;
import com.SWP391.KoiXpress.Model.response.Vehicle.AllVehicleResponse;
import com.SWP391.KoiXpress.Model.response.Vehicle.CreateVehicleResponse;
import com.SWP391.KoiXpress.Model.response.Vehicle.UpdateVehicleResponse;
import com.SWP391.KoiXpress.Service.OrderService;
import com.SWP391.KoiXpress.Service.ProgressService;
import com.SWP391.KoiXpress.Service.UserService;
import com.SWP391.KoiXpress.Service.VehicleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @Autowired
    VehicleService vehicleService;

    @Autowired
    UserService userService;


    //////////////////////Create-Progress///////////////////////////
    @PostMapping("/progress")
    public ResponseEntity<?> createProgress(@Valid @RequestBody ProgressRequest progressRequest){
        try{
            List<ProgressResponse> progresses = progressService.create(progressRequest);
            return ResponseEntity.ok(progresses);
        }catch (ProgressException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    ///////////////////////////////////////////////////////////////



    //////////////////////Update-Progress///////////////////////////
    @PutMapping("{progressId}")
    public ResponseEntity<UpdateProgressResponse> updateProgress(@PathVariable long progressId, @Valid @RequestBody UpdateProgressRequest updateProgressRequest){
        UpdateProgressResponse updateProgressResponse = progressService.update(progressId, updateProgressRequest);
        return ResponseEntity.ok(updateProgressResponse);
    }
    ///////////////////////////////////////////////////////////////



    //////////////////////Delete-Progress///////////////////////////
    @DeleteMapping("{progressId}")
    public ResponseEntity<String> deleteProgress(@PathVariable long progressId, DeleteProgressRequest reason){
        progressService.delete(progressId, reason);
        return ResponseEntity.ok("Delete successfully");
    }
    ////////////////////////////////////////////////////////////////



    //////////////////////Get-Progress-By-{OrderID}///////////////////////////
    @GetMapping("/progress/{orderId}")
    public ResponseEntity<List<ProgressResponse>> getProgressByOrderId(@PathVariable long orderId){
        List<ProgressResponse> progressResponses = progressService.findProgressesByOrderId(orderId);
        return ResponseEntity.ok(progressResponses);
    }
    /////////////////////////////////////////////////////////////////////



    //////////////////////Update-Order///////////////////////////
    @PutMapping("/order/{orderId}")
    public ResponseEntity<UpdateOrderResponse> updateOrderByDelivery(@PathVariable long orderId, @Valid @RequestBody UpdateOrderRequest updateOrderRequest){
        UpdateOrderResponse updateOrderByDelivery= orderService.updateOrderByDelivery(orderId, updateOrderRequest);
        return ResponseEntity.ok(updateOrderByDelivery);
    }
    /////////////////////////////////////////////////////////////



    //////////////////////Get-All-Available-Vehicle///////////////////////////
    @GetMapping("/vehicle/get-available")
    public ResponseEntity<List<AllVehicleResponse>> getAllAvailableVehicle(){
        List<AllVehicleResponse> vehicles = vehicleService.getAllAvailableVehicle();
        return ResponseEntity.ok(vehicles);
    }
    /////////////////////////////////////////////////////////////



    //////////////////////Load-Order-To-Vehicle///////////////////////////
    @PostMapping("/vehicle/loadOrderToVehicle")
    public ResponseEntity<List<AllOrderResponse>> loadOrderToVehicle(@RequestBody @Valid LoadOrderToVehicleRequest loadOrderToVehicleRequest){
        List<AllOrderResponse> allOrderResponses = vehicleService.loadOrderToVehicle(loadOrderToVehicleRequest);
        return ResponseEntity.ok(allOrderResponses);
    }
    /////////////////////////////////////////////////////////////



    //////////////////////Get-Profile-User///////////////////////////
    @GetMapping("/profile")
    public ResponseEntity<EachUserResponse> getProfileUser() {
        EachUserResponse eachUserResponse = userService.getProfileUser();
        return ResponseEntity.ok(eachUserResponse);
    }
    ////////////////////////////////////////////////////////////////
}
