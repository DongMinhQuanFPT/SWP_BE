package com.SWP391.KoiXpress.Entity.Enum;

import lombok.Getter;

@Getter
public enum DescribeOrder {
    WHOLESALE_ORDER(0.05),

    RETAIL_ORDER(0);

    private final double discount;

    DescribeOrder(double discount){
        this.discount = discount;
    }
}
