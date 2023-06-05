package com.order.process.orderprcessingservice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse implements Serializable {

    private boolean status;

    private String message;

    private Long numberOfRecords;

    private String responseTime;

}
