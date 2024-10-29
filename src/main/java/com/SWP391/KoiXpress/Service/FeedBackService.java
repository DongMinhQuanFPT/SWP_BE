package com.SWP391.KoiXpress.Service;

import com.SWP391.KoiXpress.Entity.*;
import com.SWP391.KoiXpress.Exception.EntityNotFoundException;
import com.SWP391.KoiXpress.Model.request.FeedBack.FeedBackRequet;
import com.SWP391.KoiXpress.Model.response.FeedBack.FeedBackReplyResponse;
import com.SWP391.KoiXpress.Model.response.FeedBack.FeedBackResponse;
import com.SWP391.KoiXpress.Model.response.Paging.PagedResponse;
import com.SWP391.KoiXpress.Model.response.User.EachUserResponse;
import com.SWP391.KoiXpress.Model.response.User.UserResponse;
import com.SWP391.KoiXpress.Repository.FeedBackReplyRepository;
import com.SWP391.KoiXpress.Repository.FeedBackRepository;
import com.SWP391.KoiXpress.Repository.OrderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedBackService {

    @Autowired
    FeedBackRepository feedBackRepository;
    @Autowired
    FeedBackReplyRepository feedBackReplyRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    private ModelMapper modelMapper;



    public FeedBackResponse createFeedBack(FeedBackRequet feedBackRequet) {
        Users users = authenticationService.getCurrentUser();
        Orders orders = orderRepository.findById(feedBackRequet.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        FeedBacks feedBacks = new FeedBacks();
        feedBacks.setUsers(users);
        feedBacks.setOrders(orders);
        feedBacks.setComment(feedBackRequet.getComment());
        feedBacks.setRatingScore(feedBackRequet.getRatingScore());
        feedBacks.setCreatedTime(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));

        FeedBacks savedFeedback = feedBackRepository.save(feedBacks);
        FeedBackResponse response = modelMapper.map(savedFeedback, FeedBackResponse.class);
        EachUserResponse eachUserResponse = modelMapper.map(users, EachUserResponse.class);
        response.setEachUserResponse(eachUserResponse);

        return response;
    }

    public FeedBacks updateFeedBack(long feedId, FeedBackRequet feedBackRequet) {
        FeedBacks oldFeedback = getFeedById(feedId);
        oldFeedback.setComment(feedBackRequet.getComment());
        oldFeedback.setRatingScore(feedBackRequet.getRatingScore());
        return feedBackRepository.save(oldFeedback);
    }

    public void deleteFeedBack(long feedId) {
        FeedBacks feedBacks = getFeedById(feedId);
        feedBacks.setDeleted(true);
        feedBackRepository.save(feedBacks);
    }

    public FeedBackReply replyToFeedBack(long id, String replyContent, String repliedBy) {
        FeedBacks feedBacks = getFeedById(id);
        FeedBackReply feedBackReply = new FeedBackReply();
        feedBackReply.setFeedBacks(feedBacks);
        feedBackReply.setReplyContent(replyContent);
        feedBackReply.setRepliedBy(repliedBy);
        feedBackReply.setReplyDate(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));

        feedBackReply = feedBackReplyRepository.save(feedBackReply);
        feedBacks.getReplies().add(feedBackReply);
        feedBackRepository.save(feedBacks);

        return feedBackReply;
    }

    public PagedResponse<FeedBackResponse> getAllFeedBacksByUser(Long userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<FeedBacks> feedBackPage = feedBackRepository.findFeedBacksByUsersId(userId, pageRequest);

        List<FeedBackResponse> feedBackResponses = feedBackPage.getContent().stream().map(feedBacks -> {
            EachUserResponse eachUserResponse = modelMapper.map(feedBacks.getUsers(), EachUserResponse.class);
            FeedBackResponse feedBackResponse = modelMapper.map(feedBacks, FeedBackResponse.class);
            feedBackResponse.setEachUserResponse(eachUserResponse);

            List<FeedBackReplyResponse> replyResponses = feedBacks.getReplies().stream()
                    .map(reply -> modelMapper.map(reply, FeedBackReplyResponse.class))
                    .collect(Collectors.toList());
            feedBackResponse.setReplies(replyResponses);

            return feedBackResponse;
        }).collect(Collectors.toList());

        return new PagedResponse<>(
                feedBackResponses,
                page,
                size,
                feedBackPage.getTotalElements(),
                feedBackPage.getTotalPages(),
                feedBackPage.isLast()
        );
    }

    public List<FeedBackResponse> getAllFeedBacksByOrder(Long orderId) {
        List<FeedBacks> feedBacksList = feedBackRepository.findFeedBacksByOrdersId(orderId);

        return feedBacksList.stream().map(feedBacks -> {
            EachUserResponse eachUserResponse = modelMapper.map(feedBacks.getUsers(), EachUserResponse.class);
            FeedBackResponse feedBackResponse = modelMapper.map(feedBacks, FeedBackResponse.class);
            feedBackResponse.setEachUserResponse(eachUserResponse);

            List<FeedBackReplyResponse> replyResponses = feedBacks.getReplies().stream()
                    .map(reply -> modelMapper.map(reply, FeedBackReplyResponse.class))
                    .collect(Collectors.toList());
            feedBackResponse.setReplies(replyResponses);

            return feedBackResponse;
        }).collect(Collectors.toList());
    }

    public PagedResponse<FeedBackResponse> getAllFeedBacksByCurrentUser(int page, int size) {
        Users user = authenticationService.getCurrentUser();
        if (user != null) {
            return getAllFeedBacksByUser(user.getId(), page, size);
        }
        return new PagedResponse<>(new ArrayList<>(), page, size, 0, 0, true);
    }

    private FeedBacks getFeedById(long Id) {
        FeedBacks oldFeedBacks = feedBackRepository.findById(Id);
        if (oldFeedBacks == null) {
            throw new EntityNotFoundException("FeedBack not found!");
        }
        return oldFeedBacks;
    }

}
