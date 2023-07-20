package com.ecommerce.zedshop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "category_id")
    private Long id;
    private String name;
    private boolean is_deleted;
    private boolean is_activated;

    public Category(String name){
        this.name = name;
        this.is_activated = true;
        this.is_deleted = false;
    }

}
