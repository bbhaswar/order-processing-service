package com.order.process.orderprcessingservice.controller;

import com.order.process.orderprcessingservice.entity.Order;
import com.order.process.orderprcessingservice.helper.OrderProcessHelper;
import com.order.process.orderprcessingservice.request.OrderRequestFilter;
import com.order.process.orderprcessingservice.service.OrderProcessingService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/order-processing-service/v1")
public class OrderProcessingController {

    Logger log = LogManager.getLogger(OrderProcessingController.class);
    @Autowired
    OrderProcessHelper orderProcessHelper;
    @Autowired
    OrderProcessingService service;

    @GetMapping(value = "/fetch/order", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> fetchOrder(
            @RequestParam(value = "id",required = false) Long id,
            @RequestParam(value = "email",required = false) String email,
            @RequestParam(value = "phoneNumber",required = false) String phoneNumber,
            @RequestParam(value = "parcelWeight",required = false) String parcelWeight,
            @RequestParam(value = "country",required = false) String country,
            @RequestParam(value = "creationDate",required = false) String creationDate,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy) {

        Instant start = Instant.now();

        log.info("Starting process to fetch order details based on order parameter");

        try {

            LocalDate formattedCreationDate = StringUtils
                    .isBlank(creationDate) ? null : LocalDate.parse(creationDate);

            Double weightVal = StringUtils
                    .isBlank(parcelWeight) ? null : Double.valueOf(parcelWeight);


            OrderRequestFilter requestFilter = OrderRequestFilter.builder()
                    .id(id)
                    .email(email)
                    .phoneNumber(phoneNumber)
                    .parcelWeight(weightVal)
                    .country(country)
                    .creationDate(formattedCreationDate)
                    .build();

            Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());

            Page<Order> pageableResponse = service.fetchOrderDetails(requestFilter, pageable);

            int numberOfRecord = pageableResponse.getSize();

            if (numberOfRecord == 0) {
                log.info("No record found");
                return ResponseEntity.ok().body(orderProcessHelper
                        .buildSuccessResponse(List.of(), durationInMillis(start), "No record found", null));
            } else {
                return ResponseEntity.ok().body(orderProcessHelper
                        .buildSuccessResponse(pageableResponse.getContent(), durationInMillis(start),
                                "Successfully retrieved record", pageableResponse.getPageable()));
            }
        } catch (Exception e) {
            log.error("Exception occurred", e);
            return ResponseEntity.internalServerError()
                    .body(orderProcessHelper.buildFailureResponse(durationInMillis(start),
                            "Exception Occurred while fetch order"));
        }
    }

    @GetMapping("/fetch/parcel-weight/country/{country}")
    public ResponseEntity<?> getTotalWeightByCountry(@PathVariable(value = "country") String country) {

        Instant start = Instant.now();

        try {

            Double totalWeight = service.getTotalParcelWeightByCountry(country);

            log.info("For {} Total parcel weight available is {} kg", country, totalWeight);

            return ResponseEntity.ok().body(orderProcessHelper
                    .buildSuccessResponse(totalWeight, durationInMillis(start),
                            "Successfully retrieved record", 1L, country));
        } catch (Exception e) {
            log.error("Exception occurred", e);
            return ResponseEntity.internalServerError()
                    .body(orderProcessHelper.buildFailureResponse(durationInMillis(start),
                            "Exception Occurred while fetch order", country));
        }

    }

    @GetMapping("/fetch/order/count/country/{country}")
    public ResponseEntity<?> getTotalOrderCountByCountry(@PathVariable(value = "country") String country) {

        Instant start = Instant.now();

        try {

            Long orderCount = service.getTotalNumberOfRecordByCountry(country);

            log.info("For {} Total number of order available in database is {}", country, orderCount);

            return ResponseEntity.ok().body(orderProcessHelper
                    .buildSuccessResponse(null, durationInMillis(start),
                            "Successfully retrieved record", orderCount, country));
        } catch (Exception e) {
            log.error("Exception occurred", e);
            return ResponseEntity.internalServerError()
                    .body(orderProcessHelper.buildFailureResponse(durationInMillis(start),
                            "Exception Occurred while fetch order", country));
        }

    }

    private String durationInMillis(Instant start) {
        Long durationInMillis = Duration.between(start, Instant.now()).toMillis();
        return durationInMillis + " ms";
    }

}
