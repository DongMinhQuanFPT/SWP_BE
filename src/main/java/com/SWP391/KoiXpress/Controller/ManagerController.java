package com.SWP391.KoiXpress.Controller;

import com.SWP391.KoiXpress.Entity.Boxes;
import com.SWP391.KoiXpress.Entity.WareHouses;
import com.SWP391.KoiXpress.Model.request.Box.CreateBoxRequest;
import com.SWP391.KoiXpress.Model.request.Box.UpdateBoxRequest;
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
import java.util.Map;

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

    @Autowired
    DashboardService dashboardService;


    //////////////////////Get-Profile-Manager///////////////////////////
    @GetMapping("/profile")
    public ResponseEntity<ProfileManagerResponse> getProfileManager(){
        ProfileManagerResponse managerResponse = userService.getProfileManager();
        return ResponseEntity.ok(managerResponse);
    }
    ////////////////////////////////////////////////////////////////////



    //////////////////////Create-User///////////////////////////
    @PostMapping("/user")
    public ResponseEntity<CreateUserByManagerResponse> createUserByManager(@Valid @RequestBody CreateUserByManagerRequest createUserByManagerRequest) {
        CreateUserByManagerResponse newUser = userService.create(createUserByManagerRequest);
        return ResponseEntity.ok(newUser);
    }
    ////////////////////////////////////////////////////////////



    //////////////////////Update-User///////////////////////////
    @PutMapping("/{userId}")
    public ResponseEntity<UpdateCustomerResponse> updateUserByManager(@PathVariable long userId, @Valid @RequestBody UpdateUserByManagerRequest updateUserByManagerRequest) {
        UpdateCustomerResponse updateUser = userService.update(userId, updateUserByManagerRequest);
        return ResponseEntity.ok(updateUser);
    }
    ////////////////////////////////////////////////////////////



    //////////////////////Get-All-User///////////////////////////
    @GetMapping("/allUser")
    public ResponseEntity<PagedResponse<LoginResponse>> getAllUser(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResponse<LoginResponse> pagedResponse = userService.getAllUser(page - 1, size);
        return ResponseEntity.ok(pagedResponse);
    }
    /////////////////////////////////////////////////////////////



    //////////////////////Delete-User///////////////////////////
    @DeleteMapping("/{userId}")
    public ResponseEntity<DeleteUserByManagerResponse> deleteUserByManager(@PathVariable long userId) {
        DeleteUserByManagerResponse deleteUser = userService.deleteByManager(userId);
        return ResponseEntity.ok(deleteUser);
    }
    ///////////////////////////////////////////////////////////



    //////////////////////Create-Box///////////////////////////
    @PostMapping("/box")
    public ResponseEntity<CreateBoxResponse> createBox(@Valid @RequestBody CreateBoxRequest createBoxRequest) {
        CreateBoxResponse box = boxService.create(createBoxRequest);
        return ResponseEntity.ok(box);
    }
    ///////////////////////////////////////////////////////////



    //////////////////////Update-Box///////////////////////////
    @PutMapping("/box")
    public ResponseEntity<Boxes> updateBox(@PathVariable long id, @Valid @RequestBody UpdateBoxRequest updateBoxRequest){
        Boxes boxes = boxService.update(id,updateBoxRequest);
        return ResponseEntity.ok(boxes);
    }
    //////////////////////////////////////////////////////////



    //////////////////////Delete-Box///////////////////////////
    @DeleteMapping("/box/{boxId}")
    public ResponseEntity<String> deleteBox(@PathVariable long boxId) {
        boxService.delete(boxId);
        return ResponseEntity.ok("Delete Box success");
    }
    ///////////////////////////////////////////////////////////



    //////////////////////Get-All-Box///////////////////////////
    @GetMapping("/allBox")
    public ResponseEntity<List<Boxes>> getAllBox() {
        return ResponseEntity.ok(boxService.getAllBox());
    }
    ////////////////////////////////////////////////////////////



    //////////////////////Get-All-BoxDetail///////////////////////////
    @GetMapping("/allBoxDetail")
    public ResponseEntity<PagedResponse<AllBoxDetailResponse>> getAllBoxDetail(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResponse<AllBoxDetailResponse> boxDetails = boxDetailService.getAllBox(page - 1, size);
        return ResponseEntity.ok(boxDetails);
    }
    //////////////////////////////////////////////////////////////////



    //////////////////////Create-WareHouse///////////////////////////
    @PostMapping("/wareHouse")
    public ResponseEntity<CreateWarehouseResponse> createWareHouse(@Valid @RequestBody CreateWareHouseRequest wareHouse) {
        CreateWarehouseResponse newWareHouse = wareHouseService.create(wareHouse);
        return ResponseEntity.ok(newWareHouse);
    }
    ////////////////////////////////////////////////////////////////



    //////////////////////Delete-WareHouse///////////////////////////
    @DeleteMapping("/wareHouse/{id}")
    public ResponseEntity<String> deleteWareHouse(@PathVariable long id) {
        wareHouseService.delete(id);
        return ResponseEntity.ok("Delete success");
    }
    ////////////////////////////////////////////////////////////////



    //////////////////////Get-All-WareHouse-Available///////////////////////////
    @GetMapping("/wareHouse/available")
    public ResponseEntity<List<WareHouses>> getAllWareHouseAvailable() {
        return ResponseEntity.ok(wareHouseService.getAllWareHouseAvailable());
    }
    ///////////////////////////////////////////////////////////////////////////



    //////////////////////Get-All-WareHouse-NotAvailable///////////////////////////
    @GetMapping("/wareHouse/notAvailable")
    public ResponseEntity<List<WareHouses>> getAllWareHouseNotAvailable() {
        return ResponseEntity.ok(wareHouseService.getAllWareHouseNotAvailable());
    }
    ///////////////////////////////////////////////////////////////////////////////



    //////////////////////Get-DashBroad///////////////////////////
    @GetMapping("/dashboardStats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }
    //////////////////////////////////////////////////////////////
}
