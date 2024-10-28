package com.SWP391.KoiXpress.Entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;


import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "dashboard_report")
public class DashboardReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     Long id;

    @Temporal(TemporalType.DATE)
    @Column(name = "report_date", nullable = false)
     Date reportDate;

    @Column(name = "total_revenue")
     Double totalRevenue;

    @Column(name = "average_order_value")
     Double averageOrderValue;

    @Column(name = "highest_order_value")
     Double highestOrderValue;

    @Column(name = "order_count")
     Long orderCount;

    @Column(name = "customers_count")
     Long customersCount;

    @Column(name = "sales_count")
     Long salesCount;

    @Column(name = "average_rating_score")
     Double averageRatingScore;

    @Column(name = "revenue_growth")
     Double revenueGrowth;

}

