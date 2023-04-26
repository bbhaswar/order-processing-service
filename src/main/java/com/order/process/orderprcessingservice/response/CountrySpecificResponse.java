package com.order.process.orderprcessingservice.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CountrySpecificResponse {

    CommonResponse meta;

    Double totalWeight;

    String country;

    Long count;
}
