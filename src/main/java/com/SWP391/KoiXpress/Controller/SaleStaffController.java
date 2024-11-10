package com.SWP391.KoiXpress.Controller;

import com.SWP391.KoiXpress.Exception.NotFoundException;
import com.SWP391.KoiXpress.Exception.WareHouseException;
import com.SWP391.KoiXpress.Model.request.Order.UpdateOrderRequest;
import com.SWP391.KoiXpress.Model.response.Order.UpdateOrderResponse;
import com.SWP391.KoiXpress.Model.response.User.EachUserResponse;
import com.SWP391.KoiXpress.Service.OrderService;
import com.SWP391.KoiXpress.Service.UserService;
import com.SWP391.KoiXpress.Service.WareHouseService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sale")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
@PreAuthorize("hasAuthority('SALE_STAFF')")
public class SaleStaffController {

    @Autowired
    OrderService orderService;

    @Autowired
    UserService userService;

    @Autowired
    WareHouseService wareHouseService;


    //////////////////////Update-Order///////////////////////////
    @PutMapping("{orderId}")
    public ResponseEntity<UpdateOrderResponse> updateOrder(@PathVariable long orderId, @RequestBody @Valid UpdateOrderRequest orderRequest) throws Exception {
        UpdateOrderResponse updateOrder = orderService.updateBySale(orderId, orderRequest);
        return ResponseEntity.ok(updateOrder);
    }
    //////////////////////////////////////////////////////////////



    //////////////////////Send-Account///////////////////////////
    @GetMapping("/sendAccount")
    public ResponseEntity<String> sendAccountUser(@RequestBody String fullName){
        userService.sendAccountUser(fullName);
        return ResponseEntity.ok("Send email account for user success");
    }
    /////////////////////////////////////////////////////////////




    //////////////////////BookingSlot-WareHouse///////////////////////////
    @PostMapping("/warehouse/bookingSlot")
    public ResponseEntity<String> bookingSlot(@RequestParam long wareHouseId, @RequestParam long orderId) {
        try {
            boolean isBooked = wareHouseService.bookingSlot(wareHouseId, orderId);
            if (isBooked) {
                return ResponseEntity.ok("Successfully booked");
            }
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (WareHouseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unknown error");
    }
    //////////////////////////////////////////////////////////////////////



    //////////////////////Get-Profile-User///////////////////////////
    @GetMapping("/profile")
    public ResponseEntity<EachUserResponse> getProfileUser() {
        EachUserResponse eachUserResponse = userService.getProfileUser();
        return ResponseEntity.ok(eachUserResponse);
    }
    ////////////////////////////////////////////////////////////////
}
