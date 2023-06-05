package com.order.process.orderprcessingservice.scheduler;

import com.order.process.orderprcessingservice.service.FileProcessingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
public class OrderProcessScheduler {

    @Autowired
    FileProcessingService fileProcessingService;
    Logger log = LogManager.getLogger(OrderProcessScheduler.class);

    @Scheduled(cron = "*/20 * * * * *")
    public void launchJob(){
        try {
            log.info("Starting cron job for file processing");
            fileProcessingService.processOrderFileForScheduler();
            log.info("Successfully processed file");
        }catch (Exception e){
            log.error("Exception occurred while while processing file -{}", LocalDateTime.now(), e);
        }
    }

}
