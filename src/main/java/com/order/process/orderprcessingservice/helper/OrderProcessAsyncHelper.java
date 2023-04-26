package com.order.process.orderprcessingservice.helper;

import com.order.process.orderprcessingservice.service.FileProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Slf4j
public class OrderProcessAsyncHelper {

    @Autowired
    FileProcessingService fileProcessingService;

    @Async("orderProcessingAsyncExecutor")
    public void startBatchProcessing(MultipartFile multipartFile) {

        log.info("Starting async batch job operation");
        fileProcessingService.processOrderFile(multipartFile,false);
    }
}
