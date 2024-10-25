package com.SWP391.KoiXpress.Model.request.User;

import com.SWP391.KoiXpress.Entity.Enum.Role;
import com.SWP391.KoiXpress.Model.request.Authen.RegisterRequest;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateUserByManagerRequest {
    RegisterRequest registerRequest;

    @Enumerated(EnumType.STRING)
    Role role;

    @Min(value = 0, message = "at least 0")
    long loyaltyPoint;

    boolean isDeleted;
}
