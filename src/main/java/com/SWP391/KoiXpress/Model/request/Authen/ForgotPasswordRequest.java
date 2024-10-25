package com.SWP391.KoiXpress.Model.request.Authen;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequest {
    @NotBlank(message = "email can not be blank!")
    @Email(message = "Email not valid")
    String email;
}
