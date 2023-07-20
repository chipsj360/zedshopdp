package com.ecommerce.zedshop.controller;

import com.ecommerce.zedshop.model.CartItem;
import com.ecommerce.zedshop.model.Product;
import com.ecommerce.zedshop.model.ShoppingCart;
import com.ecommerce.zedshop.model.User;
import com.ecommerce.zedshop.service.CustomUserDetailsService;
import com.ecommerce.zedshop.service.ProductService;
import com.ecommerce.zedshop.service.ShoppingCartService;
import com.ecommerce.zedshop.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Set;

@Controller
public class CartController {
    @Autowired
    private ShoppingCartService cartService;

    @Autowired
    private ProductService productService;

   @Autowired
    UserService userService;

    @GetMapping("/cart")
    public String cart(Model model, Principal principal,HttpSession session){
        if(principal == null){
            return "redirect:/login";
        }

        String username = principal.getName();
        User user = userService.findByUsername(username);
        ShoppingCart shoppingCart = user.getShoppingCart();
        if(shoppingCart == null || shoppingCart.getCartItem().isEmpty()){
            model.addAttribute("check", "No Items in the Cart!<br>Go to catalog to shop items");
            session.setAttribute("subTotal", 0);
            model.addAttribute("totalItems", 0);
        }else {
            session.setAttribute("totalItems", shoppingCart.getTotalItems());
            model.addAttribute("subTotal", shoppingCart.getTotalPrices());
            model.addAttribute("shoppingCart", shoppingCart);
        }
        return "cart";
    }

/* adding to cart*/
    @PostMapping("/add-to-cart")
    public String addItemToCart(
            @RequestParam("id") Long productId,
            @RequestParam(value = "quantity", required = false, defaultValue = "1") int quantity,
            Principal principal,
            HttpServletRequest request){

        if(principal == null){
            return "redirect:/login";
        }
        Product product = productService.getProductById(productId);
        String username = principal.getName();
        User user = userService.findByUsername(username);
        ShoppingCart cart = cartService.addItemToCart(product, quantity, user);
        return "redirect:/cart" ;

    }


    /* Updating The Cart */
    @RequestMapping(value = "/update-cart", method = RequestMethod.POST, params = "action=update")
    public String updateCart(@RequestParam("quantity") Integer quantity,
                             @RequestParam("id") Long productId,
                             Model model,
                             Principal principal
    ){

        if(principal == null){
            return "redirect:/login";
        }else{
            String username = principal.getName();
            User user = userService.findByUsername(username);
            Product product = productService.getProductById(productId);
            ShoppingCart cart = cartService.updateItemInCart(product, quantity, user);

            model.addAttribute("shoppingCart", cart);
            return "redirect:/cart";
        }

    }


    @RequestMapping(value = "/update-cart", method = RequestMethod.POST, params = "action=delete")
    public String deleteItemFromCart(@RequestParam("id") Long productId,
                                     Model model,
                                     Principal principal){
        if(principal == null){
            return "redirect:/login";
        }else{
            String username = principal.getName();
            User user = userService.findByUsername(username);
            Product product = productService.getProductById(productId);
            ShoppingCart cart = cartService.deleteItemFromCart(product,user);
            model.addAttribute("shoppingCart", cart);
            return "redirect:/cart";
        }

    }


}
