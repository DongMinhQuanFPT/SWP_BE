package com.SWP391.KoiXpress.Config;


import com.SWP391.KoiXpress.Service.AuthenticationService;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    Filter filter;



    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http)  throws Exception {
            return http
                    .cors().and()
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers(CorsUtils::isPreFlightRequest).permitAll() // Cho phép tất cả pre-flight requests
                            .requestMatchers("/**").permitAll()
                             .anyRequest().authenticated()
                    )
                    .userDetailsService(authenticationService)
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                    .build();
        }

    @Configuration
    public class FirebaseConfig {

        @Bean
        public FirebaseApp firebaseInit() throws IOException {
            // Update the path to the correct service account JSON file
            InputStream serviceAccount = new ClassPathResource("koi-fish-delivery-firebase-adminsdk-hsria-f878099696.json").getInputStream();

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            return FirebaseApp.initializeApp(options);
        }
    }
}
