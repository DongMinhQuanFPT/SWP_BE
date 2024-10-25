package com.SWP391.KoiXpress.Model.response.Authen;
import com.SWP391.KoiXpress.Entity.Enum.Role;
import lombok.Data;


@Data
public class LoginResponse {
    //tra ve thong tin User
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
