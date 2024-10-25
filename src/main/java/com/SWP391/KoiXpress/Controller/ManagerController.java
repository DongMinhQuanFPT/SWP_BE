package com.SWP391.KoiXpress.Controller;

import com.SWP391.KoiXpress.Entity.Boxes;
import com.SWP391.KoiXpress.Entity.WareHouses;
import com.SWP391.KoiXpress.Model.request.Box.CreateBoxRequest;
import com.SWP391.KoiXpress.Model.request.User.CreateUserByManagerRequest;
import com.SWP391.KoiXpress.Model.request.User.UpdateUserByManagerRequest;
import com.SWP391.KoiXpress.Model.request.WareHouse.CreateWareHouseRequest;
import com.SWP391.KoiXpress.Model.response.Authen.LoginResponse;
import com.SWP391.KoiXpress.Model.response.Box.AllBoxDetailResponse;
import com.SWP391.KoiXpress.Model.response.Box.CreateBoxResponse;
import com.SWP391.KoiXpress.Model.response.Paging.PagedResponse;
import com.SWP391.KoiXpress.Model.response.WareHouse.CreateWarehouseResponse;
import com.SWP391.KoiXpress.Model.response.User.*;
import com.SWP391.KoiXpress.Service.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
@PreAuthorize("hasAuthority('MANAGER')")
public class ManagerController {

    @Autowired
    BoxService boxService;

    @Autowired
    BoxDetailService boxDetailService;

    @Autowired
    UserService userService;

    @Autowired
    WareHouseService wareHouseService;



    @PostMapping("/box")
    public ResponseEntity<CreateBoxResponse> createBox(@Valid @RequestBody CreateBoxRequest createBoxRequest) {
        CreateBoxResponse box = boxService.create(createBoxRequest);
        return ResponseEntity.ok(box);
    }

    @DeleteMapping("/box/{boxId}")
    public ResponseEntity<String> deleteBox(@PathVariable long boxId){
        boxService.delete(boxId);
        return ResponseEntity.ok("Delete Box success");
    }

    @GetMapping("/allBox")
    public ResponseEntity<List<Boxes>> getAllBox(){
        return ResponseEntity.ok(boxService.getAllBox());
    }

    @GetMapping("/allBoxDetail")
    public ResponseEntity<PagedResponse<AllBoxDetailResponse>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResponse<AllBoxDetailResponse> boxDetails = boxDetailService.getAllBox(page-1, size);
        return ResponseEntity.ok(boxDetails);
    }

    @PostMapping("/user")
    public ResponseEntity<CreateUserByManagerResponse> createUserByManager(@Valid @RequestBody CreateUserByManagerRequest createUserByManagerRequest) {
        CreateUserByManagerResponse newUser = userService.create(createUserByManagerRequest);
        return ResponseEntity.ok(newUser);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UpdateCustomerResponse> updateUserByManager(@PathVariable long userId, @Valid @RequestBody UpdateUserByManagerRequest updateUserByManagerRequest) {
        UpdateCustomerResponse updateUser = userService.update(userId, updateUserByManagerRequest);
        return ResponseEntity.ok(updateUser);
    }

    @GetMapping("/allUser")
    public ResponseEntity<PagedResponse<LoginResponse>> getAllUser(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResponse<LoginResponse> pagedResponse = userService.getAllUser(page - 1, size);
        return ResponseEntity.ok(pagedResponse);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<EachUserResponse> getEachUser(@PathVariable long userId) {
        EachUserResponse user = userService.getEachUserById(userId);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<DeleteUserByManagerResponse> deleteUserByManager(@PathVariable long userId) {
        DeleteUserByManagerResponse deleteUser = userService.deleteByManager(userId);
        return ResponseEntity.ok(deleteUser);
    }

    @PostMapping("/wareHouse")
    public ResponseEntity<CreateWarehouseResponse> create(@Valid @RequestBody CreateWareHouseRequest wareHouse) {
        CreateWarehouseResponse newWareHouse = wareHouseService.create(wareHouse);
        return ResponseEntity.ok(newWareHouse);
    }

    @DeleteMapping("/wareHouse/{id}")
    public ResponseEntity<String> delete(@PathVariable long id) {
        wareHouseService.delete(id);
        return ResponseEntity.ok("Delete success");
    }

    @GetMapping("/wareHouse/available")
    public ResponseEntity<List<WareHouses>> getAllWareHouseAvailable() {
        return ResponseEntity.ok(wareHouseService.getAllWareHouseAvailable());
    }

    @GetMapping("/wareHouse/notAvailable")
    public ResponseEntity<List<WareHouses>> getAllWareHouseNotAvailable() {
        return ResponseEntity.ok(wareHouseService.getAllWareHouseNotAvailable());
    }

}
