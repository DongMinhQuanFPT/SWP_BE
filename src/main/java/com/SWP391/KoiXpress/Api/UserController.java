package com.SWP391.KoiXpress.Api;

import com.SWP391.KoiXpress.Model.request.User.UpdateCustomerRequest;
import com.SWP391.KoiXpress.Model.response.Order.AllOrderByCurrentResponse;
import com.SWP391.KoiXpress.Model.response.User.UpdateCustomerResponse;
import com.SWP391.KoiXpress.Service.OrderService;
import com.SWP391.KoiXpress.Service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin("*")
@SecurityRequirement(name="api")
@PreAuthorize("hasAuthority('CUSTOMER','MANAGER','SALE_STAFF','DELIVERING_STAFF')")
public class UserController {


    @Autowired
    UserService userService;

    @Autowired
    OrderService orderService;

    @PutMapping("/{userId}")
    public ResponseEntity<UpdateCustomerResponse> updateCustomer(@PathVariable long userId, @Valid @RequestBody UpdateCustomerRequest updateCustomerRequest){
        UpdateCustomerResponse updateUser = userService.update(userId, updateCustomerRequest);
        return ResponseEntity.ok(updateUser);
    }

    @GetMapping("/order/each-user")
    public ResponseEntity<List<AllOrderByCurrentResponse>> getAllOrdersByCurrentUser(){
        List<AllOrderByCurrentResponse> createOrderResponseList = orderService.getAllOrdersByCurrentUser();
        return ResponseEntity.ok(createOrderResponseList);
    }

}
