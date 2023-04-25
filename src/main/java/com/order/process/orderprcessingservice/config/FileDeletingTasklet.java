package com.order.process.orderprcessingservice.config;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

@Slf4j
public class FileDeletingTasklet implements Tasklet, InitializingBean {

    //@Value("${order.file.temp.storage.path}")
    private String tempDirectory;

    //@Value("${order.file.processed.storage.path}")
    private String processedDirectory;


    //private String tempDirectory = "C:/Users/bbhas/source/order-processing-service/temp";
    //private String processedDirectory = "C:/Users/bbhas/source/order-processing-service/processed";

    @Override
    public RepeatStatus execute(StepContribution contribution,
                                ChunkContext chunkContext) throws Exception {
        File dir = new File(tempDirectory);
        Assert.state(dir.isDirectory(), "temp directory is not a valid directory");
        Assert.state(Files.isDirectory(Paths.get(processedDirectory)), "processedDirectoryPath is not a valid directory");

        File[] files = dir.listFiles();
        for (File tempFile : files) {
            // Move file from temp folder to processed folder
            Path source = Paths.get(tempFile.getPath());
            Path destination = Paths.get((processedDirectory+ File.separator + tempFile.getName()));
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);

            //delete the file from the TEMP_STORAGE
            boolean deleted = tempFile.delete();
            if (!deleted) {
                throw new UnexpectedJobExecutionException("Could not delete file " +
                        tempFile.getPath());
            }
        }
        return RepeatStatus.FINISHED;
    }

    public void setDirectoryResource(String tempDirectory, String processedDirectory) {
        this.tempDirectory = tempDirectory;
        this.processedDirectory = processedDirectory;

    }

    public void afterPropertiesSet() {
        Assert.state(tempDirectory != null, "temp directory must be set");
        Assert.state(processedDirectory != null, "temp directory must be set");

    }

}
