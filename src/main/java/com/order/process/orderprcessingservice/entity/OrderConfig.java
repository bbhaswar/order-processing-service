package com.order.process.orderprcessingservice.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_config")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderConfig {

    @Id
    private String name;

    private String value;
}
