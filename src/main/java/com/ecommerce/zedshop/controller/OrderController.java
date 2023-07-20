package com.ecommerce.zedshop.controller;

import com.ecommerce.zedshop.model.Order;
import com.ecommerce.zedshop.model.Product;
import com.ecommerce.zedshop.model.ShoppingCart;
import com.ecommerce.zedshop.model.User;
import com.ecommerce.zedshop.service.OrderService;
import com.ecommerce.zedshop.service.ProductService;
import com.ecommerce.zedshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
public class OrderController {

    @Autowired
    UserService userService;
    @Autowired
    OrderService orderService;

    @Autowired
    ProductService productService;

    @GetMapping("/check-out")
    public String checkout(Model model, Principal principal){
        if(principal == null){
            return "redirect:/login";
        }
        String username = principal.getName();
         User user= userService.findByUsername(username);

        if (user.getEmail().trim().isEmpty()
                || user.getUserName().trim().isEmpty()){

            model.addAttribute("user", user);
            model.addAttribute("error", "You must fill the information after checkout!");
            return "account";
        } else {
            model.addAttribute("user", user);
            ShoppingCart cart = user.getShoppingCart();/*we are getting the shopping cart id that is associated with
            the logged in user */
            if(cart == null || cart.getCartItem().isEmpty()){
                model.addAttribute("check", "Please Add some things to your cart!");
                model.addAttribute("totalItems", 0);
                model.addAttribute("subTotal", 0);
            }else{
                model.addAttribute("totalItems", cart.getTotalItems());
                model.addAttribute("subTotal", cart.getTotalPrices());
            }
            model.addAttribute("cart", cart);
        }

        return "checkout3";
    }
@RequestMapping(value="/buy/{id}" , method = {RequestMethod.PUT , RequestMethod.GET})
    public String buy(@PathVariable("id")Long id,Principal principal,Model model){
        if(principal==null){
            return "redirect:/login";
        }
        String username= principal.getName();
        User user=userService.findByUsername(username);

      Product product = productService.getProductById(id);
    model.addAttribute("user", user);
      model.addAttribute("product", product);
        return "checkout3";
    }

    @PostMapping("/save-order")
    public String saveOrder(Principal principal,
                            @RequestParam("phone_number")String phoneNumber,
                            @RequestParam("currency")String currency,
                            @RequestParam("country")String country) throws IOException {
        if (principal==null){
            return "redirect:/login";
        }
        String username = principal.getName();
        User user=userService.findByUsername(username);
        ShoppingCart cart=user.getShoppingCart();
        orderService.saveOrder(cart,phoneNumber,currency,country);
//       OrderService.pay(phone_number,amount,currency,country,txn);
        return "redirect:/order";
    }

    @RequestMapping(value="/save-direct-order/{id}")
    public String saveDirectOrder(@PathVariable("id")Long id, Principal principal){
        if (principal==null){
            return "redirect:/login";
        }
        String username = principal.getName();
        User user=userService.findByUsername(username);
        Product product=productService.getProductById(id);
        orderService.saveDirectOrder(user,product);

        return "redirect:/order";
    }
    @GetMapping("/order")
    public String order(Principal principal,Model model){
        if (principal==null){
            return "redirect:/login";
        }
        String username = principal.getName();
        User user=userService.findByUsername(username);
        List<Order>orderList= user.getOrders();
        model.addAttribute("orders",orderList);
        return "order";
    }



    @GetMapping("/admin-orders")
    public String findAllOrders(Principal principal,Model model){
        if (principal==null){
            return "redirect:/login";
        }
        String username = principal.getName();
        User user=userService.findByUsername(username);
        List<Order>orderList=orderService.findAllOrders();
        model.addAttribute("orders",orderList);
        return "admin-order";
    }




    @RequestMapping(value = "/accept-order/{id}", method = {RequestMethod.PUT , RequestMethod.GET})
    public String acceptOrder(@PathVariable("id")Long id, RedirectAttributes attributes){
        orderService.acceptOrder(id);
        return "redirect:/admin-orders";
            }

    @RequestMapping(value = "/cancel-order/{id}", method = {RequestMethod.PUT , RequestMethod.GET})
    public String cancelOrder(@PathVariable("id")Long id, RedirectAttributes attributes){
        orderService.cancelOrder(id);
        return "redirect:/admin-orders";
    }


    @RequestMapping(value = "/user-cancel-order/{id}", method = {RequestMethod.PUT , RequestMethod.GET})
    public String UsercancelOrder(@PathVariable("id")Long id, RedirectAttributes attributes){
        orderService.cancelOrder(id);
        return "redirect:/order";
    }


}
