package com.sagatrading.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sagatrading.contract.PortfolioManager;
import com.sagatrading.exception.ProductNotFoundException;
import com.sagatrading.model.dto.StockDto;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;

@NoArgsConstructor
public class DummyPortfolioService extends PortfolioService {

    @Override
    public List<StockDto> getStocksByClientId(Integer clientId) {
        String stockJSONStr = "[{\"product\":\"AAPL\",\"quantity\":2},{\"product\":\"IBM\",\"quantity\":4},{\"product\":\"NFLX\",\"quantity\":6}]";;
        ObjectMapper stockMapper = new ObjectMapper();
        try {
            List<StockDto> stockList = stockMapper.readValue(stockJSONStr, new TypeReference<>() {
            });
            return stockList;
        } catch (JsonProcessingException ex) {
            System.out.println(ex);
            throw new RuntimeException("Could not process stocks");
        }
    }
}
