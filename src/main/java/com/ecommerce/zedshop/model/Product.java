package com.ecommerce.zedshop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String name;
    /*private Double price;*/
    private Date date;
    private String description;

    private double costPrice;
    private double salePrice;
    private int currentQuantity;
    @Lob
    @Column(columnDefinition = "VARBINARY")
    private  String image;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    private Category category;
    private boolean is_deleted;
    private boolean is_activated;
}
