package com.SWP391.KoiXpress.Controller;

import com.SWP391.KoiXpress.Model.response.Order.*;
import com.SWP391.KoiXpress.Model.response.Paging.PagedResponse;
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
    public ResponseEntity<PagedResponse<AllOrderResponse>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        PagedResponse<AllOrderResponse> orderResponses = orderService.getAll(page - 1, size);
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
    public ResponseEntity<PagedResponse<AllOrderResponse>> getListOrderPending(
            @RequestParam( defaultValue = "1") int page,
            @RequestParam( defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.getListOrderPending(page - 1, size));
    }
//////////////////////////////////////////////////////////////////////

    //////////////////////Get-OrderList-AwaitingPayment///////////////////////////
    @GetMapping("/listOrderAwaitingPayment")
    public ResponseEntity<PagedResponse<AllOrderResponse>> getListOrderAwaitingPayment(
            @RequestParam( defaultValue = "1") int page,
            @RequestParam( defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.getListOrderAwaitingPayment(page - 1, size));
    }
/////////////////////////////////////////////////////////////////////////////

    //////////////////////Get-OrderList-Paid///////////////////////////
    @GetMapping("/listOrderPaid")
    public ResponseEntity<PagedResponse<AllOrderResponse>> getListOrderPaid(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam( defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.getListOrderPaid(page - 1, size));
    }
//////////////////////////////////////////////////////////////////

    //////////////////////Get-OrderList-Reject///////////////////////////
    @GetMapping("/listOrderReject")
    public ResponseEntity<PagedResponse<AllOrderResponse>> getListOrderReject(
            @RequestParam( defaultValue = "1") int page,
            @RequestParam( defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.getListOrderRejected(page - 1, size));
    }
/////////////////////////////////////////////////////////////////////

    //////////////////////Get-OrderList-Shipping///////////////////////////
    @GetMapping("/listOrderShipping")
    public ResponseEntity<PagedResponse<AllOrderResponse>> getListOrderShipping(
            @RequestParam( defaultValue = "1") int page,
            @RequestParam( defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.getListOrderShipping(page - 1, size));
    }
///////////////////////////////////////////////////////////////////////

    //////////////////////Get-OrderList-Delivered///////////////////////////
    @GetMapping("/listOrderDelivered")
    public ResponseEntity<PagedResponse<AllOrderResponse>> getListOrderDelivered(
            @RequestParam( defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.getListOrderDelivered(page - 1, size));
    }
////////////////////////////////////////////////////////////////////////

    //////////////////////Get-OrderList-Canceled///////////////////////////
    @GetMapping("/listOrderDelivered")
    public ResponseEntity<PagedResponse<AllOrderResponse>> getListOrderCanceled(
            @RequestParam( defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.getListOrderCanceled(page - 1, size));
    }
////////////////////////////////////////////////////////////////////////
}