package com.sagatrading.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sagatrading.contract.PortfolioManager;
import com.sagatrading.model.dto.StockDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.mockito.Mockito.when;

public class PortfolioServiceTest {
    PortfolioManager portfolioService;

    @Mock
    RestClientService restClientService = Mockito.mock(RestClientService.class);

    @Mock
    ResponseEntity responseEntity = Mockito.mock(ResponseEntity.class);

    @BeforeEach
    void setUp() {
        portfolioService = new PortfolioService(restClientService);
    }

    @Test
    @DisplayName("should return a list of client stocks")
    void testShouldReturnListOfStocks() {
        String clientStockJSONStr = "[{\"product\":\"AAPL\",\"quantity\":2},{\"product\":\"IBM\",\"quantity\":4},{\"product\":\"NFLX\",\"quantity\":6}]";
        when(responseEntity.getBody()).thenReturn(clientStockJSONStr);
        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
        when(restClientService.makeAPIRequest(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(responseEntity);
        List<StockDto> stocks = portfolioService.getStocksByClientId(4);

        Assertions.assertTrue(stocks.size() == 3);
    }
}
