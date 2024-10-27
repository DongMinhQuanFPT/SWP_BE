package com.SWP391.KoiXpress.Controller;

import com.SWP391.KoiXpress.Model.response.Order.*;
import com.SWP391.KoiXpress.Service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@SecurityRequirement(name = "api")
@CrossOrigin("*")
@PreAuthorize("hasAuthority('MANAGER') or hasAuthority('DELIVERING_STAFF') or hasAuthority('SALE_STAFF')")
public class OrderController {

    @Autowired
    OrderService orderService;

    //////////////////////Get-Each-Order///////////////////////////
    @GetMapping("{id}")
    public ResponseEntity<CreateOrderResponse> getEachOrder(@PathVariable long id) {
        CreateOrderResponse createOrderResponse = orderService.getEachOrderById(id);
        return ResponseEntity.ok(createOrderResponse);
    }
    ///////////////////////////////////////////////////////////////



    //////////////////////Get-All-Order///////////////////////////
    @GetMapping("/allOrder")
    public ResponseEntity<List<AllOrderResponse>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        List<AllOrderResponse> orderResponses = orderService.getAll(page - 1, size);
        return ResponseEntity.ok(orderResponses);
    }
    //////////////////////////////////////////////////////////////



    //////////////////////Delete-Order///////////////////////////
    @DeleteMapping("{id}")
    public ResponseEntity<DeleteOrderResponse> delete(@PathVariable long id) {
        DeleteOrderResponse deleteOrder = orderService.delete(id);
        return ResponseEntity.ok(deleteOrder);
    }
    /////////////////////////////////////////////////////////////



    //////////////////////Get-OrderList-Pending///////////////////////////
    @GetMapping("/listOrderPending")
    public ResponseEntity<List<AllOrderResponse>> getListOrderPending(){
        return  ResponseEntity.ok(orderService.getListOrderPending());
    }
    //////////////////////////////////////////////////////////////////////



    //////////////////////Get-OrderList-AwaitingPayment///////////////////////////
    @GetMapping("/listOrderAwaitingPayment")
    public ResponseEntity<List<AllOrderResponse>> getListOrderAwaitingPayment(){
        return  ResponseEntity.ok(orderService.getListOrderAwaitingPayment());
    }
    /////////////////////////////////////////////////////////////////////////////



    //////////////////////Get-OrderList-Paid///////////////////////////
    @GetMapping("/listOrderPaid")
    public ResponseEntity<List<AllOrderResponse>> getListOrderPaid(){
        return  ResponseEntity.ok(orderService.getListOrderPaid());
    }
    //////////////////////////////////////////////////////////////////



    //////////////////////Get-OrderList-Reject///////////////////////////
    @GetMapping("/listOrderReject")
    public ResponseEntity<List<AllOrderResponse>> getListOrderReject(){
        return  ResponseEntity.ok(orderService.getListOrderRejected());
    }
    /////////////////////////////////////////////////////////////////////



    //////////////////////Get-OrderList-Shipping///////////////////////////
    @GetMapping("/listOrderShipping")
    public ResponseEntity<List<AllOrderResponse>> getListOrderShipping(){
        return  ResponseEntity.ok(orderService.getListOrderShipping());
    }
    ///////////////////////////////////////////////////////////////////////



    //////////////////////Get-OrderList-Delivered///////////////////////////
    @GetMapping("/listOrderDelivered")
    public ResponseEntity<List<AllOrderResponse>> getListOrderDelivered(){
        return  ResponseEntity.ok(orderService.getListOrderDelivered());
    }
    ////////////////////////////////////////////////////////////////////////
}