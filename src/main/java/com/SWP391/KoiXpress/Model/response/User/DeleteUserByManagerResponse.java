package com.SWP391.KoiXpress.Model.response.User;

import com.SWP391.KoiXpress.Entity.Enum.Role;
import lombok.Data;


@Data
public class DeleteUserByManagerResponse {
    long id;
    String username;
    String fullname;
    String image;
    String address;
    String phone;
    String email;
    Role role;
    long loyaltyPoint;
    boolean isDeleted;
    String token;
}
