package com.SWP391.KoiXpress.Service;

import com.SWP391.KoiXpress.Entity.Enum.OrderStatus;
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

import java.util.*;
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

    //-----------------------------------------------------------------------------------------------
    //GET DASHBOARDSTATS-----------------------------------------------------------------------------
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> dashboardStats = new HashMap<>();

        //Số lượng đơn hàng
        long totalOrders = orderRepository.count();
        dashboardStats.put("orders", totalOrders);

        //TotalPrice orders listing
        List<Double> paidOrdersTotalPrices = orderRepository.getAllTotalPricesOfPaidOrders();
        dashboardStats.put("paidOrdersTotalPrices", paidOrdersTotalPrices);

        // Top 5 Customers by Loyalty Points
        List<EachUserResponse> topCustomers = getTopCustomersByLoyaltyPoints(5);
        dashboardStats.put("topCustomers", topCustomers);

        // Top 5 Orders by Total Price
        List<AllOrderResponse> topOrders = getTopOrdersByTotalPrice(5);
        dashboardStats.put("topOrders", topOrders);

        //Số customer
        long customersCount = userRepository.countUsersByRole(Role.CUSTOMER);
        dashboardStats.put("customersCount", customersCount);
        //Số Sales
        long salesCount = userRepository.countUsersByRole(Role.SALE_STAFF);
        dashboardStats.put("salesCount", salesCount);

        Double averageRating = feedBackRepository.getAverageRating().orElse(0.0);
        dashboardStats.put("averageRatingScore", averageRating);

        // Order Trends Over Time
        List<Map<String, Object>> ordersOverTimeList = orderRepository.getOrderCountsByDate();
        Map<String, Long> ordersOverTime = new HashMap<>();
        // Convert List<Map<String, Object>> to Map<String, Long>
        for (Map<String, Object> entry : ordersOverTimeList) {
            String date = (String) entry.get("date");
            Long orderCount = (Long) entry.get("orderCount");
            ordersOverTime.put(date, orderCount);
        }

        dashboardStats.put("ordersOverTime", ordersOverTime);

        ////////////////////////////////////////////////////////////////////
        OrderStatus paidStatus = OrderStatus.PAID;

        // Fetch Total Revenue for PAID orders
        Double totalRevenue = orderRepository.getTotalRevenueByStatus(paidStatus).orElse(0.0);
        dashboardStats.put("totalRevenue", totalRevenue);

        // Fetch Average Order Value for PAID orders
        Double averageOrderValue = orderRepository.getAverageOrderValueByStatus(paidStatus).orElse(0.0);
        dashboardStats.put("averageOrderValue", averageOrderValue);

        // Fetch Highest Order Value for PAID orders
        Double highestOrderValue = orderRepository.getHighestOrderValueByStatus(paidStatus).orElse(0.0);
        dashboardStats.put("highestOrderValue", highestOrderValue);

        // Calculate Revenue Growth
        Calendar calendar = Calendar.getInstance();

        // Current Period (this month)
        calendar.set(Calendar.DAY_OF_MONTH, 1); // Start of the month
        Date startDateCurrentPeriod = calendar.getTime();
        Date endDateCurrentPeriod = new Date(); // Today

        Double currentPeriodRevenue = orderRepository
                .getTotalRevenueForPeriod(paidStatus, startDateCurrentPeriod, endDateCurrentPeriod)
                .orElse(0.0);

        // Previous Period (last month)
        calendar.add(Calendar.MONTH, -1); // Go to the previous month
        Date endDatePreviousPeriod = calendar.getTime();

        calendar.set(Calendar.DAY_OF_MONTH, 1); // Start of the previous month
        Date startDatePreviousPeriod = calendar.getTime();

        Double previousPeriodRevenue = orderRepository
                .getTotalRevenueForPeriod(paidStatus, startDatePreviousPeriod, endDatePreviousPeriod)
                .orElse(0.0);

        Double revenueGrowth = (previousPeriodRevenue != 0)
                ? ((currentPeriodRevenue - previousPeriodRevenue) / previousPeriodRevenue) * 100
                : 0.0;
        dashboardStats.put("revenueGrowth", revenueGrowth);

        return dashboardStats;
    }

    //-----------------------------------------------------------------------------------------------
    //GET ORDERS STATISTICS-----------------------------------------------------------------------------

    public List<Map<String, Object>> getOrderStatistics(String filter) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Months are zero-based in Java

        switch (filter.toLowerCase()) {
            case "day":
                return orderRepository.getOrderPricesByDay(currentMonth, currentYear);
            case "month":
                return orderRepository.getOrderPricesByMonth(currentYear);
            case "year":
                return orderRepository.getOrderPricesByYear();
            default:
                throw new IllegalArgumentException("Invalid filter type. Choose from 'day', 'month', or 'year'.");
        }
    }

    //-----------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------

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

        return topOrders.stream()
                .map(order -> modelMapper.map(order, AllOrderResponse.class))
                .collect(Collectors.toList());
    }
}
