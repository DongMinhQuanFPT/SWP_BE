package com.SWP391.KoiXpress.Config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;


@Configuration
public class FirebaseConfig {

    @Value("${fcm.credentials.file.path}")
    private String fcmCredentials;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        FirebaseOptions firebaseOptions = FirebaseOptions
                .builder()
                .setCredentials(GoogleCredentials.fromStream(new ClassPathResource(fcmCredentials).getInputStream()))
                .build();
        return FirebaseApp.initializeApp(firebaseOptions);
    }
}

