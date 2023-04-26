package com.order.process.orderprcessingservice.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestFilter {

    private Long id;

    private String email;

    private String phoneNumber;

    private Double parcelWeight;

    private String country;

    private LocalDate creationDate;

}
