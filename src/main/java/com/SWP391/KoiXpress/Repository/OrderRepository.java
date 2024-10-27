package com.SWP391.KoiXpress.Repository;

import com.SWP391.KoiXpress.Entity.Enum.OrderStatus;
import com.SWP391.KoiXpress.Entity.Orders;
import com.SWP391.KoiXpress.Entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Orders,Long> {
    Orders findOrdersById(long Id);

    Page<Orders> findOrdersByUsers(Users users, Pageable pageable);

    List<Orders> findOrdersByOrderStatus(OrderStatus orderStatus);

    @Query("SELECT o FROM Orders o ORDER BY o.totalPrice DESC")
    List<Orders> findTopOrdersByTotalPrice(Pageable pageable);

    @Query("SELECT o.totalPrice FROM Orders o WHERE o.orderStatus = 'PAID'")
    List<Double> getAllTotalPricesOfPaidOrders();

    @Query("SELECT FUNCTION('DATE', o.orderDate) AS date, COUNT(o) AS orderCount " +
            "FROM Orders o GROUP BY FUNCTION('DATE', o.orderDate) ORDER BY FUNCTION('DATE', o.orderDate) ASC")
    List<Map<String, Object>> getOrderCountsByDate();



    // Tổng doanh số order ở trạng thái PAID
    @Query("SELECT SUM(o.totalPrice) FROM Orders o WHERE o.orderStatus = :status")
    Optional<Double> getTotalRevenueByStatus(@Param("status") OrderStatus status);

    // Tính trung bình
    @Query("SELECT AVG(o.totalPrice) FROM Orders o WHERE o.orderStatus = :status")
    Optional<Double> getAverageOrderValueByStatus(@Param("status") OrderStatus status);

    // Order có doanh số cao nhất
    @Query("SELECT MAX(o.totalPrice) FROM Orders o WHERE o.orderStatus = :status")
    Optional<Double> getHighestOrderValueByStatus(@Param("status") OrderStatus status);

    // Growth calculation
    @Query("SELECT SUM(o.totalPrice) FROM Orders o WHERE o.orderStatus = :status AND o.orderDate BETWEEN :startDate AND :endDate")
    Optional<Double> getTotalRevenueForPeriod(@Param("status") OrderStatus status, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    // Day
    @Query("SELECT o.orderDate AS date, o.totalPrice AS totalPrice " +
            "FROM Orders o WHERE FUNCTION('MONTH', o.orderDate) = :month AND FUNCTION('YEAR', o.orderDate) = :year " +
            "ORDER BY o.orderDate ASC")
    List<Map<String, Object>> getOrderPricesByDay(@Param("month") int month, @Param("year") int year);

    // Month
    @Query("SELECT o.orderDate AS date, o.totalPrice AS totalPrice " +
            "FROM Orders o WHERE FUNCTION('YEAR', o.orderDate) = :year " +
            "ORDER BY o.orderDate ASC")
    List<Map<String, Object>> getOrderPricesByMonth(@Param("year") int year);

    // Year
    @Query("SELECT o.orderDate AS date, o.totalPrice AS totalPrice " +
            "FROM Orders o ORDER BY o.orderDate ASC")
    List<Map<String, Object>> getOrderPricesByYear();
}
