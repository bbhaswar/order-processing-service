package com.order.process.orderprcessingservice.service;

import com.order.process.orderprcessingservice.entity.Order;
import com.order.process.orderprcessingservice.request.OrderRequestFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderProcessingService {
    Page<Order> fetchOrderDetails(OrderRequestFilter requestFilter, Pageable pageable);

    Double getTotalParcelWeightByCountry(String country);

    Long getTotalNumberOfRecordByCountry(String country);
}
