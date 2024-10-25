package com.SWP391.KoiXpress.Model.response.User;

import com.SWP391.KoiXpress.Entity.Enum.EmailStatus;
import lombok.Data;

@Data
public class AllUserResponse {
    long id;
    String username;
    String fullname;
    String password;
    String image;
    String address;
    String phone;
    String email;
    EmailStatus emailStatus;
    boolean isDeleted;
    String role;
    long loyaltyPoint;
}
