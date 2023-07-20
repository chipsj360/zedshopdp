package com.ecommerce.zedshop.exceptions;

public class CustomerNotFoundException extends RuntimeException {


    public CustomerNotFoundException(String message) {
        super(message);
    }
}
