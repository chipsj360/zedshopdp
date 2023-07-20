package com.ecommerce.zedshop.service;

import com.ecommerce.zedshop.model.Role;
import com.ecommerce.zedshop.model.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private User user;
    private String userName;
    private String password;

    private String email;
    private List<GrantedAuthority>authorities;

    public CustomUserDetails(User user){
        this.userName=user.getUserName();
        this.email=user.getEmail();
        this.password=user.getPassword();
//        this.authorities= Arrays.stream(user.getRoles().split(",")).map(SimpleGrantedAuthority::new ).collect(Collectors.toList());
      this.user=user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<Role> roles = user.getRoles();
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        return authorities;
    }

  /*  @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }*/

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
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

    public boolean hasRole(String roleName) {
        return this.user.hasRole(roleName);
    }






}
