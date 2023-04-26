package com.order.process.orderprcessingservice.controller;

import com.order.process.orderprcessingservice.helper.OrderProcessAsyncHelper;
import com.order.process.orderprcessingservice.service.OrderProcessingService;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


@RestController
@RequestMapping("/file-processing-service/v1")
public class FileProcessingController {

    Logger log = LogManager.getLogger(FileProcessingController.class);

    @Autowired
    OrderProcessAsyncHelper helper;

    @PostMapping(value ="/process/order",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> processOrder(@RequestParam(value = "file",required = false) MultipartFile multipartFile) {

        log.info("Consuming multipart file for processing");
        if(multipartFile ==null){
            return ResponseEntity.ok().body("Invalid file format received as input is null");
        }
        helper.startBatchProcessing(multipartFile);

        return ResponseEntity.ok().body("Started processing on file");
    }

}
