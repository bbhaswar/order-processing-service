package com.order.process.orderprcessingservice.service.impl;

import com.order.process.orderprcessingservice.constant.FileProcessingConstant;
import com.order.process.orderprcessingservice.service.FileProcessingService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class FileProcessingServiceImpl implements FileProcessingService {

    Logger log  = LogManager.getLogger(FileProcessingServiceImpl.class);

    @Resource
    Map<String, String> configMap;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Override
    public void processOrderFile(MultipartFile multipartFile) {

        InputStream inputStream = null;
        try {
            String tempStoragePath = configMap.get(FileProcessingConstant.FILE_TEMP_DIRECTORY);

            log.info("Processing file shared via api");
            String originalFileName = multipartFile.getOriginalFilename();
            String tempStorageFileName = tempStoragePath + File.separator + originalFileName;
            File fileToImport = new File(tempStorageFileName);
           // multipartFile.transferTo(fileToImport);

            inputStream = multipartFile.getInputStream();
            Path destinationPath = Paths.get(tempStorageFileName);
            Files.createDirectories(destinationPath.getParent());
            Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);

            startExecution(tempStorageFileName);

        } catch (NoSuchFileException e) {
            log.error("Destination directory does not exist", e);
        } catch (IOException e) {
            log.error("An error occurred while copying the file: " + e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("An error occurred while closing the input stream.", e);
                }
            }
        }
    }

    @Override
    public void processOrderFileForScheduler(){
        try {
            log.info("Processing file for scheduler");
            File fileToImport = getLatestFile();
            if (fileToImport == null) {
                log.info("No file available for processing");
                return;
            }
            String tempStorageFileName = fileToImport.getAbsolutePath();
            startExecution(tempStorageFileName);
        }catch (Exception e){
            log.error("Exception Occurred while processing file for scheduler", e);
        }
    }

    public void startExecution(String tempStorageFileName){
            try{
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("fullPathFileName", tempStorageFileName)
                    .addLong("startAt", System.currentTimeMillis()).toJobParameters();

            JobExecution execution = jobLauncher.run(job, jobParameters);
            log.info("Batch job operation successful");

            log.info("Status::"+ execution.getExitStatus());

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            log.error("Exception Occurred", e);
        }
    }

    public File getLatestFile() {
        String directoryPath = configMap.get(FileProcessingConstant.FILE_TEMP_DIRECTORY);
        if(StringUtils.isBlank(directoryPath)){
           return null;
        }
        File dir = new File(directoryPath);
        if (dir.isDirectory()) {
            Optional<File> opFile = Arrays.stream(Objects.requireNonNull(dir.listFiles(File::isFile)))
                    .max(Comparator.comparingLong(File::lastModified));

            if (opFile.isPresent()){
                return opFile.get();
            }
        }

        return null;
    }
}
