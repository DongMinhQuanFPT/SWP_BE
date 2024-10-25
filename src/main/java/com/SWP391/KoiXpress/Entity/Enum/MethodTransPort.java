package com.SWP391.KoiXpress.Entity.Enum;

import lombok.Getter;

@Getter
public enum MethodTransPort {
    FAST_DELIVERY(0.42),
    NORMAL_DELIVERY(0.22);

    private final double price;

    MethodTransPort(double price){
        this.price = price;
    }

}
