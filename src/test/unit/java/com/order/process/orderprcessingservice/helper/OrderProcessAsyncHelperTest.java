package com.order.process.orderprcessingservice.helper;

import com.order.process.orderprcessingservice.service.FileProcessingService;
import com.order.process.orderprcessingservice.service.impl.FileProcessingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class OrderProcessAsyncHelperTest {

    @InjectMocks
    OrderProcessAsyncHelper orderProcessAsyncHelper;

    @Mock
    FileProcessingService fileProcessingService;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void startProcessing_test(){

        MockMultipartFile mockFile = new MockMultipartFile("file", "test.csv",
                MediaType.MULTIPART_FORM_DATA_VALUE, "file content".getBytes());

        Mockito.doNothing().when(fileProcessingService).processOrderFile(Mockito.any(MultipartFile.class));

        orderProcessAsyncHelper.startProcessing(mockFile);

        Mockito.verify(fileProcessingService, Mockito.times(1)).processOrderFile(mockFile);
    }

}
