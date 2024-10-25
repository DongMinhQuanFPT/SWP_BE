package com.SWP391.KoiXpress.Repository;

import com.SWP391.KoiXpress.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthenticationRepository extends JpaRepository<Users, Long> {
    Users findUsersById(long id);
}
