package com.SWP391.KoiXpress.Api;

import com.SWP391.KoiXpress.Model.request.Order.UpdateOrderRequest;
import com.SWP391.KoiXpress.Model.response.Order.UpdateOrderResponse;
import com.SWP391.KoiXpress.Service.OrderService;
import com.SWP391.KoiXpress.Service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PutMapping("{orderId}")
    public ResponseEntity<UpdateOrderResponse> update(@PathVariable long orderId, @RequestBody @Valid UpdateOrderRequest orderRequest) throws Exception {
        UpdateOrderResponse updateOrder = orderService.updateBySale(orderId, orderRequest);
        return ResponseEntity.ok(updateOrder);
    }

    @GetMapping("/sendAccount")
    public ResponseEntity<String> sendAccountUser(@RequestBody String fullName){
        userService.sendAccountUser(fullName);
        return ResponseEntity.ok("Send email account for user success");
    }
}
