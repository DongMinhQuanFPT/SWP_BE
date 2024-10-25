package com.SWP391.KoiXpress.Model.response.Order;

import com.SWP391.KoiXpress.Entity.Enum.MethodTransPort;
import com.SWP391.KoiXpress.Entity.Enum.OrderStatus;
import com.SWP391.KoiXpress.Entity.Enum.PaymentMethod;
import com.SWP391.KoiXpress.Entity.OrderDetails;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.NumberFormat;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class AllOrderByCurrentResponse {
    long id;

    UUID trackingOrder = UUID.randomUUID();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    Date orderDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    Date deliveryDate;

    String originLocation;

    String nearWareHouse;

    String destinationLocation;

    @NumberFormat(pattern = "#.##")
    double totalPrice;

    int totalQuantity;

    int totalBox;
    @NumberFormat(pattern = "#.##")
    double totalDistance;

    @NumberFormat(pattern = "#.##")
    double distancePrice;

    @NumberFormat(pattern = "#.##")
    double discountPrice;

    @NumberFormat(pattern = "#.##")
    double totalVolume;

    String recipientInfo;

    String customerNotes;

    MethodTransPort methodTransPort;

    OrderStatus orderStatus;

    List<OrderDetails> orderDetails;
}
