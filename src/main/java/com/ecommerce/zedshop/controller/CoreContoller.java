package com.ecommerce.zedshop.controller;


import com.ecommerce.zedshop.model.Product;
import com.ecommerce.zedshop.model.ShoppingCart;
import com.ecommerce.zedshop.model.User;
import com.ecommerce.zedshop.model.dto.CategoryDto;
import com.ecommerce.zedshop.repository.UserRepository;
import com.ecommerce.zedshop.service.CategoryService;
import com.ecommerce.zedshop.service.CustomUserDetailsService;
import com.ecommerce.zedshop.service.ProductService;
import com.ecommerce.zedshop.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
/*import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;*/
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class CoreContoller {
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService service;

    @Autowired
    CategoryService categoryService;
    @Autowired
    private UserRepository repo;
    @GetMapping("/")
    public String home(Model model, Principal principal, HttpSession session){

        if(principal != null){
            String username = principal.getName();
            User user = service.findByUsername(username);
            ShoppingCart cart = user.getShoppingCart();

            if(cart == null || cart.getCartItem().isEmpty()){
                model.addAttribute("check", "No Items in the Cart!<br>Go to catalog to shop items");
                session.setAttribute("subTotal", 0);
                model.addAttribute("totalItems", 0);
            }else {
                session.setAttribute("totalItems", cart.getTotalItems());
                model.addAttribute("subTotal", cart.getTotalPrices());
                model.addAttribute("shoppingCart", cart);
            }

        }else{
            session.removeAttribute("username");
        }

        List<Product> products = productService.getAllProduct();
        List<CategoryDto> categories = categoryService.getCategoryAndProduct();
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        return "index";
    }
    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("user", new User());
        return "register";
    }
    @PostMapping("/process_register")
    public String processRegistration(User user,Model model){

        if(service.isEmailExists(user.getEmail())){
            model.addAttribute("emailExists", true);
            return "register";
        }else if(service.isUsernameExists(user.getUserName())){
            model.addAttribute("usernameExists", true);
            return "register";
        }

        else{
            return service.addUser(user);
        }


    }

    @GetMapping("/login")
    public String logingPage(){
        return "login";
    }

    @GetMapping("/access_denied")
    public String accessDenied(){
        return "access_denied";
    }

     @GetMapping("/dashboard")
     @PreAuthorize("hasAuthority('ROLE_ADMIN')")
     public String viewAll(){
        return "dashboard";
     }


    @GetMapping("/category")
    public String shopByCategory(){
        return "category";
    }



}
