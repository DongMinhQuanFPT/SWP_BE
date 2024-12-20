package com.SWP391.KoiXpress.Controller;

import com.SWP391.KoiXpress.Model.response.Order.*;
import com.SWP391.KoiXpress.Model.response.Paging.PagedResponse;
import com.SWP391.KoiXpress.Model.response.Transaction.AllTransactionResponse;
import com.SWP391.KoiXpress.Service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@SecurityRequirement(name = "api")
@CrossOrigin("*")
@PreAuthorize("hasAuthority('MANAGER') or hasAuthority('DELIVERING_STAFF') or hasAuthority('SALE_STAFF') or hasAuthority('CUSTOMER')")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    //////////////////////Get-Each-Order///////////////////////////
    @PreAuthorize("hasAuthority('MANAGER') or hasAuthority('DELIVERING_STAFF') or hasAuthority('SALE_STAFF')")
    @GetMapping("{id}")
    public ResponseEntity<CreateOrderResponse> getEachOrder(@PathVariable long id) {
        CreateOrderResponse createOrderResponse = orderService.getEachOrderById(id);
        return ResponseEntity.ok(createOrderResponse);
    }
    ///////////////////////////////////////////////////////////////



    //////////////////////Get-All-Order///////////////////////////
    @PreAuthorize("hasAuthority('MANAGER') or hasAuthority('DELIVERING_STAFF') or hasAuthority('SALE_STAFF')")
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
    public ResponseEntity<String> delete(@PathVariable long id) {
        orderService.delete(id);
        return ResponseEntity.ok("Delete successfully");
    }
    /////////////////////////////////////////////////////////////



    //////////////////////Get-OrderList-Pending///////////////////////////
    @PreAuthorize("hasAuthority('MANAGER') or hasAuthority('DELIVERING_STAFF') or hasAuthority('SALE_STAFF')")
    @GetMapping("/listOrderPending")
    public ResponseEntity<PagedResponse<AllOrderResponse>> getListOrderPending(
            @RequestParam( defaultValue = "1") int page,
            @RequestParam( defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.getListOrderPending(page - 1, size));
    }
    //////////////////////////////////////////////////////////////////////



    //////////////////////Get-OrderList-AwaitingPayment///////////////////////////
    @PreAuthorize("hasAuthority('MANAGER') or hasAuthority('DELIVERING_STAFF') or hasAuthority('SALE_STAFF')")
    @GetMapping("/listOrderAwaitingPayment")
    public ResponseEntity<PagedResponse<AllOrderResponse>> getListOrderAwaitingPayment(
            @RequestParam( defaultValue = "1") int page,
            @RequestParam( defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.getListOrderAwaitingPayment(page - 1, size));
    }
    /////////////////////////////////////////////////////////////////////////////



    //////////////////////Get-OrderList-Paid///////////////////////////
    @PreAuthorize("hasAuthority('MANAGER') or hasAuthority('DELIVERING_STAFF') or hasAuthority('SALE_STAFF')")
    @GetMapping("/listOrderPaid")
    public ResponseEntity<PagedResponse<AllOrderResponse>> getListOrderPaid(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam( defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.getListOrderPaid(page - 1, size));
    }
    //////////////////////////////////////////////////////////////////



    //////////////////////Get-OrderList-Reject///////////////////////////
    @PreAuthorize("hasAuthority('MANAGER') or hasAuthority('DELIVERING_STAFF') or hasAuthority('SALE_STAFF')")
    @GetMapping("/listOrderReject")
    public ResponseEntity<PagedResponse<AllOrderResponse>> getListOrderReject(
            @RequestParam( defaultValue = "1") int page,
            @RequestParam( defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.getListOrderRejected(page - 1, size));
    }
    /////////////////////////////////////////////////////////////////////



    //////////////////////Get-OrderList-Shipping///////////////////////////
    @PreAuthorize("hasAuthority('MANAGER') or hasAuthority('DELIVERING_STAFF') or hasAuthority('SALE_STAFF')")
    @GetMapping("/listOrderShipping")
    public ResponseEntity<PagedResponse<AllOrderResponse>> getListOrderShipping(
            @RequestParam( defaultValue = "1") int page,
            @RequestParam( defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.getListOrderShipping(page - 1, size));
    }
    ///////////////////////////////////////////////////////////////////////



    //////////////////////Get-OrderList-Delivered///////////////////////////
    @PreAuthorize("hasAuthority('MANAGER') or hasAuthority('DELIVERING_STAFF') or hasAuthority('SALE_STAFF')")
    @GetMapping("/listOrderDelivered")
    public ResponseEntity<PagedResponse<AllOrderResponse>> getListOrderDelivered(
            @RequestParam( defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.getListOrderDelivered(page - 1, size));
    }
    ////////////////////////////////////////////////////////////////////////



    //////////////////////Get-OrderList-Canceled///////////////////////////
    @PreAuthorize("hasAuthority('MANAGER') or hasAuthority('DELIVERING_STAFF') or hasAuthority('SALE_STAFF')")
    @GetMapping("/listOrderCanceled")
    public ResponseEntity<PagedResponse<AllOrderCanceledResponse>> getListOrderCanceled(
            @RequestParam( defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.getListOrderCanceled(page - 1, size));
    }
    ////////////////////////////////////////////////////////////////////////



    //////////////////////Get-OrderList-Canceled///////////////////////////
    @PreAuthorize("hasAuthority('MANAGER') or hasAuthority('DELIVERING_STAFF') or hasAuthority('SALE_STAFF')")
    @GetMapping("/listOrderBooking")
    public ResponseEntity<PagedResponse<AllOrderResponse>> getListOrderBooking(
            @RequestParam( defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.getListOrderBooking(page - 1, size));
    }
    ////////////////////////////////////////////////////////////////////////



    //////////////////////Get-TransactionList///////////////////////////
    @PreAuthorize("hasAuthority('MANAGER') or hasAuthority('DELIVERING_STAFF') or hasAuthority('SALE_STAFF')")
    @GetMapping("/listTransaction")
    public ResponseEntity<List<AllTransactionResponse>> getAllTransaction(){
        return ResponseEntity.ok(orderService.getAllTransaction());
    }
    ////////////////////////////////////////////////////////////////////
}