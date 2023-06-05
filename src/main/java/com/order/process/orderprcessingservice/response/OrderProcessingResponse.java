package com.order.process.orderprcessingservice.response;


import com.order.process.orderprcessingservice.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderProcessingResponse implements Serializable {

    CommonResponse meta;

    List<Order> data;

    Pageable pageInfo;

}
