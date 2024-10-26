package com.SWP391.KoiXpress.Model.response.User;

import com.SWP391.KoiXpress.Entity.Enum.EmailStatus;
import com.SWP391.KoiXpress.Entity.Enum.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProfileManagerResponse {
    long id;

    Role role;

    String username;

    String fullname;

    String image;

    String address;

    String phone;

    String email;

    float balance;

    EmailStatus emailStatus;

    long loyaltyPoint;

    boolean isDeleted;
}
