package com.order.process.orderprcessingservice.repository;

import com.order.process.orderprcessingservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderProcessingRepository extends JpaRepository<Order,Long> {
}
