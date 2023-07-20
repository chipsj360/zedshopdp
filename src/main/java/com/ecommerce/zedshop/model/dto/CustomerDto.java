package com.ecommerce.zedshop.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDto {

    private Long id;
    private String firstName;

    private String lastName;
    private String username;

    private String password;

    private String email;
    private String phoneNumber;

    private String address;
    private String confirmPassword;

    private String image;
    private String country;
}
