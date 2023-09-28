package com.sagatrading.model.dto;

import com.sagatrading.dto.ExchangeDto;
import com.sagatrading.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderExchangeInfo {
    private ExchangeDto exchange;
    private Order order;
}
