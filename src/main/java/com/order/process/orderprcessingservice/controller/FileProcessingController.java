package com.order.process.orderprcessingservice.controller;

import com.order.process.orderprcessingservice.constant.FileProcessingConstant;
import com.order.process.orderprcessingservice.helper.OrderProcessAsyncHelper;
import com.order.process.orderprcessingservice.service.OrderProcessingService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


@RestController
@RequestMapping("/file-processing-service/v1")
@Slf4j
@RestControllerAdvice
public class FileProcessingController {

    @Resource
    Map<String, String> configMap;

    @Autowired
    OrderProcessAsyncHelper helper;

    @Autowired
    OrderProcessingService service;

    @PostMapping(value ="/process/order",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> processOrder(@RequestParam(value = "file",required = false) MultipartFile multipartFile) {

        if(multipartFile ==null){
            return ResponseEntity.ok().body("Invalid file format received as input is null");
        }
        helper.startBatchProcessing(multipartFile);

        return ResponseEntity.ok().body("Started processing on file");
    }


    @ExceptionHandler(SizeLimitExceededException.class)
    public ResponseEntity<String> handleFileUploadError(SizeLimitExceededException e){
        String maxFileSize = configMap.get(FileProcessingConstant.MULTIPART_MAX_FILE_SIZE);
        log.info("Caught Max File Size Exceed Exception as file size is greater than -{}",maxFileSize);

        return ResponseEntity.ok().body("Could not process file as file size exceeds " +
                "the max permissible file size of :: "+maxFileSize);
    }

}
