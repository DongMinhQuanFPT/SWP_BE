package com.SWP391.KoiXpress.Service;


import com.SWP391.KoiXpress.Annotation.Trimmed;
import com.SWP391.KoiXpress.Entity.EmailDetail;
import com.SWP391.KoiXpress.Entity.Enum.EmailStatus;
import com.SWP391.KoiXpress.Entity.Enum.Role;
import com.SWP391.KoiXpress.Entity.Users;
import com.SWP391.KoiXpress.Exception.DuplicateEntity;
import com.SWP391.KoiXpress.Exception.EmailNotVerifiedException;
import com.SWP391.KoiXpress.Exception.EntityNotFoundException;
import com.SWP391.KoiXpress.Exception.NotFoundException;

import com.SWP391.KoiXpress.Model.request.Authen.*;

import com.SWP391.KoiXpress.Model.response.Authen.LoginGoogleResponse;
import com.SWP391.KoiXpress.Model.response.Authen.LoginResponse;

import com.SWP391.KoiXpress.Model.response.User.CreateUserByManagerResponse;
import com.SWP391.KoiXpress.Repository.AuthenticationRepository;
import com.SWP391.KoiXpress.Repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import java.util.Date;

@Service

public class AuthenticationService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    EmailService emailService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    TokenService tokenService;

    @Autowired
    AuthenticationRepository authenticationRepository;


    @Trimmed
    public CreateUserByManagerResponse register(RegisterRequest registerRequest) {
        Users users = modelMapper.map(registerRequest, Users.class);

        try {
            String originPassword = users.getPassword();
            users.setPassword(passwordEncoder.encode(originPassword));
            users.setRole(Role.CUSTOMER);

            Users newUsers = userRepository.save(users);
            EmailDetail emailDetail = new EmailDetail();
            emailDetail.setUsers(newUsers);
            emailDetail.setSubject("Verify your email");
            emailDetail.setLink("#");
            emailDetail.setCreateDate(new Date());
            boolean emailSent =  emailService.sendEmailVerify(emailDetail);
            if(emailSent) {
                newUsers.setEmailStatus(EmailStatus.VERIFIED);
                userRepository.save(newUsers);
            }
            return modelMapper.map(newUsers, CreateUserByManagerResponse.class);
        } catch (Exception e) {
            if (e.getMessage().contains(users.getEmail())) {
                throw new DuplicateEntity("Duplicate email");
            } else if (e.getMessage().contains(users.getPhone())) {
                throw new DuplicateEntity("Duplicate phone");
            } else {
                throw new NotFoundException("Unknown Error error");
            }

        }
    }

    @Trimmed
    public LoginResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            ));
            Users users = (Users) authentication.getPrincipal();
            LoginResponse loginResponse = modelMapper.map(users, LoginResponse.class);
            loginResponse.setToken(tokenService.generateToken(users));
            return loginResponse;
        } catch (Exception e) {
            throw new BadCredentialsException("UserName or password invalid!");
        }

    }

    @Trimmed
    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        Users users = userRepository.findUsersByEmail(forgotPasswordRequest.getEmail());
        if (users == null) {
            throw new EntityNotFoundException("Cant find your email account");
        }
        String token = tokenService.generateToken(users);
        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setUsers(users);
        emailDetail.setSubject("Reset Password");
        emailDetail.setLink("http://transportkoifish.online/reset-password?token=" + token);
        emailDetail.setCreateDate(new Date());
        emailService.sendEmailResetPassword(emailDetail);

    }

    @Trimmed
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        Users users = getCurrentUser();
        users.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
        try{
            userRepository.save(users);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }

    //security method
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users users = userRepository.findUsersByUsername(username);
        if(users !=null){
        }
        return users;
    }

    public Users getCurrentUser() {
        Users users = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authenticationRepository.findUsersById(users.getId());
    }

    public LoginGoogleResponse loginGoogle(LoginGoogleRequest loginGoogleRequest) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(loginGoogleRequest.getToken());
            String email = decodedToken.getEmail();
            Users user = userRepository.findUsersByEmail(email);
            String image = decodedToken.getPicture();
            String name = decodedToken.getName();

            if (user == null) {
                Users newUser = new Users();
                newUser.setFullname(decodedToken.getName() != null ? decodedToken.getName() : "Unknown User");
                newUser.setEmail(email);
                newUser.setUsername(name);
                newUser.setRole(Role.CUSTOMER);
                newUser.setPassword(null);
                newUser.setPhone(null);
                newUser.setEmailStatus(EmailStatus.VERIFIED);
                userRepository.save(newUser);
                return new LoginGoogleResponse(
                        tokenService.generateToken(newUser),
                        newUser.getId(),
                        newUser.getUsername(),
                        newUser.getFullname(),
                        image,
                        newUser.getAddress(),
                        newUser.getPhone(),
                        newUser.getEmail(),
                        newUser.getRole(),
                        newUser.getLoyaltyPoint(),
                        newUser.isDeleted()
                );
            }
            return new LoginGoogleResponse(
                    tokenService.generateToken(user),
                    user.getId(),
                    user.getUsername(),
                    user.getFullname(),
                    image,
                    user.getAddress(),
                    user.getPhone(),
                    user.getEmail(),
                    user.getRole(),
                    user.getLoyaltyPoint(),
                    user.isDeleted()
            );
        } catch (FirebaseAuthException e) {
            throw new RuntimeException("Invalid Google ID token.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to log in with Google.");
        }
    }
}


