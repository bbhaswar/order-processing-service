package com.order.process.orderprcessingservice.helper;

import com.order.process.orderprcessingservice.entity.Order;
import com.order.process.orderprcessingservice.response.CommonResponse;
import com.order.process.orderprcessingservice.response.CountrySpecificResponse;
import com.order.process.orderprcessingservice.response.OrderProcessingResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderProcessHelper {

    public OrderProcessingResponse buildSuccessResponse(List<Order> orderList, String responseTime, String message, Pageable pageable){
        return OrderProcessingResponse.builder()
                .meta(CommonResponse.builder()
                        .status(true)
                        .message(message)
                        .responseTime(responseTime)
                        .numberOfRecords((long) orderList.size()).build())
                .pageInfo(pageable)
                .data(orderList).build();
    }

    public OrderProcessingResponse buildFailureResponse(String responseTime, String message){
        return OrderProcessingResponse.builder()
                .meta(CommonResponse.builder()
                        .status(false)
                        .message(message)
                        .responseTime(responseTime)
                        .numberOfRecords(0L).build())
                .data(List.of()).build();
    }

    public CountrySpecificResponse buildSuccessResponse(Double totalWeight, String responseTime, String message,
                                                        Long count, String country){
        return CountrySpecificResponse.builder()
                .meta(CommonResponse.builder()
                        .status(true)
                        .message(message)
                        .responseTime(responseTime).numberOfRecords(count)
                        .build())
                .count(count)
                .country(country)
                .totalWeight(totalWeight)
                .build();
    }

    public CountrySpecificResponse buildFailureResponse(String responseTime, String message, String country){
        return CountrySpecificResponse.builder()
                .meta(CommonResponse.builder()
                        .status(true)
                        .message(message)
                        .responseTime(responseTime)
                        .build())
                .country(country)
                .build();
    }
}
