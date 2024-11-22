package com.SWP391.KoiXpress.Controller;


import com.SWP391.KoiXpress.Model.response.Notification.NotificationKoiFishDeli;
import com.SWP391.KoiXpress.Service.NotificationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class NotificationController {

    @Autowired
    NotificationService notificationService;

//    @PostMapping
//    public ResponseEntity<?> sendNotification(@RequestBody NotificationKoiFishDeli notificationKoiFishDeli){
//        notificationService.sendNotification(notificationKoiFishDeli);
//        return ResponseEntity.ok("Send notification success");
//    }
}
