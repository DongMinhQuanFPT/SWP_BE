package com.SWP391.KoiXpress.Service;

import com.SWP391.KoiXpress.Model.response.Notification.NotificationKoiFishDeli;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public FirebaseMessaging firebaseMessaging;

    public NotificationService(FirebaseApp firebaseApp){
        this.firebaseMessaging = FirebaseMessaging.getInstance(firebaseApp);
    }

    public void sendNotification(NotificationKoiFishDeli notificationKoiFishDeli){
        Notification notification = Notification
                .builder()
                .setTitle(notificationKoiFishDeli.getTitle())
                .setBody(notificationKoiFishDeli.getMessage())
                .build();

        Message message = Message.builder()
                .setNotification(notification)
                .setToken(notificationKoiFishDeli.getFcmToken())
                .build();
        try{
            firebaseMessaging.send(message);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
