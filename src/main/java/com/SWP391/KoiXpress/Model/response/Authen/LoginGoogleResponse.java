package com.SWP391.KoiXpress.Model.response.Authen;

import com.SWP391.KoiXpress.Entity.Enum.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginGoogleResponse {
    String token;

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
}
