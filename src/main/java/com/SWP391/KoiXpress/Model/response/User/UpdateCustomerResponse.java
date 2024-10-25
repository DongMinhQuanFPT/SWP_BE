package com.SWP391.KoiXpress.Model.response.User;

import com.SWP391.KoiXpress.Entity.Enum.EmailStatus;
import com.SWP391.KoiXpress.Entity.Enum.Role;
import lombok.Data;

@Data
public class UpdateCustomerResponse {
    long id;
    Role role;
    String username;
    String password;
    String fullname;
    String image;
    String address;
    String phone;
    String email;
    EmailStatus emailStatus;
    boolean isDeleted;
    long loyaltyPoint;
}
