package com.ecommerce.zedshop.service;

import com.ecommerce.zedshop.model.*;
import com.ecommerce.zedshop.repository.CartItemRepository;
import com.ecommerce.zedshop.repository.OrderDetailsRepository;
import com.ecommerce.zedshop.repository.OrderRepository;
import com.ecommerce.zedshop.repository.ShoppingCartRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static com.ecommerce.zedshop.paymentApis.AirtelPay.pay;
import static com.ecommerce.zedshop.paymentApis.AirtelPay.token;


@Service
public class OrderService {

    @Autowired
    OrderDetailsRepository orderDetailsRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    ShoppingCartRepository shoppingCartRepository;
    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    ProductService productService;



//    public void saveOrder(ShoppingCart cart){
//        Order order = new Order();
//        order.setOrderStatus("PENDING");
//        order.setOrderDate(new Date());
//        order.setUser(cart.getUser());
//        order.setTotalPrice(cart.getTotalPrices());
//        List<OrderDetail>orderDetailList=new ArrayList<>();
//        for (CartItem item: cart.getCartItem()){
//            OrderDetail orderDetail = new OrderDetail();
//            orderDetail.setOrder(order);
//            orderDetail.setQuantity(item.getQuantity());
//            orderDetail.setProduct(item.getProduct());
//            orderDetail.setUnitPrice(item.getProduct().getCostPrice());
//            orderDetailsRepository.save(orderDetail);
//            orderDetailList.add(orderDetail);
//            cartItemRepository.delete(item);
//        }
//        order.setOrderDetailList(orderDetailList);
//        cart.setCartItem(new HashSet<>());
//        cart.setTotalItems(0);
//        cart.setTotalPrices(0);
//        shoppingCartRepository.save(cart);
//        orderRepository.save(order);
//    }

    public void saveOrder(ShoppingCart cart, String phoneNumber, String currency, String country) {
        if (cart == null) {
            throw new IllegalArgumentException("Cart cannot be null.");
        }

        // Checks if cart is empty and does not allow the user to check out.
        if (cart.getCartItem().isEmpty()) {
            throw new IllegalStateException("Cannot checkout with an empty cart.");
        }

        try {
            // Step 1: Get the access token
            String accessToken = token().getString("access_token");

            Double totalPrice = cart.getTotalPrices();

            // Step 2: Make the payment
            JSONObject paymentResult = pay(phoneNumber, totalPrice ,currency, country, accessToken);

            // Step 3: Process the payment result
            int statusCode = paymentResult.getInt("status");
            if (statusCode == 200) {
                JSONObject jsonData = paymentResult.getJSONObject("jsondata");
                // Payment successful, save the order
                Order order = new Order();
                order.setOrderStatus("PENDING");
                order.setOrderDate(new Date());
                order.setUser(cart.getUser());
                order.setTotalPrice(cart.getTotalPrices());
                List<OrderDetail> orderDetailList = new ArrayList<>();

                for (CartItem item : cart.getCartItem()) {
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrder(order);
                    orderDetail.setQuantity(item.getQuantity());
                    orderDetail.setProduct(item.getProduct());
                    orderDetail.setUnitPrice(item.getProduct().getCostPrice());

                    orderDetailsRepository.save(orderDetail);
                    orderDetailList.add(orderDetail);

                    cartItemRepository.delete(item);
                }
                order.setOrderDetailList(orderDetailList);
                cart.setCartItem(new HashSet<>());
                cart.setTotalItems(0);
                cart.setTotalPrices(0.00);
                shoppingCartRepository.save(cart);

                orderRepository.save(order);
            } else {
                // Payment failed, handle accordingly
                // ...
                throw new IllegalStateException("Failed to make payment for this item.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle IO exception
            // ...
        }
    }


    public void saveDirectOrder(User user,Product product){
        Order order = new Order();
        order.setOrderStatus("PENDING");
        order.setOrderDate(new Date());
        order.setUser(user);
        order.setTotalPrice(product.getCostPrice());

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setQuantity(1);
            orderDetail.setProduct(product);
            orderDetail.setUnitPrice(product.getCostPrice());
        orderDetailsRepository.save(orderDetail);
        orderRepository.save(order);
    }

    public void acceptOrder(Long id ){
        Order order=orderRepository.getReferenceById(id);
        order.setDeliveryDate(new Date());
        order.setOrderStatus("Shipping");
        for (OrderDetail detail : order.getOrderDetailList()){
            Product product=productService.getProductById(detail.getProduct().getId());
            product.setCurrentQuantity(product.getCurrentQuantity()-detail.getQuantity());
        }
        orderRepository.save(order);
    }

    public void cancelOrder(Long id){
        // check if the order status is equal to Shipping
        Order order = orderRepository.getReferenceById(id);
        if (order.getOrderStatus().equals("Shipping") ) {
            // set the order status cancelled and add the products back to inventory
            order.setOrderStatus("Cancelled");
            for (OrderDetail detail : order.getOrderDetailList()){
                Product product = productService.getProductById(detail.getProduct().getId());
                product.setCurrentQuantity(product.getCurrentQuantity() + detail.getQuantity());
            }
            // set a flag indicating the order was cancelled by the user
            order.setUserCanceled(true);
            orderRepository.save(order);
        } else {
            // Handle error or display message indicating order cannot be cancelled
            throw new IllegalArgumentException("Order cannot be cancelled");
        }
    }


    public List<Order> findAllOrders(){
        return orderRepository.findAll();
    }
}
