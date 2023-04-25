package com.order.process.orderprcessingservice.controller;

import com.order.process.orderprcessingservice.helper.OrderProcessAsyncHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/order-processing-service/v1")
@Slf4j
public class OrderProcessingController {

    @Autowired
    OrderProcessAsyncHelper helper;


    @PostMapping(value ="/process/order",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String processOrder(@RequestParam("file") MultipartFile multipartFile) {

        helper.startBatchProcessing(multipartFile);

        return "Started processing on file";
    }
}
