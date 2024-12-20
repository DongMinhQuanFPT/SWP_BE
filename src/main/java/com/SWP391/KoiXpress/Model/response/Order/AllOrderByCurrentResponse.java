package com.SWP391.KoiXpress.Model.response.Order;

import com.SWP391.KoiXpress.Entity.Enum.*;
import com.SWP391.KoiXpress.Entity.OrderDetails;
import com.SWP391.KoiXpress.Model.response.Payment.PaymentResponse;
import com.SWP391.KoiXpress.Model.response.Progress.ProgressResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.NumberFormat;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
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

    @NumberFormat(pattern = "#.##")
    double totalBoxPrice;

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

    String failure_reason;

    MethodTransPort methodTransPort;

    OrderStatus orderStatus;

    PaymentResponse paymentResponse;

    List<OrderDetails> orderDetails;

    List<ProgressResponse> progresses;
}
