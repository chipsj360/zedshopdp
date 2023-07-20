package com.ecommerce.zedshop.service;

import com.ecommerce.zedshop.model.User;
import com.ecommerce.zedshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository repo;
    @Override
    public UserDetails loadUserByUsername(String fullName) throws UsernameNotFoundException {
        User user;
        if (fullName.contains("@")) {
            user = repo.findByEmail(fullName);
        } else {
            user = repo.findByUsername(fullName);
        }

        if(user==null){
            throw new UsernameNotFoundException("user not found ");
        }
        return new CustomUserDetails(user);
    }


}
