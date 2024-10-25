package com.SWP391.KoiXpress.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class FeedBackReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    String replyContent;
    String repliedBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    Date replyDate;

    @ManyToOne
    @JoinColumn(name = "feed_back_id")
    @JsonBackReference
    FeedBacks feedBacks;


}
