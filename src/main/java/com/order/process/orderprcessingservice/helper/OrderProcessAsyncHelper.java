package com.order.process.orderprcessingservice.helper;

import com.order.process.orderprcessingservice.service.FileProcessingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class OrderProcessAsyncHelper {

    Logger log = LogManager.getLogger(OrderProcessAsyncHelper.class);

    @Autowired
    FileProcessingService fileProcessingService;

    @Async("orderProcessingAsyncExecutor")
    public void startBatchProcessing(MultipartFile multipartFile) {

        log.info("Starting async batch job operation");
        fileProcessingService.processOrderFile(multipartFile,false);
    }
}
