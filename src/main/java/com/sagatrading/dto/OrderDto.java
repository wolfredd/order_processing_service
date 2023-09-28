package com.sagatrading.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sagatrading.model.OrderSide;
import lombok.Data;

@Data
public class OrderDto {
    private String userName;
    private double quantity;
    private OrderSide orderSide;
}
