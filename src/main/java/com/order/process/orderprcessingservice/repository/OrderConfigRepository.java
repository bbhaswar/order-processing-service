package com.order.process.orderprcessingservice.repository;

import com.order.process.orderprcessingservice.entity.OrderConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderConfigRepository extends JpaRepository<OrderConfig, String> {
}
