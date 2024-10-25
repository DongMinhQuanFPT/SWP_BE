package com.SWP391.KoiXpress.Service;

import com.SWP391.KoiXpress.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    OrderRepository orderRepository;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> dashboardStats = new HashMap<>();
        long totalOrders = orderRepository.count();

        return null;
    }
}
