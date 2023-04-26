package com.order.process.orderprcessingservice.service.impl;

import com.order.process.orderprcessingservice.entity.Order;
import com.order.process.orderprcessingservice.mapper.OrderProcessObjectMapper;
import com.order.process.orderprcessingservice.repository.OrderProcessingRepository;
import com.order.process.orderprcessingservice.request.OrderRequestFilter;
import com.order.process.orderprcessingservice.service.OrderProcessingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrderProcessingServiceImpl implements OrderProcessingService {

    Logger log  = LogManager.getLogger(OrderProcessingServiceImpl.class);

    @Autowired
    OrderProcessingRepository repo;

    @Override
    public Page<Order> fetchOrderDetails(OrderRequestFilter requestFilter, Pageable pageable){

        log.info("Fetching order based on filter::{}", OrderProcessObjectMapper.mapToJsoString(requestFilter));

        Page<Order> pageableOrderList = repo.findOrderByOptionalParameter(
                requestFilter.getId(),
                requestFilter.getEmail(),
                requestFilter.getPhoneNumber(),
                requestFilter.getParcelWeight(),
                requestFilter.getCountry(),
                requestFilter.getCreationDate(),
                pageable);

        log.info("Acquired pageable order list with ::{} records",pageableOrderList.getSize());
        return pageableOrderList;
    }

    @Override
    public Double getTotalParcelWeightByCountry(String country){

        log.info("Fetching total weight based on country::{}",country);

        Double totalWeight  = repo.findTotalWeightByCountry(country);

        log.info("Total weight for country {} is ::{}",country, totalWeight);

        return totalWeight;
    }

    @Override
    public Long getTotalNumberOfRecordByCountry(String country){


        log.info("Fetching order count based on country::{}",country);

        Long count  = repo.countByCountry(country);

        log.info("Total order count for country {} is ::{}",country, count);

        return count;
    }



}
