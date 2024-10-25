package com.SWP391.KoiXpress.Repository;

import com.SWP391.KoiXpress.Entity.Enum.Role;
import com.SWP391.KoiXpress.Entity.Users;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Users findUsersById(long Id);

    Users findUsersByUsername(String username);

    Users findUsersByEmail(String email);

    Optional<Users> findUsersByFullname(String fullname);

    Users findUsersByRole(Role role);

    @Query("select count(a)  from Users a where a.role = :role")
    long countUsersByRole(Role role);

    @Query("SELECT u FROM Users u WHERE u.role = :role ORDER BY u.loyaltyPoint DESC")
    List<Users> findTopCustomersByLoyaltyPoints(@Param("CUSTOMER") Role role, Pageable pageable);

}
