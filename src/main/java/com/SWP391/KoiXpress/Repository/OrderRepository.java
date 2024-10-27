package com.SWP391.KoiXpress.Repository;

import com.SWP391.KoiXpress.Entity.Enum.OrderStatus;
import com.SWP391.KoiXpress.Entity.Orders;
import com.SWP391.KoiXpress.Entity.Users;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders,Long> {
    Orders findOrdersById(long Id);

    List<Orders> findOrdersByUsers(Users users);

    List<Orders> findOrdersByOrderStatus(OrderStatus orderStatus);

    @Query("SELECT o FROM Orders o ORDER BY o.totalPrice DESC")
    List<Orders> findTopOrdersByTotalPrice(Pageable pageable);

    @Query("SELECT SUM(o.totalPrice) FROM Orders o WHERE o.orderStatus = 'PAID'")
    Double getTotalPriceOfPaidOrders();


}
