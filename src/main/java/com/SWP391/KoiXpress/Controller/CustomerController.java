package com.SWP391.KoiXpress.Controller;

import com.SWP391.KoiXpress.Entity.Blogs;
import com.SWP391.KoiXpress.Model.request.Blog.CreateBlogRequest;
import com.SWP391.KoiXpress.Model.request.Order.CreateOrderRequest;
import com.SWP391.KoiXpress.Model.response.Blog.CreateBlogResponse;
import com.SWP391.KoiXpress.Model.response.Blog.DeleteBlogResponse;
import com.SWP391.KoiXpress.Model.response.Blog.UpdateBlogResponse;
import com.SWP391.KoiXpress.Model.response.Order.AllOrderByCurrentResponse;
import com.SWP391.KoiXpress.Model.response.Order.CreateOrderResponse;
import com.SWP391.KoiXpress.Model.response.User.DeleteUserByUserResponse;
import com.SWP391.KoiXpress.Model.response.User.EachUserResponse;
import com.SWP391.KoiXpress.Service.BlogService;
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
@RequestMapping("/api/customer")
@CrossOrigin("*")
@SecurityRequirement(name="api")
@PreAuthorize("hasAuthority('CUSTOMER')")
public class CustomerController {
    @Autowired
    BlogService blogService;

    @Autowired
    UserService userService;

    @Autowired
    OrderService orderService;


    //////////////////////Get-Profile-User///////////////////////////
    @GetMapping("/profile")
    public ResponseEntity<EachUserResponse> getProfileUser(){
        EachUserResponse eachUserResponse = userService.getProfileUser();
        return ResponseEntity.ok(eachUserResponse);
    }
    ////////////////////////////////////////////////////////////////



    //////////////////////Delete-User///////////////////////////
    @DeleteMapping("/{userId}")
    public ResponseEntity<DeleteUserByUserResponse> deleteUser(@PathVariable long userId){
        DeleteUserByUserResponse deleteUserByUserResponse = userService.deleteByUser(userId);
        return ResponseEntity.ok(deleteUserByUserResponse);
    }
    ///////////////////////////////////////////////////////////



    //////////////////////Create-Blog///////////////////////////
    @PostMapping("/blog")
    public ResponseEntity<CreateBlogResponse> createBlog(@Valid @RequestBody CreateBlogRequest createBlogRequest){
        CreateBlogResponse newBlog = blogService.createBlog(createBlogRequest);
        return ResponseEntity.ok(newBlog);
    }
    ///////////////////////////////////////////////////////////



    //////////////////////Delete-Blog///////////////////////////
    @DeleteMapping("/blog/{blogId}")
    public ResponseEntity<DeleteBlogResponse> deleteBlog(@PathVariable long blogId){
        DeleteBlogResponse deleteBlog = blogService.delete(blogId);
        return ResponseEntity.ok(deleteBlog);
    }
    ////////////////////////////////////////////////////////////



    //////////////////////Update-Blog///////////////////////////
    @PutMapping("/blog/{blogId}")
    public ResponseEntity<UpdateBlogResponse> updateBlog(@PathVariable long blogId,@Valid @RequestBody Blogs blogs){
        UpdateBlogResponse newBlog = blogService.update(blogId, blogs);
        return  ResponseEntity.ok(newBlog);
    }
    ////////////////////////////////////////////////////////////



    //////////////////////Create-Order///////////////////////////
    @PostMapping("/order")
    public ResponseEntity<CreateOrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest createOrderRequest) throws Exception {
        CreateOrderResponse order = orderService.create(createOrderRequest);
        return ResponseEntity.ok(order);
    }
    ////////////////////////////////////////////////////////////


    //////////////////////Get-All-Order-By-CurrentCustomer///////////////////////////
    @GetMapping("/order/each-user")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<List<AllOrderByCurrentResponse>> getAllOrdersByCurrentUser(){
        List<AllOrderByCurrentResponse> createOrderResponseList = orderService.getAllOrdersByCurrentUser();
        return ResponseEntity.ok(createOrderResponseList);
    }
    /////////////////////////////////////////////////////////////////////////////////



    //////////////////////PAYMENT-URL-Order///////////////////////////
    @PostMapping("/orderPaymentUrl/{orderId}")
    public ResponseEntity<String> orderPaymentUrl(@PathVariable long orderId) throws Exception {
        String url = orderService.orderPaymentUrl(orderId);
        return ResponseEntity.ok(url);
    }
    /////////////////////////////////////////////////////////////////



    //////////////////////History-Order///////////////////////////
    @GetMapping("/order/orderHistory")
    public ResponseEntity<List<AllOrderByCurrentResponse>>  getAllOrdersDeliveredByCurrentUser(){
        List<AllOrderByCurrentResponse> createOrderResponseList = orderService.getAllOrdersDeliveredByCurrentUser();
        return ResponseEntity.ok(createOrderResponseList);
    }
    /////////////////////////////////////////////////////////



    //////////////////////Create-Transaction///////////////////////////
    @PostMapping("/transaction")
    public ResponseEntity<?> createTransaction(@RequestParam long orderId){
        orderService.createTransactions(orderId);
        return ResponseEntity.ok("Create transaction success");
    }
    //////////////////////////////////////////////////////////////////
}
