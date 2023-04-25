package com.order.process.orderprcessingservice.config;

import com.order.process.orderprcessingservice.entity.Order;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.ItemProcessor;

import java.util.Map;
import java.util.regex.Pattern;

public class OrderProcessor implements ItemProcessor<Order, Order> {

    @Resource
    Map<String, String> numberPatternCountryMap;

    @Override
    public Order process(Order item) throws Exception {

        String number = item.getPhoneNumber();

        if(StringUtils.isNotBlank(number)) {

            String country = numberPatternCountryMap.keySet().stream()
                    .filter(numberPattern -> Pattern.matches(numberPattern, number)).findFirst()
                    .map(numberPatternCountryMap::get).orElse(null);

            item.setCountry(country);
        }

        return item;
    }
}
