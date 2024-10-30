package com.SWP391.KoiXpress.Model.response.User;

import com.SWP391.KoiXpress.Entity.Enum.Role;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;


@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
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
