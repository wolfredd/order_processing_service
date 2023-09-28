package com.sagatrading.model.dto;

import lombok.*;

@Data
public class StockDto {
    private String stockName;
    private Integer quantity;
    private Integer portfolioId;

    private Integer id;
}
