package com.SWP391.KoiXpress.Entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "`feedback`")
public class FeedBacks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @JsonIgnore
    boolean isDeleted = false;

    @NotNull(message = "you need to rate")
    @Min(value = 0)
    @Max(value = 5)
    float ratingScore;

    @Column(length = 200)
    String comment;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    Date createdTime;


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "feedBacks")
    @JsonManagedReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<FeedBackReply> replies;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"feedBacks", "orders", "blogs"})
    Users users;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    Orders orders;
}
