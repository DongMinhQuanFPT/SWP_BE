package com.SWP391.KoiXpress.Entity;

import com.SWP391.KoiXpress.Entity.Enum.EmailStatus;
import com.SWP391.KoiXpress.Entity.Enum.Role;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "`user`")
public class Users implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Enumerated(EnumType.STRING)
    Role role;

    @NotBlank(message = "username can not be blank!")
    @Size(min = 6, message = "username must at least 6 characters")
    String username;

    @Pattern(
            regexp = ".*[A-Z].*",
            message = "Password must contain at least one uppercase letter."
    )
    @Size(min = 6, message = "password must be at least 6 characters!")
    String password;

    @NotBlank(message = "fullname can not be blank!")
    @Size(min = 1, message = "fullName must be at least 1 character!")
    String fullname;

    @Column(length = 200)
    String image;

    @Column(length = 200)
    String address;

    @Pattern(
            regexp = "(84|0[3|5|7|8|9])\\d{8}",
            message = "Phone number must start with 84 or a valid Vietnamese prefix, followed by 8 digits."
    )
    @Column(unique = true)
    String phone;

    @NotBlank(message = "email can not be blank!")
    @Email(message = "Email not valid")
    @Column(unique = true)
    String email;

    @Enumerated(EnumType.STRING)
    EmailStatus emailStatus = EmailStatus.NOT_VERIFIED;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Min(value = 0, message = "Loyalty points must be at least 0.")
    long loyaltyPoint;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    boolean isDeleted = false;

    @OneToMany(mappedBy = "users")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<Blogs> blogs;

    @OneToMany(mappedBy = "users")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonManagedReference
    List<FeedBacks> feedBacks;

    @OneToMany(mappedBy = "users")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    List<Orders> orders;

    @OneToMany(mappedBy = "from")
    Set<Transactions> transactionFrom;

    @OneToMany(mappedBy = "to")
    Set<Transactions> transactionTo;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(this.role.toString()));
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
