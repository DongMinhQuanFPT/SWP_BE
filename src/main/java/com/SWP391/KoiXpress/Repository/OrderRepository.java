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

    @Query("SELECT o FROM Orders o WHERE o.users = :user AND o.orderStatus = :status ORDER BY o.id DESC")
    Page<Orders> findOrdersByUsersAndStatus(@Param("user") Users user, @Param("status") OrderStatus status, Pageable pageable);

    @Query("SELECT o FROM Orders o WHERE o.users = :user AND o.orderStatus NOT IN (:excludedStatuses) ORDER BY o.id DESC")
    Page<Orders> findOrdersByUsers(@Param("user") Users user, @Param("excludedStatuses") List<OrderStatus> excludedStatuses, Pageable pageable);


    Page<Orders> findOrdersByOrderStatus(OrderStatus status, Pageable pageable);

    @Query("SELECT o FROM Orders o ORDER BY o.totalPrice DESC")
    List<Orders> findTopOrdersByTotalPrice(Pageable pageable);

    @Query("SELECT new map(o.id as orderId, o.totalPrice as totalPrice) FROM Orders o WHERE o.orderStatus = 'DELIVERED'")
    List<Map<String, Object>> getAllTotalPricesOfPaidOrders();

    @Query("SELECT FUNCTION('DATE', o.orderDate) AS date, COUNT(o) AS orderCount " +
            "FROM Orders o GROUP BY FUNCTION('DATE', o.orderDate) ORDER BY FUNCTION('DATE', o.orderDate) ASC")
    List<Map<String, Object>> getOrderCountsByDate();

    //Ngày có đơn đầu tiên
    @Query("SELECT MIN(o.orderDate) FROM Orders o")
    Optional<Date> getFirstOrderDate();

    //Ngày có đơn mới nhất
    @Query("SELECT MAX(o.orderDate) FROM Orders o")
    Optional<Date> getLastOrderDate();

    //Ngày có nhiều đơn nhất
    @Query("SELECT FUNCTION('DATE', o.orderDate) AS date, COUNT(o) AS orderCount " +
            "FROM Orders o GROUP BY FUNCTION('DATE', o.orderDate) " +
            "ORDER BY orderCount DESC")
    List<Map<String, Object>> getMostActiveDay();

    //Số đơn đã giao thành công
    @Query("SELECT COUNT(o) FROM Orders o WHERE o.orderStatus = :status")
    Optional<Long> countOrdersByStatus(@Param("status") OrderStatus status);

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

    @Query("SELECT o FROM Orders o WHERE o.orderStatus = 'DELIVERED'")
    List<Orders> findOrdersByStatusPaid();

    @Query("SELECT FUNCTION('DATE', o.orderDate) AS date, SUM(o.totalPrice) AS totalPrice " +
            "FROM Orders o WHERE o.orderStatus = 'DELIVERED' GROUP BY FUNCTION('DATE', o.orderDate) " +
            "ORDER BY FUNCTION('DATE', o.orderDate) ASC")
    List<Map<String, Object>> getOrderCountByDate();

    @Query("SELECT FUNCTION('MONTH', o.orderDate) AS month, SUM(o.totalPrice) AS totalPrice " +
            "FROM Orders o WHERE o.orderStatus = 'DELIVERED' AND FUNCTION('YEAR', o.orderDate) = :year " +
            "GROUP BY FUNCTION('MONTH', o.orderDate) ORDER BY FUNCTION('MONTH', o.orderDate) ASC")
    List<Map<String, Object>> getOrderPricesByMonth(@Param("year") int year);


    @Query("SELECT FUNCTION('YEAR', o.orderDate) AS year, SUM(o.totalPrice) AS totalPrice " +
            "FROM Orders o WHERE o.orderStatus = 'DELIVERED' " +
            "GROUP BY FUNCTION('YEAR', o.orderDate) ORDER BY FUNCTION('YEAR', o.orderDate) ASC")
    List<Map<String, Object>> getOrderPricesByYear();

    @Query("SELECT o FROM Orders o JOIN o.progresses p WHERE p.wareHouses.id = :warehouseId")
    List<Orders> findOrdersByWareHouseIdThroughProgress(long warehouseId);

    List<Orders> findByWareHouses_Id(long warehouseId);

    @Query("SELECT o FROM Orders o WHERE o.nearWareHouse = :location AND (o.orderStatus = 'PENDING' OR o.orderStatus = 'AWAITING_PAYMENT' OR o.orderStatus = 'PAID')")
    List<Orders> findOrdersByNearWareHouse(String location);
}

