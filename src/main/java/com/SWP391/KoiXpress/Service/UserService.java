package com.SWP391.KoiXpress.Service;

import com.SWP391.KoiXpress.Entity.EmailDetail;
import com.SWP391.KoiXpress.Entity.Enum.EmailStatus;
import com.SWP391.KoiXpress.Entity.Enum.Role;
import com.SWP391.KoiXpress.Entity.Users;
import com.SWP391.KoiXpress.Exception.*;
import com.SWP391.KoiXpress.Model.request.User.CreateUserByManagerRequest;
import com.SWP391.KoiXpress.Model.request.User.UpdateCustomerRequest;
import com.SWP391.KoiXpress.Model.request.User.UpdateUserByManagerRequest;
import com.SWP391.KoiXpress.Model.response.Authen.LoginResponse;
import com.SWP391.KoiXpress.Model.response.Paging.PagedResponse;
import com.SWP391.KoiXpress.Model.response.User.*;
import com.SWP391.KoiXpress.Repository.UserRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    EmailService emailService;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    TokenService tokenService;

    public UpdateCustomerResponse update(long userId, UpdateCustomerRequest updateCustomerRequest) {
        modelMapper.map(updateCustomerRequest, Users.class);
        Users oldUsers = getUserById(userId);
        try {
            oldUsers.setUsername(updateCustomerRequest.getUsername());

            String originPassword = updateCustomerRequest.getPassword();
            oldUsers.setPassword(passwordEncoder.encode(originPassword));

            oldUsers.setFullname(updateCustomerRequest.getFullname());

            oldUsers.setImage(updateCustomerRequest.getImage());

            oldUsers.setAddress(updateCustomerRequest.getAddress());

            oldUsers.setPhone(updateCustomerRequest.getPhone());

            oldUsers.setEmail(updateCustomerRequest.getEmail());

            //check mail truoc khi gui

            Users newUsers = userRepository.save(oldUsers);
            EmailDetail emailDetail = new EmailDetail();
            emailDetail.setUsers(newUsers);
            emailDetail.setSubject("Update Email");
            emailDetail.setLink("#");
            boolean emailSend = emailService.sendEmailVerify(emailDetail);
            if (emailSend) {
                //gui email xac thuc
                newUsers.setEmailStatus(EmailStatus.VERIFIED);
            } else {
                newUsers.setEmailStatus(EmailStatus.NOT_VERIFIED);
                throw new EmailNotVerifiedException("Email not verify. please provide a valid email.");
            }
            userRepository.save(newUsers);
            return modelMapper.map(newUsers, UpdateCustomerResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains(updateCustomerRequest.getFullname())) {
                throw new DuplicateEntity("Duplicate fullName");
            } else if (e.getMessage().contains(updateCustomerRequest.getPhone())) {
                throw new DuplicateEntity("Duplicate phone");
            } else {
                throw new NullPointerException("Can not find old user to update!");
            }
        }
    }

    public DeleteUserByUserResponse deleteByUser(long userId) {
        try {
            Users oldUsers = getUserById(userId);
            oldUsers.setDeleted(true);
            Users newUsers = userRepository.save(oldUsers);
            return modelMapper.map(newUsers, DeleteUserByUserResponse.class);

        } catch (Exception e) {
            // Xử lý các lỗi khác nếu cần
            e.printStackTrace();
            throw new EntityNotFoundException("User not found");
        }
    }

    public CreateUserByManagerResponse create(CreateUserByManagerRequest createUserByManagerRequest) {
        Users users = modelMapper.map(createUserByManagerRequest, Users.class);
        try {
            String originPassword = createUserByManagerRequest.getPassword();
            users.setPassword(passwordEncoder.encode(originPassword));
            users.setRole(createUserByManagerRequest.getRole());
            Users newUsers = userRepository.save(users);
            return modelMapper.map(newUsers, CreateUserByManagerResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains(users.getFullname())) {
                throw new DuplicateEntity("Duplicate fullName");
            } else if (e.getMessage().contains(users.getPhone())) {
                throw new DuplicateEntity("Duplicate phone");
            } else {
                throw new NotFoundException("Unknown Error ");
            }
        }
    }

    public UpdateCustomerResponse update(long userId, UpdateUserByManagerRequest updateUserByManagerRequest) {
        modelMapper.map(updateUserByManagerRequest, Users.class);
        Users currentUsers = authenticationService.getCurrentUser();
        Users oldUsers = getUserById(userId);
        if (currentUsers == oldUsers) {
            throw new AuthException("You cant update because this account is using");
        }
        try {
            oldUsers.setImage(updateUserByManagerRequest.getImage());
            oldUsers.setRole(updateUserByManagerRequest.getRole());
            oldUsers.setLoyaltyPoint(updateUserByManagerRequest.getLoyaltyPoint());
            oldUsers.setDeleted(updateUserByManagerRequest.isDeleted());
            Users newUsers = userRepository.save(oldUsers);
            return modelMapper.map(newUsers, UpdateCustomerResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotFoundException("Unknown Error");
        }
    }

    public DeleteUserByManagerResponse deleteByManager(long userId) {
        try {
            Users currentUsers = authenticationService.getCurrentUser();
            Users oldUsers = getUserById(userId);
            if(currentUsers == oldUsers){
                throw new AuthException("Can delete that user because user is using web");
            }
            oldUsers.setDeleted(true);
            Users newUsers = userRepository.save(oldUsers);
            return modelMapper.map(newUsers, DeleteUserByManagerResponse.class);

        } catch (Exception e) {
            // Xử lý các lỗi khác nếu cần
            e.printStackTrace();
            throw new EntityNotFoundException("User not found");
        }
    }

    public PagedResponse<LoginResponse> getAllUser(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Users> userPage = userRepository.findAll(pageRequest);
        List<LoginResponse> loginResponses = userPage.getContent().stream()
                .map(user -> modelMapper.map(user, LoginResponse.class))
                .collect(Collectors.toList());
        return new PagedResponse<>(
                loginResponses,
                page,
                size,
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isLast()
        );
    }

    public EachUserResponse getEachUserById(long id) {
        Users users = userRepository.findUsersById(id);
        if (users == null) {
            throw new EntityNotFoundException("User not found");
        }
        return modelMapper.map(users, EachUserResponse.class);
    }

    public EachUserResponse getProfileUser(){
        Users users = authenticationService.getCurrentUser();
        return modelMapper.map(users, EachUserResponse.class);
    }

    public ProfileManagerResponse getProfileManager(){
        Users manager = authenticationService.getCurrentUser();
        if(manager.getRole() != Role.MANAGER){
            throw new AuthException("Do not have permission");
        }
        return modelMapper.map(manager, ProfileManagerResponse.class);
    }

    public void sendAccountUser(String fullname){
        Users account = userRepository.findUsersByFullname(fullname).orElseThrow(() -> new NotFoundException("Can found that account"));
        if(account.getUsername().equals(account.getFullname()) && passwordEncoder.matches(account.getFullname(), account.getPassword() ) ){
            String token = tokenService.generateToken(account);
            EmailDetail emailDetail = new EmailDetail();
            emailDetail.setUsers(account);
            emailDetail.setLink("http://transportkoifish.online/login?token=" + token);
            emailDetail.setSubject("Account Info");
            emailService.sendEmailAccount(emailDetail);
        }
        throw new AuthException("Account can not send");
    }

    private Users getUserById(long userId) {
        Users oldUsers = userRepository.findUsersById(userId);
        if (oldUsers == null) {
            throw new EntityNotFoundException("User not found!");
        }
        if (oldUsers.isDeleted()) {
            throw new EntityNotFoundException("User not found!");
        }
        return oldUsers;
    }



}
