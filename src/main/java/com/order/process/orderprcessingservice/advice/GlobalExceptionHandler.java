package com.order.process.orderprcessingservice.advice;

import com.order.process.orderprcessingservice.constant.FileProcessingConstant;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@RestControllerAdvice
@Component
public class GlobalExceptionHandler {

    Logger log = LogManager.getLogger(GlobalExceptionHandler.class);

    @Resource
    Map<String, String> configMap;

    @ExceptionHandler(SizeLimitExceededException.class)
    public ResponseEntity<String> handleFileUploadError(SizeLimitExceededException e){
        String maxFileSize = configMap.get(FileProcessingConstant.MULTIPART_MAX_FILE_SIZE);
        log.info("Caught Max File Size Exceed Exception as file size is greater than -{}",maxFileSize);

        return ResponseEntity.badRequest().body("Could not process file as file size exceeds " +
                "the max permissible file size of :: "+maxFileSize);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleFileUploadError(IllegalArgumentException e){
        log.error("Exception occurred during processing",e);
        return ResponseEntity.badRequest().body("Exception occurred during processing :: "+e.getMessage());
    }

    @ExceptionHandler({NullPointerException.class, ArrayIndexOutOfBoundsException.class, TimeoutException.class,
            IOException.class, StackOverflowError.class})
    public ResponseEntity<String> handleFileUploadError(Throwable e){
        log.error("Exception occurred during processing",e);
        return ResponseEntity.internalServerError().body("Exception occurred during processing :: "+e.getMessage());
    }
}
