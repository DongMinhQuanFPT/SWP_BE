package com.SWP391.KoiXpress.Controller;


import com.SWP391.KoiXpress.Model.request.Authen.*;
import com.SWP391.KoiXpress.Model.response.Authen.LoginGoogleResponse;
import com.SWP391.KoiXpress.Model.response.Authen.LoginResponse;
import com.SWP391.KoiXpress.Model.response.User.CreateUserByManagerResponse;
import com.SWP391.KoiXpress.Service.AuthenticationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/authentication")
@CrossOrigin("*")
@SecurityRequirement(name="api")
public class AuthenticationController {

    @Autowired
    AuthenticationService authenticationService;

    //////////////////////Register///////////////////////////
    @PostMapping("/register")
    public ResponseEntity<CreateUserByManagerResponse> register(@Valid @RequestBody RegisterRequest registerRequest){
        CreateUserByManagerResponse newUser =  authenticationService.register(registerRequest);
        return ResponseEntity.ok(newUser);
    }
    /////////////////////////////////////////////////////////



    //////////////////////Login/////////////////////////////
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest){
        LoginResponse newUser = authenticationService.login(loginRequest);
        return ResponseEntity.ok(newUser);
    }
    ////////////////////////////////////////////////////////



    //////////////////////Forgot-Password///////////////////////////
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest){
        authenticationService.forgotPassword(forgotPasswordRequest);
        return ResponseEntity.ok("Check your email!");
    }
    ///////////////////////////////////////////////////////////////



    //////////////////////Reset-Password///////////////////////////
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        authenticationService.resetPassword(resetPasswordRequest);
        return ResponseEntity.ok("Reset password successfully");
    }
    //////////////////////////////////////////////////////////////



    //////////////////////Login-Google///////////////////////////
    @PostMapping("/login-google")
    public ResponseEntity<?> googleLogin(@RequestBody LoginGoogleRequest loginGoogleRequest) {
        try {
            LoginGoogleResponse response = authenticationService.loginGoogle(loginGoogleRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
    ////////////////////////////////////////////////////////////

}
