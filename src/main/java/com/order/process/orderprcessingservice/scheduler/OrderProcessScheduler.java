package com.order.process.orderprcessingservice.scheduler;

import com.order.process.orderprcessingservice.service.FileProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class OrderProcessScheduler {

    @Autowired
    FileProcessingService fileProcessingService;

    @Scheduled(cron = "*/20 * * * * *")
    public void launchJob(){
        try {
            log.info("Starting cron job for file processing");
            fileProcessingService.processOrderFile(null, true);
            log.info("Successfully processed file");
        }catch (Exception e){
            log.error("Exception occurred while while processing file -{}", LocalDateTime.now(), e);
        }
    }

}
