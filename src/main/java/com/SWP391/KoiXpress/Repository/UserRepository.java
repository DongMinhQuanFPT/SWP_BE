package com.SWP391.KoiXpress.Repository;

import com.SWP391.KoiXpress.Entity.Enum.Role;
import com.SWP391.KoiXpress.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Users findUsersById(long Id);
    Users findUsersByUsername(String username);
    Users findUsersByEmail(String email);

    Optional<Users> findUsersByFullname(String fullname);

    Users findUsersByRole(Role role);
}
