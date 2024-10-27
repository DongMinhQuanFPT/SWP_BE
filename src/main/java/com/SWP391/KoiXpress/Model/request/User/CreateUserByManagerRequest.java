package com.SWP391.KoiXpress.Model.request.User;

import com.SWP391.KoiXpress.Entity.Enum.Role;
import com.SWP391.KoiXpress.Model.request.Authen.RegisterRequest;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserByManagerRequest {
    @NotBlank(message = "username can not be blank!")
    @Size(min = 6, message = "username must at least 6 character")
    String username;

    @NotBlank(message = "password can not be blank!")
    @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter.")
    @Size(min=6, message = "password at least 6 character!")
    String password;

    @NotBlank(message = "fullname can not be blank!")
    @Size(min = 1, message = "fullName at least 1 character!")
    String fullname;

    @Column(length = 200)
    String image;

    @Column(length = 200)
    String address;

    String phone;

    String email;

    @Enumerated(EnumType.STRING)
    Role role;

    @Min(value = 0, message = "at least 0")
    long loyaltyPoint;

    boolean isDeleted;
}
