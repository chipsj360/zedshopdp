package com.ecommerce.zedshop.repository;

import com.ecommerce.zedshop.model.Category;
import com.ecommerce.zedshop.model.ShoppingCart;
import com.ecommerce.zedshop.model.dto.CategoryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart,Long> {


}
