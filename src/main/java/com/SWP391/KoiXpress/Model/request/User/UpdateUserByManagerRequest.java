package com.SWP391.KoiXpress.Model.request.User;

import com.SWP391.KoiXpress.Entity.Enum.EmailStatus;
import com.SWP391.KoiXpress.Entity.Enum.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateUserByManagerRequest {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    String username;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    String password;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    String fullname;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    String address;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    String phone;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    String email;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    EmailStatus emailStatus;

    @Column(length = 200)
    String image;

    Role role;
    @Min(value = 0, message = "at least 0")
    long loyaltyPoint;

    boolean isDeleted;
}
