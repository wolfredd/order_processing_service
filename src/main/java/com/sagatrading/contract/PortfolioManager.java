package com.sagatrading.contract;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sagatrading.model.dto.StockDto;

import java.util.List;

public interface PortfolioManager {
    public StockDto getStockData(Integer clientId, String product);

    public List<StockDto> getStocksByClientId(Integer clientId);

    public boolean updateStockData(Integer clientId, String product, Integer quantity);

}
