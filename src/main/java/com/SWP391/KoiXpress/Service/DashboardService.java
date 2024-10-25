package com.SWP391.KoiXpress.Service;

import com.SWP391.KoiXpress.Entity.Enum.Role;
import com.SWP391.KoiXpress.Entity.Orders;
import com.SWP391.KoiXpress.Entity.Users;
import com.SWP391.KoiXpress.Model.response.Order.AllOrderResponse;
import com.SWP391.KoiXpress.Model.response.User.EachUserResponse;
import com.SWP391.KoiXpress.Repository.FeedBackReplyRepository;
import com.SWP391.KoiXpress.Repository.FeedBackRepository;
import com.SWP391.KoiXpress.Repository.OrderRepository;
import com.SWP391.KoiXpress.Repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FeedBackRepository feedBackRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> dashboardStats = new HashMap<>();

        //Số lượng đơn hàng
        long totalOrders = orderRepository.count();
        dashboardStats.put("orders", totalOrders);

        // Top 5 Customers by Loyalty Points
        List<EachUserResponse> topCustomers = getTopCustomersByLoyaltyPoints(5);
        dashboardStats.put("topCustomers", topCustomers);

        // Top 5 Orders by Total Price
        List<AllOrderResponse> topOrders = getTopOrdersByTotalPrice(5);
        dashboardStats.put("topOrders", topOrders);

        //Số customer
        long customersCount = userRepository.countUsersByRole(Role.CUSTOMER);
        dashboardStats.put("customersCount", customersCount);

        long salesCount = userRepository.countUsersByRole(Role.SALE_STAFF);
        dashboardStats.put("salesCount", salesCount);

        double averageRating = feedBackRepository.getAverageRating();
        dashboardStats.put("averageRatingScore", averageRating);

        return dashboardStats;
    }

    public List<EachUserResponse> getTopCustomersByLoyaltyPoints(int limit) {
        Pageable topCustomersPage = PageRequest.of(0, limit);
        List<Users> topCustomers = userRepository.findTopCustomersByLoyaltyPoints(Role.CUSTOMER, topCustomersPage);

        return topCustomers.stream()
                .map(user -> modelMapper.map(user, EachUserResponse.class))
                .collect(Collectors.toList());
    }

    public List<AllOrderResponse> getTopOrdersByTotalPrice(int limit) {
        Pageable topOrdersPage = PageRequest.of(0, limit);
        List<Orders> topOrders = orderRepository.findTopOrdersByTotalPrice(topOrdersPage);

        // Convert Orders entity to response DTO (OrderResponse)
        return topOrders.stream()
                .map(order -> modelMapper.map(order, AllOrderResponse.class))
                .collect(Collectors.toList());
    }
}
