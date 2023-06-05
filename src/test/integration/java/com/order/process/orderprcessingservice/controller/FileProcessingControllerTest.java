package com.order.process.orderprcessingservice.controller;


import com.order.process.orderprcessingservice.advice.GlobalExceptionHandler;
import com.order.process.orderprcessingservice.helper.OrderProcessAsyncHelper;
import com.order.process.orderprcessingservice.service.FileProcessingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.Executor;

@WebMvcTest(FileProcessingController.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class FileProcessingControllerTest {

    private static final String FILE_PROCESS_BASE_URL = "/file-processing-service/v1";

    private static final String PROCESS_ORDER_URL = "/process/order";

    @Autowired
    WebApplicationContext context;

    @Autowired
    MockMvc mvc;

    @MockBean
    GlobalExceptionHandler globalExceptionHandler;

    @MockBean
    OrderProcessAsyncHelper helper;

    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void processOrder_test() throws Exception {

        MockMultipartFile mockFile = new MockMultipartFile("file", "test.csv",
                MediaType.MULTIPART_FORM_DATA_VALUE, "file content".getBytes());

        Mockito.doNothing().when(helper).startProcessing(Mockito.any(MultipartFile.class));

        MvcResult result = mvc.perform(MockMvcRequestBuilders.multipart(FILE_PROCESS_BASE_URL + PROCESS_ORDER_URL)
                .file(mockFile)).andReturn();

        String responseString = result.getResponse().getContentAsString();

        Assertions.assertNotNull(responseString);

        Assertions.assertEquals("Started processing on file", responseString);

        Mockito.verify(helper,Mockito.times(1))
                .startProcessing(Mockito.any(MultipartFile.class));
    }

    @Test
    public void processOrder_null_file_test() throws Exception {

        MvcResult result = mvc.perform(MockMvcRequestBuilders.multipart(FILE_PROCESS_BASE_URL + PROCESS_ORDER_URL)).andReturn();

        String responseString = result.getResponse().getContentAsString();

        Assertions.assertNotNull(responseString);

        Assertions.assertEquals("Invalid file format received as input is null", responseString);
    }

}
