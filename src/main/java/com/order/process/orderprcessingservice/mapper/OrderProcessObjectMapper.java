package com.order.process.orderprcessingservice.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class OrderProcessObjectMapper {

    static Logger log = LogManager.getLogger(OrderProcessObjectMapper.class);

    public static String mapToJsoString(Object object){
        try{
            return new ObjectMapper().writeValueAsString(object);
        }catch(JsonProcessingException e){
         log.info("Exception occurred while parsing object to string");
        }
        return null;
    }

}
