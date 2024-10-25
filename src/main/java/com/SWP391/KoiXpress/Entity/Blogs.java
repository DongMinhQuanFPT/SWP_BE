package com.SWP391.KoiXpress.Entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "`blog`")
public class Blogs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(length=200)
    String img;

    @Column(length = 2000)
    String post;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    boolean isDeleted =false;

    @ManyToOne
    @JoinColumn(name="user_id")
    Users users;
}
