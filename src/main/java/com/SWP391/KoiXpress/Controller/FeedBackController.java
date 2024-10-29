package com.SWP391.KoiXpress.Controller;

import com.SWP391.KoiXpress.Entity.FeedBacks;
import com.SWP391.KoiXpress.Entity.FeedBackReply;
import com.SWP391.KoiXpress.Model.request.FeedBack.FeedBackRequet;
import com.SWP391.KoiXpress.Model.response.FeedBack.FeedBackResponse;
import com.SWP391.KoiXpress.Model.response.Paging.PagedResponse;
import com.SWP391.KoiXpress.Service.FeedBackService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/feedBack")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
@PreAuthorize("hasAuthority('CUSTOMER') or hasAuthority('SALE_STAFF') or hasAuthority('MANAGER')")
public class FeedBackController {

    @Autowired
    private FeedBackService feedBackService;

    ///////////////////////////////Create Feedback///////////////////////////////
    @PostMapping
    public ResponseEntity<FeedBackResponse> createFeedBack(@Valid @RequestBody FeedBackRequet feedBackRequet) {
        FeedBackResponse response = feedBackService.createFeedBack(feedBackRequet);
        return ResponseEntity.ok(response);
    }
    ////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////Reply Feedback///////////////////////////////
    @PostMapping("/{feedBackId}/reply")
    @PreAuthorize("hasAuthority('SALE_STAFF') or hasAuthority('MANAGER')")
    public ResponseEntity<FeedBackReply> replyToFeedBack(
            @PathVariable long feedBackId,
            @RequestBody String replyContent,
            Principal principal) {
        String repliedBy = principal.getName();
        FeedBackReply reply = feedBackService.replyToFeedBack(feedBackId, replyContent, repliedBy);
        return ResponseEntity.ok(reply);
    }
    ////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////Get Feedback by User///////////////////////////////
    @GetMapping("/user/{userId}/feedbacks")
    @PreAuthorize("hasAuthority('SALE_STAFF') or hasAuthority('MANAGER')")
    public ResponseEntity<PagedResponse<FeedBackResponse>> getFeedBacksByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResponse<FeedBackResponse> pagedResponse = feedBackService.getAllFeedBacksByUser(userId, page - 1, size);
        return ResponseEntity.ok(pagedResponse);
    }
    ////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////Get Feedback by Order///////////////////////////////
    @GetMapping("/order/{orderId}/feedbacks")
    @PreAuthorize("hasAuthority('SALE_STAFF') or hasAuthority('MANAGER')")
    public List<FeedBackResponse> getFeedBacksByOrder(@PathVariable Long orderId) {
        return feedBackService.getAllFeedBacksByOrder(orderId);
    }
    ////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////Get Feedback by current User///////////////////////////////
    @GetMapping("/my-feedbacks")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<PagedResponse<FeedBackResponse>> getFeedBacksByCurrentUser(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResponse<FeedBackResponse> pagedResponse = feedBackService.getAllFeedBacksByCurrentUser(page - 1, size);
        return ResponseEntity.ok(pagedResponse);
    }
    ////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////Update Feedback///////////////////////////////
    @PutMapping("/{feedBackId}")
    @PreAuthorize("hasAuthority('SALE_STAFF') or (hasAuthority('CUSTOMER') and @feedBackService.isOwner(#feedBackId))")
    public ResponseEntity<FeedBacks> updateFeedBack(
            @PathVariable long feedBackId,
            @Valid @RequestBody FeedBackRequet feedBackRequet) {
        FeedBacks newFeedBacks = feedBackService.updateFeedBack(feedBackId, feedBackRequet);
        return ResponseEntity.ok(newFeedBacks);
    }
    ////////////////////////////////////////////////////////////////////////////////


    ///////////////////////////////Delete Feedback///////////////////////////////
    @DeleteMapping("/{feedBackId}")
    @PreAuthorize("hasAuthority('SALE_STAFF') or (hasAuthority('CUSTOMER') and @feedBackService.isOwner(#feedBackId))")
    public ResponseEntity<?> deleteFeedBack(@PathVariable long feedBackId) {
        feedBackService.deleteFeedBack(feedBackId);
        return ResponseEntity.ok().build();
    }
    ////////////////////////////////////////////////////////////////////////////////
}