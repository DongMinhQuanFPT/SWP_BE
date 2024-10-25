package com.SWP391.KoiXpress.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "`report`")
public class Reports {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String reportType;
    Date generateDate;
    String dateRange;

    @OneToMany(mappedBy = "reports")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<Orders> orders;
}
