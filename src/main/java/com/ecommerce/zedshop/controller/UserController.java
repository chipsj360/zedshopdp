package com.ecommerce.zedshop.controller;

import com.ecommerce.zedshop.model.Category;
import com.ecommerce.zedshop.model.Order;
import com.ecommerce.zedshop.model.User;
import com.ecommerce.zedshop.model.dto.CustomerDto;
import com.ecommerce.zedshop.model.dto.ProductDto;
import com.ecommerce.zedshop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;

    @GetMapping("/customers")
    public String getAllUsers(Model model){
        List<User>customers=userService.getAllUsers();
        model.addAttribute("customers", customers);
        return "customers";
    }

    @GetMapping("/delete-customer/{id}")
    public String deleteCustomer(@PathVariable("id") Long id)
    {
        userService.deleteById(id);
        return "dashboard";
    }

    @GetMapping("/account")
    public String profile(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        } else {
            String username = principal.getName();
            CustomerDto customer = userService.getUser(username);
            model.addAttribute("customer", customer);
            return "account";
        }
    }

    @PostMapping("/change-password")
    public String changePass(@RequestParam("oldPassword") String oldPassword,
                             @RequestParam("newPassword") String newPassword,
                             @RequestParam("repeatNewPassword") String repeatPassword,
                             RedirectAttributes attributes,
                             Model model,
                             Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        } else {
            CustomerDto customerDto =userService.getUser(principal.getName());
            if (passwordEncoder.matches(oldPassword, customerDto.getPassword())
                    && !passwordEncoder.matches(newPassword, oldPassword)
                    && !passwordEncoder.matches(newPassword, customerDto.getPassword())
                    && repeatPassword.equals(newPassword) && newPassword.length() >= 5) {
                customerDto.setPassword(passwordEncoder.encode(newPassword));
                userService.changePass(customerDto);
                attributes.addFlashAttribute("success", "Your password has been changed successfully!");
                return "redirect:/account";
            } else {
                model.addAttribute("message", "Your password is wrong");
                return "redirect:/account";
            }
        }
    }

    @PostMapping("/update-profile")
    public String updateProfile(@Valid @ModelAttribute("customer") CustomerDto customerDto,
                                BindingResult result,
                                RedirectAttributes attributes,
                                Model model,
                                Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        } else {
            String username = principal.getName();
            CustomerDto customer = userService.getUser(username);


            if (result.hasErrors()) {
                return "account";
            }
            userService.update(customer,customerDto);
            CustomerDto customerUpdate = userService.getUser(principal.getName());
            attributes.addFlashAttribute("success", "Update successfully!");
            model.addAttribute("customer", customerUpdate);
            return "redirect:/account";
        }
    }
    }
