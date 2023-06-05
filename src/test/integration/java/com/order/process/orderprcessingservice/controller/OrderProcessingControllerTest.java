package com.order.process.orderprcessingservice.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.order.process.orderprcessingservice.adapter.LocalDateTypeAdapter;
import com.order.process.orderprcessingservice.adapter.PageableTypeAdapter;
import com.order.process.orderprcessingservice.advice.GlobalExceptionHandler;
import com.order.process.orderprcessingservice.entity.Order;
import com.order.process.orderprcessingservice.helper.OrderProcessHelper;
import com.order.process.orderprcessingservice.request.OrderRequestFilter;
import com.order.process.orderprcessingservice.response.CountrySpecificResponse;
import com.order.process.orderprcessingservice.response.OrderProcessingResponse;
import com.order.process.orderprcessingservice.service.OrderProcessingService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;

@WebMvcTest(OrderProcessingController.class)
@ExtendWith(SpringExtension.class)
public class OrderProcessingControllerTest {

    private static final String ORDER_PROCESS_BASE_URL = "/order-processing-service/v1";

    private static final String FETCH_ORDER_URL = "/fetch/order";

    private static final String WEIGHT_BY_COUNTRY_URL = "/fetch/parcel-weight/country/{country}";
    private static final String ORDER_BY_COUNTRY_URL = "/fetch/order/count/country/{country}";

    @Autowired
    WebApplicationContext context;

    @Autowired
    MockMvc mvc;

    @SpyBean
    OrderProcessHelper orderProcessHelper;

    @MockBean
    GlobalExceptionHandler globalExceptionHandler;
    @MockBean
    OrderProcessingService service;


    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }


    @Test
    public void fetchOrder_test() throws Exception {

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("id", "1");
        queryParams.add("email", "test1@example.com");
        queryParams.add("phoneNumber", "1234");
        queryParams.add("parcelWeight", "12.25");
        queryParams.add("country", "Uganda");
        queryParams.add("creationDate", "2020-05-05");
        queryParams.add("pageNo", "0");
        queryParams.add("pageSize", "10");
        queryParams.add("sortBy", "id");

        Mockito.doReturn(getPageObject()).when(service).fetchOrderDetails(Mockito.any(OrderRequestFilter.class),
                Mockito.any(Pageable.class));


        MvcResult result = mvc.perform(MockMvcRequestBuilders.get(ORDER_PROCESS_BASE_URL + FETCH_ORDER_URL)
                .queryParams(queryParams)).andReturn();

        String responseStr = result.getResponse().getContentAsString();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .registerTypeAdapter(Pageable.class, new PageableTypeAdapter())
                .setPrettyPrinting()
                .create();


        OrderProcessingResponse response = gson.fromJson(responseStr, OrderProcessingResponse.class);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.getData().get(0).getId(),1l);

    }


    @Test
    public void fetchOrder_empty_test() throws Exception {

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("id", "1");
        queryParams.add("email", "test1@example.com");
        queryParams.add("phoneNumber", "1234");
        queryParams.add("parcelWeight", "12.25");
        queryParams.add("country", "Uganda");
        queryParams.add("creationDate", "2020-05-05");
        queryParams.add("pageNo", "0");
        queryParams.add("pageSize", "10");
        queryParams.add("sortBy", "id");

        Mockito.doReturn(getPageObject_empty()).when(service).fetchOrderDetails(Mockito.any(OrderRequestFilter.class),
                Mockito.any(Pageable.class));


        MvcResult result = mvc.perform(MockMvcRequestBuilders.get(ORDER_PROCESS_BASE_URL + FETCH_ORDER_URL)
                .queryParams(queryParams)).andReturn();

        String responseStr = result.getResponse().getContentAsString();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .registerTypeAdapter(Pageable.class, new PageableTypeAdapter())
                .setPrettyPrinting()
                .create();


        OrderProcessingResponse response = gson.fromJson(responseStr, OrderProcessingResponse.class);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.getData().size(),0);
        Assertions.assertEquals(response.getMeta().getMessage(),"No record found");

    }

    @Test
    public void fetchOrder_exception_test() throws Exception {

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("id", "1");
        queryParams.add("email", "test1@example.com");
        queryParams.add("phoneNumber", "1234");
        queryParams.add("parcelWeight", "12.25");
        queryParams.add("country", "Uganda");
        queryParams.add("creationDate", "2020-05-05");
        queryParams.add("pageNo", "0");
        queryParams.add("pageSize", "10");
        queryParams.add("sortBy", "id");

        Mockito.doThrow(new RuntimeException("Exception Occurred")).when(service).fetchOrderDetails(Mockito.any(OrderRequestFilter.class),
                Mockito.any(Pageable.class));


        MvcResult result = mvc.perform(MockMvcRequestBuilders.get(ORDER_PROCESS_BASE_URL + FETCH_ORDER_URL)
                .queryParams(queryParams)).andReturn();

        String responseStr = result.getResponse().getContentAsString();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .registerTypeAdapter(Pageable.class, new PageableTypeAdapter())
                .setPrettyPrinting()
                .create();


        OrderProcessingResponse response = gson.fromJson(responseStr, OrderProcessingResponse.class);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.getMeta().getNumberOfRecords(),0);
        Assertions.assertEquals(response.getMeta().getMessage(),"Exception Occurred while fetching order details");

    }


    private Page<Order> getPageObject() {

        return new PageImpl<>(List.of(Order.builder().id(1L).country("Uganda").build()),
                getPageableObject(0, 10, "country"), 0);
    }


    private Pageable getPageableObject(int page, int size, String sortBy) {
        return PageRequest.of(page, size, Sort.by(sortBy).ascending());
    }

    private Page<Order> getPageObject_empty() {
        return new PageImpl<>(List.of(), getPageableObject(0,10,"country"), 0);
    }


    @Test
    public void getTotalOrderCountByCountry_test() throws Exception {

        Mockito.doReturn(1200L).when(service).getTotalNumberOfRecordByCountry(Mockito.anyString());


        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get(ORDER_PROCESS_BASE_URL + ORDER_BY_COUNTRY_URL, "Uganda")).andReturn();

        String responseStr = result.getResponse().getContentAsString();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .registerTypeAdapter(Pageable.class, new PageableTypeAdapter())
                .setPrettyPrinting()
                .create();


        CountrySpecificResponse response = gson.fromJson(responseStr, CountrySpecificResponse.class);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1200L,response.getMeta().getNumberOfRecords());

    }

    @Test
    public void getTotalOrderCountByCountry_exception_test() throws Exception {


        Mockito.doThrow(new RuntimeException("Exception Occurred")).when(service).getTotalNumberOfRecordByCountry(Mockito.anyString());


        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get(ORDER_PROCESS_BASE_URL + ORDER_BY_COUNTRY_URL, "Uganda")).andReturn();

        String responseStr = result.getResponse().getContentAsString();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .registerTypeAdapter(Pageable.class, new PageableTypeAdapter())
                .setPrettyPrinting()
                .create();


        CountrySpecificResponse response = gson.fromJson(responseStr, CountrySpecificResponse.class);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.getMeta().getMessage(),"Exception Occurred while fetching total order count");


    }

    @Test
    public void getTotalWeightByCountry_test() throws Exception {

        Mockito.doReturn(1245.67).when(service).getTotalParcelWeightByCountry(Mockito.anyString());


        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get(ORDER_PROCESS_BASE_URL + WEIGHT_BY_COUNTRY_URL, "Uganda")).andReturn();

        String responseStr = result.getResponse().getContentAsString();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .registerTypeAdapter(Pageable.class, new PageableTypeAdapter())
                .setPrettyPrinting()
                .create();


        CountrySpecificResponse response = gson.fromJson(responseStr, CountrySpecificResponse.class);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1245.67,response.getTotalWeight().doubleValue());

    }

    @Test
    public void getTotalWeightByCountry_exception_test() throws Exception {


        Mockito.doThrow(new RuntimeException("Exception Occurred")).when(service).getTotalParcelWeightByCountry(Mockito.anyString());


        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get(ORDER_PROCESS_BASE_URL + WEIGHT_BY_COUNTRY_URL, "Uganda")).andReturn();

        String responseStr = result.getResponse().getContentAsString();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .registerTypeAdapter(Pageable.class, new PageableTypeAdapter())
                .setPrettyPrinting()
                .create();


        CountrySpecificResponse response = gson.fromJson(responseStr, CountrySpecificResponse.class);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.getMeta().getMessage(),"Exception Occurred while fetching total order weight");


    }


}
