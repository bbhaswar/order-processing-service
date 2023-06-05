package com.order.process.orderprcessingservice.service.impl;

import com.order.process.orderprcessingservice.entity.Order;
import com.order.process.orderprcessingservice.repository.OrderProcessingRepository;
import com.order.process.orderprcessingservice.request.OrderRequestFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

public class OrderProcessingServiceImplTest {

    @InjectMocks
    OrderProcessingServiceImpl orderProcessingServiceImpl;

    @Mock
    OrderProcessingRepository repo;

    @BeforeEach
    void init(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void fetchOrderDetails_Test(){


        Mockito.doReturn(getPageObject()).when(repo).findOrderByOptionalParameter(Mockito.anyLong(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyDouble(),
                Mockito.anyString(),
                Mockito.any(),
                Mockito.any(Pageable.class));

        Page <Order> response = orderProcessingServiceImpl.fetchOrderDetails(getRequest(),getPageableObject());

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.get().findFirst().get().getCountry().equals("Uganda"));
    }

    @Test
    public void fetchOrderDetails_null_response_Test(){


        Mockito.doReturn(null).when(repo).findOrderByOptionalParameter(Mockito.anyLong(),
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyDouble(),
                Mockito.anyString(),
                Mockito.any(),
                Mockito.any(Pageable.class));

        Page <Order> response = orderProcessingServiceImpl.fetchOrderDetails(getRequest(),getPageableObject());

        Assertions.assertNull(response);
    }

    @Test
    public void getTotalParcelWeightByCountry_Test(){

        Mockito.doReturn(12.5).when(repo).findTotalWeightByCountry(Mockito.anyString());

        Double totalWeight = orderProcessingServiceImpl.getTotalParcelWeightByCountry("Uganda");

        Assertions.assertEquals(totalWeight,12.5);
    }

    @Test
    public void getTotalNumberOfRecordByCountry_Test(){

        Mockito.doReturn(100L).when(repo).countByCountry(Mockito.anyString());

        Long count = orderProcessingServiceImpl.getTotalNumberOfRecordByCountry("Uganda");

        Assertions.assertEquals(count,100L);

    }

    private Page <Order> getPageObject(){

        PageRequest.of(0, 10, Sort.by("country").ascending());

        return new PageImpl<>(List.of(Order.builder().id(1L).country("Uganda").build()),getPageableObject(), 0);
    }

    private Pageable getPageableObject(){
        return PageRequest.of(0, 10, Sort.by("country").ascending());
    }

    private OrderRequestFilter getRequest(){
        return OrderRequestFilter.builder()
                .id(1L).email("abc@abc.com").parcelWeight(12.5)
                .creationDate(LocalDate.now())
                .phoneNumber("123456")
                .country("Uganda")
                .build();
    }


}
