package com.SWP391.KoiXpress.Entity.Enum;

import lombok.Getter;

@Getter
public enum MethodTransPort {
    FAST_DELIVERY(11126.8),
    NORMAL_DELIVERY(5581.4);

    private final double price;

    MethodTransPort(double price){
        this.price = price;
    }

}
