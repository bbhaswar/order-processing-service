package com.order.process.orderprcessingservice.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileProcessingService {

    void processOrderFile(MultipartFile multipartFile, boolean isScheduler);
}
