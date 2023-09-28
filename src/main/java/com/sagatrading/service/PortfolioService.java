package com.sagatrading.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sagatrading.contract.PortfolioManager;
import com.sagatrading.exception.ProductNotFoundException;
import com.sagatrading.model.dto.StockDto;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@NoArgsConstructor
public class PortfolioService implements PortfolioManager {

    @Value("${api.portfolio.baseurl}")
    private String portfolioBaseUrl;

    @Autowired
    RestClientService restClientService;

    public PortfolioService(RestClientService restClientService) {
        this.restClientService = restClientService;
    }

    public List<StockDto> getStocksByClientId(Integer clientId) {
        // Make request to portfolio service
        ResponseEntity response = restClientService.makeAPIRequest(portfolioBaseUrl + "/getstocksbyclientid/" + clientId, HttpMethod.GET, "");
        String stockJSONStr = Objects.requireNonNull(response.getBody()).toString();
        System.out.println(stockJSONStr);
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

    public void updateClientStockQuantity(int stockId, int quantity) {
        ResponseEntity response = restClientService.makeAPIRequest(portfolioBaseUrl + "/addtostock/" + stockId + "?quantity=" + quantity , HttpMethod.POST, "");
        System.out.println(response);
    }

    @Override
    public StockDto getStockData(Integer clientId, String product) {
            List<StockDto> stocks = getStocksByClientId(clientId);
            List<StockDto> foundStock = stocks.stream().filter(stock -> stock.getStockName().equals(product)).toList();
            if (foundStock.size() < 1) {
                throw new ProductNotFoundException("You do not own any " + product + " stock");
            }
            return foundStock.get(0);
    }

    @Override
    public boolean updateStockData(Integer clientId, String product, Integer quantity) {
        List<StockDto> stocks = getStocksByClientId(clientId);
        List<StockDto> foundStock = stocks.stream().filter(stock -> stock.getStockName().equals(product)).toList();
        if (foundStock.isEmpty()) return false;
        StockDto stock = foundStock.get(0);
        updateClientStockQuantity(stock.getId(), quantity );
        return true;
    }
}
