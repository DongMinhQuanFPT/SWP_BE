package com.SWP391.KoiXpress.Model.request.Order;


import com.SWP391.KoiXpress.Entity.Enum.DescribeOrder;
import com.SWP391.KoiXpress.Entity.Enum.MethodTransPort;
import com.SWP391.KoiXpress.Entity.Enum.PaymentMethod;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


import java.util.List;

@Data
public class CreateOrderRequest {
    @NotBlank(message = "need information of recipient")
    String recipientInfo;

    @NotBlank(message = "location start can not be blank")
    String originLocation;

    @NotBlank(message = "location end can not be blank")
    String destinationLocation;

    String customerNotes;

    @Enumerated(EnumType.STRING)
    DescribeOrder describeOrder;

    @Enumerated(EnumType.STRING)
    MethodTransPort methodTransPort;

    List<OrderDetailRequest> orderDetailRequestList;

}
