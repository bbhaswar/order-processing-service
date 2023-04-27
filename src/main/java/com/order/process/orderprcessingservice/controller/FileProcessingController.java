package com.order.process.orderprcessingservice.controller;

import com.order.process.orderprcessingservice.helper.OrderProcessAsyncHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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


@RestController
@RequestMapping("/file-processing-service/v1")
public class FileProcessingController {

    Logger log = LogManager.getLogger(FileProcessingController.class);

    @Autowired
    OrderProcessAsyncHelper helper;

    @Operation(summary = "Uploads a csv file", description = "Upload csv file for processing.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File started processing",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid file uploaded",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error while processing",
                    content = @Content) })
    @PostMapping(value ="/process/order",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> processOrder(
            @Parameter(description = "File to be processed")
            @RequestParam(value = "file",required = false) MultipartFile multipartFile) {

        log.info("Consuming multipart file for processing");
        if(multipartFile ==null){
            return ResponseEntity.ok().body("Invalid file format received as input is null");
        }
        helper.startBatchProcessing(multipartFile);

        return ResponseEntity.ok().body("Started processing on file");
    }

}
