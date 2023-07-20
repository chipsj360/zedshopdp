package com.ecommerce.zedshop.repository;

import com.ecommerce.zedshop.model.User;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
@ComponentScan
public interface UserRepository extends JpaRepository<User,Long> {


    @Query("SELECT u FROM User u WHERE u.email = :email")
    User findByEmail( @Param("email")String email);

    @Query("SELECT u FROM User u WHERE u.userName = :userName")
    User findByUsername(@Param("userName")String fullName);

    boolean existsByEmail(String email);

    boolean existsByUserName(String userName);

    User findByResetPasswordToken(String token);
}
