package com.ecommerce.zedshop.repository;

import com.ecommerce.zedshop.model.Order;
import com.ecommerce.zedshop.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetail,Long> {
    @Query("select d.quantity from OrderDetail d inner join Order o on d.order.id=o.id")
    OrderDetail getOrderQuantity(Long orderId);
}
