package com.SWP391.KoiXpress.Model.response.User;

import com.SWP391.KoiXpress.Entity.Enum.EmailStatus;
import com.SWP391.KoiXpress.Entity.Enum.Role;
import lombok.Data;

@Data
public class CreateUserByManagerResponse {
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
    Role role;
    long loyaltyPoint;

}
