package com.SWP391.KoiXpress.Model.request.Authen;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank(message = "password can not be blank!")
    @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter.")
    @Size(min=6, message = "password at least 6 character!")
    String password;

    @JsonIgnore
    String token;
}
