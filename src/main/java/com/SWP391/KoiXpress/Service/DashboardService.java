package com.SWP391.KoiXpress.Service;


import com.SWP391.KoiXpress.Entity.Enum.OrderStatus;
import com.SWP391.KoiXpress.Entity.Enum.Role;
import com.SWP391.KoiXpress.Entity.Orders;
import com.SWP391.KoiXpress.Entity.Users;
import com.SWP391.KoiXpress.Model.response.Order.AllOrderResponse;
import com.SWP391.KoiXpress.Model.response.User.EachUserResponse;
import com.SWP391.KoiXpress.Repository.*;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.properties.TextAlignment;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import java.io.ByteArrayOutputStream;

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
   ;

    //-----------------------------------------------------------------------------------------------
    //GET DASHBOARDSTATS-----------------------------------------------------------------------------

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> dashboardStats = new HashMap<>();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");

        // Số lượng đơn hàng
        long totalOrders = orderRepository.count();
        dashboardStats.put("orders", totalOrders);

        // Tổng giá đơn hàng đã thanh toán
        List<Map<String, Object>> paidOrders = orderRepository.getAllTotalPricesOfPaidOrders();
        List<Map<String, Object>> paidOrdersTotalPrices = paidOrders.stream().map(order -> {
            Map<String, Object> orderInfo = new HashMap<>();
            orderInfo.put("orderId", order.get("orderId"));
            orderInfo.put("totalPrice", order.get("totalPrice"));
            return orderInfo;
        }).collect(Collectors.toList());
        dashboardStats.put("paidOrdersTotalPrices", paidOrdersTotalPrices);

        // Top 5 Khách hàng theo điểm Loyalty
        List<EachUserResponse> topCustomers = getTopCustomersByLoyaltyPoints(5);
        dashboardStats.put("topCustomers", topCustomers);

        // Top 5 Đơn hàng theo Tổng giá
        List<AllOrderResponse> topOrders = getTopOrdersByTotalPrice(5);
        dashboardStats.put("topOrders", topOrders);

        // Số lượng Khách hàng
        long customersCount = userRepository.countUsersByRole(Role.CUSTOMER);
        dashboardStats.put("customersCount", customersCount);

        // Số lượng Nhân viên Bán hàng
        long salesCount = userRepository.countUsersByRole(Role.SALE_STAFF);
        dashboardStats.put("salesCount", salesCount);

        // Điểm đánh giá trung bình
        Double averageRating = feedBackRepository.getAverageRating().orElse(0.0);
        dashboardStats.put("averageRatingScore", averageRating);

        // Xu hướng đơn hàng theo thời gian (formatting dates)
        List<Map<String, Object>> ordersOverTimeList = orderRepository.getOrderCountsByDate();
        Map<String, Long> ordersOverTime = new HashMap<>();
        for (Map<String, Object> entry : ordersOverTimeList) {
            Date date = (Date) entry.get("date");
            Long orderCount = (Long) entry.get("orderCount");
            String formattedDate = dateFormatter.format(date);
            ordersOverTime.put(formattedDate, orderCount);
        }
        dashboardStats.put("ordersOverTime", ordersOverTime);

        ////////////////////////////////////////////////////////////////////
        OrderStatus paidStatus = OrderStatus.PAID;

        // Doanh thu Tổng cho các đơn hàng đã thanh toán
        Double totalRevenue = orderRepository.getTotalRevenueByStatus(paidStatus).orElse(0.0);
        dashboardStats.put("totalRevenue", totalRevenue);

        // Giá trị Đơn hàng Trung bình cho các đơn hàng đã thanh toán
        Double averageOrderValue = orderRepository.getAverageOrderValueByStatus(paidStatus).orElse(0.0);
        dashboardStats.put("averageOrderValue", averageOrderValue);

        // Giá trị Đơn hàng Cao nhất cho các đơn hàng đã thanh toán
        Double highestOrderValue = orderRepository.getHighestOrderValueByStatus(paidStatus).orElse(0.0);
        dashboardStats.put("highestOrderValue", highestOrderValue);

        // Tính Toán Tăng Trưởng Doanh Thu
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startDateCurrentPeriod = calendar.getTime();
        Date endDateCurrentPeriod = new Date();

        Double currentPeriodRevenue = orderRepository
                .getTotalRevenueForPeriod(paidStatus, startDateCurrentPeriod, endDateCurrentPeriod)
                .orElse(0.0);

        calendar.add(Calendar.MONTH, -1);
        Date endDatePreviousPeriod = calendar.getTime();

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startDatePreviousPeriod = calendar.getTime();

        Double previousPeriodRevenue = orderRepository
                .getTotalRevenueForPeriod(paidStatus, startDatePreviousPeriod, endDatePreviousPeriod)
                .orElse(0.0);

        Double revenueGrowth = (previousPeriodRevenue != 0)
                ? ((currentPeriodRevenue - previousPeriodRevenue) / previousPeriodRevenue) * 100
                : null;
        dashboardStats.put("revenueGrowth", revenueGrowth);

        dashboardStats.put("startDateCurrentPeriod", dateFormatter.format(startDateCurrentPeriod));
        dashboardStats.put("endDateCurrentPeriod", dateFormatter.format(endDateCurrentPeriod));
        dashboardStats.put("startDatePreviousPeriod", dateFormatter.format(startDatePreviousPeriod));
        dashboardStats.put("endDatePreviousPeriod", dateFormatter.format(endDatePreviousPeriod));

        return dashboardStats;
    }

    //-----------------------------------------------------------------------------------------------
    //GET ORDERS STATISTICS-----------------------------------------------------------------------------

    public List<Map<String, Object>> getOrderStatistics(String filter) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);

        switch (filter.toLowerCase()) {
            case "detail":
                List<Orders> paidOrders = orderRepository.findOrdersByStatusPaid();
                SimpleDateFormat Dateformatter = new SimpleDateFormat("dd/MM/yyyy");

                return paidOrders.stream().map(order -> {
                    Map<String, Object> orderMap = new HashMap<>();
                    orderMap.put("id", order.getId());
                    orderMap.put("totalPrice", order.getTotalPrice());
                    orderMap.put("orderDate", Dateformatter.format(order.getOrderDate()));
                    return orderMap;
                }).collect(Collectors.toList());

            case "synthetic":

                return orderRepository.getOrderCountByDate();

            case "month":

                return orderRepository.getOrderPricesByMonth(currentYear);

            case "year":

                return orderRepository.getOrderPricesByYear();

            default:
                throw new IllegalArgumentException("Invalid filter type. Choose from 'detail', 'synthetic', 'month', or 'year'.");
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

    public byte[] generateDashboardPdf(Map<String, Object> dashboardStats) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Dashboard Statistics")
                    .setFontSize(24)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new LineSeparator(new SolidLine()));
            document.add(new Paragraph("\n")); // Space

            addStatisticsToPdf(document, dashboardStats);

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error while generating PDF: " + e.getMessage(), e);
        }
    }

    private void addStatisticsToPdf(Document document, Map<String, Object> dashboardStats) {
        dashboardStats.forEach((key, value) -> {
            document.add(new Paragraph(key + ": " + formatValue(value))
                    .setFontSize(12)
                    .setMultipliedLeading(1.5f));
        });

    }

    private String formatValue(Object value) {
        if (value == null) {
            return "N/A";
        }
        if (value instanceof List) {
            return formatList((List<?>) value);
        } else if (value instanceof Map) {
            return formatMap((Map<?, ?>) value);
        } else {
            return value.toString();
        }
    }


    private String formatList(List<?> list) {
        return list.stream().map(Object::toString).collect(Collectors.joining(", "));
    }

    private String formatMap(Map<?, ?> map) {
        return map.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));
    }



}
