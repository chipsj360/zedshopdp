package com.ecommerce.zedshop.service;

import com.ecommerce.zedshop.exceptions.CustomerNotFoundException;
import com.ecommerce.zedshop.model.Role;
import com.ecommerce.zedshop.model.User;
import com.ecommerce.zedshop.model.dto.CustomerDto;
import com.ecommerce.zedshop.repository.RoleRepository;
import com.ecommerce.zedshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository repo;



    public String addUser(User user){
        user.setPassword( bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles(Arrays.asList(roleRepository.findByName("CUSTOMER")));
        repo.save(user);
        return "register_success";
    }

    public User findByUsername(String username) {
        return repo.findByUsername(username);
    }

public CustomerDto getUser(String username){

    CustomerDto customerDto = new CustomerDto();
    User user = repo.findByUsername(username);
    customerDto.setFirstName(user.getFirstName());
    customerDto.setLastName(user.getLastName());
    customerDto.setUsername(user.getUserName());
    customerDto.setPassword(user.getPassword());
    customerDto.setEmail(user.getEmail());

    return customerDto;

}

public User changePass(CustomerDto customerDto) {
    User user = repo.findByUsername(customerDto.getUsername());
    user.setPassword(customerDto.getPassword());
    return repo.save(user);
    }

    public User update(CustomerDto dto,CustomerDto customerDto) {
        User user = repo.findByUsername(dto.getUsername());
        user.setFirstName(customerDto.getFirstName());
        user.setLastName(customerDto.getLastName());
        user.setEmail(customerDto.getEmail());
        return repo.save(user);
    }


    public void deleteById(Long id){
        repo.deleteById(id);
    }

    public List<User> getAllUsers(){
        return repo.findAll();
    }

    public boolean isEmailExists(String email){
        return repo.existsByEmail(email);
    }

    public boolean isUsernameExists(String userName){
        return repo.existsByUserName(userName);
    }
    public void updateResetPasswordToken(String token, String email) throws CustomerNotFoundException {
        User user = repo.findByEmail(email);
        if (user != null) {
            user.setResetPasswordToken(token);
            repo.save(user);
        } else {
            throw new CustomerNotFoundException("Could not find any customer with the email " + email);
        }
    }

    public void updatePassword(User user, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);

        user.setResetPasswordToken(null);
        repo.save(user);
    }

    public User getByResetPasswordToken(String token) {
        return repo.findByResetPasswordToken(token);
    }
}
