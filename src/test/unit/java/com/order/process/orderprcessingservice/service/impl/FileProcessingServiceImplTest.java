package com.order.process.orderprcessingservice.service.impl;

import com.order.process.orderprcessingservice.constant.FileProcessingConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class FileProcessingServiceImplTest {

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private Job job;

    @InjectMocks
    private FileProcessingServiceImpl fileProcessingService;

    Map<String,String> customConfigMap = new HashMap<>();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);


        customConfigMap.put(FileProcessingConstant.FILE_TEMP_DIRECTORY,"src/test/unit/resources/temp");

        fileProcessingService.configMap = customConfigMap;
    }

    @Test
    public void processOrderFile_shouldTransferFileAndStartExecution() throws Exception {

        MockMultipartFile multipartFile = new MockMultipartFile(
                "file", "test.csv", MediaType.MULTIPART_FORM_DATA_VALUE, "Test file".getBytes());

        String tempStoragePath = "src/test/unit/resources/temp";
        String originalFileName = "test.csv";
        String tempStorageFileName = tempStoragePath + File.separator + originalFileName;

        File fileToImport = new File(tempStorageFileName);

        fileToImport.delete();

        when(jobLauncher.run(any(Job.class), any(JobParameters.class))).thenReturn(mock(JobExecution.class));

        fileProcessingService.processOrderFile(multipartFile);

        assertTrue(fileToImport.exists());
        verify(jobLauncher, Mockito.times(1)).run(any(Job.class), any(JobParameters.class));
    }

    @Test
    public void processOrderFileForScheduler_whenNoFileAvailable_shouldDoNothing() throws Exception {

        customConfigMap.put(FileProcessingConstant.FILE_TEMP_DIRECTORY,"src/test/unit/resources/empty");

        fileProcessingService.configMap = customConfigMap;

        fileProcessingService.processOrderFileForScheduler();

        verify(jobLauncher, Mockito.times(0)).run(any(Job.class), any(JobParameters.class));

    }

    @Test
    public void processOrderFileForScheduler_temp_path_null_shouldDoNothing() throws Exception {

        customConfigMap.put(FileProcessingConstant.FILE_TEMP_DIRECTORY,null);

        fileProcessingService.configMap = customConfigMap;

        fileProcessingService.processOrderFileForScheduler();

        verify(jobLauncher, Mockito.times(0)).run(any(Job.class), any(JobParameters.class));

    }


    @Test
    public void processOrderFileForScheduler_temp_path_empty_shouldDoNothing() throws Exception {

        customConfigMap.put(FileProcessingConstant.FILE_TEMP_DIRECTORY,"");

        fileProcessingService.configMap = customConfigMap;

        fileProcessingService.processOrderFileForScheduler();

        verify(jobLauncher, Mockito.times(0)).run(any(Job.class), any(JobParameters.class));

    }

    @Test
    public void processOrderFileForScheduler_whenFileAvailable_shouldStartExecution() throws Exception {

        String directoryPath = "src/test/unit/resources/latest";

        customConfigMap.put(FileProcessingConstant.FILE_TEMP_DIRECTORY,directoryPath);

        when(jobLauncher.run(any(Job.class), any(JobParameters.class))).thenReturn(mock(JobExecution.class));

        fileProcessingService.processOrderFileForScheduler();

        verify(jobLauncher, Mockito.times(1)).run(any(Job.class), any(JobParameters.class));

    }


    @Test
    public void getLatestFile_whenDirectoryContainsFiles_shouldReturnLatestFile() {
        // given
        String directoryPath = "src/test/unit/resources/latest";

        customConfigMap.put(FileProcessingConstant.FILE_TEMP_DIRECTORY,directoryPath);

        fileProcessingService.configMap = customConfigMap;

        File latestFile = fileProcessingService.getLatestFile();

        assertNotNull(latestFile);
        assertTrue(latestFile.getAbsoluteFile().getPath().contains("file2") );
    }

}
