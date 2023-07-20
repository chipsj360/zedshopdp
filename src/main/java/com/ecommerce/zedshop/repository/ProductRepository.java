package com.ecommerce.zedshop.repository;

import com.ecommerce.zedshop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    @Query(value = "select p from Product p inner join Category c on c.id = p.category.id where c.id = ?1 and p.is_deleted = false and p.is_activated = true")
    List<Product> getProductsInCategory(Long categoryId);
    @Query("select p from Product p where p.name like %?1% or p.description like %?1%")
    List<Product> searchProducts(String keyword);
}
