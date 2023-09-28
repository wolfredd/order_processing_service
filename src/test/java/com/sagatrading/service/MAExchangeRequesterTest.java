package com.sagatrading.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sagatrading.dto.ExchangeDto;
import com.sagatrading.model.ProductData;
import com.sagatrading.model.dto.OrderBookItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class MAExchangeRequesterTest {
    MAExchangeRequester maExchangeRequester;
    ExchangeDto exchange;

    //    @Mock
//    RestClientService restClientService = Mockito.mock(RestClientService.class);
    RestClientService restClientService;

    RestTemplate restTemplate = new RestTemplate();

    @Mock
    ResponseEntity responseEntity = Mockito.mock(ResponseEntity.class);

    @BeforeEach
    void setUp() {
        restClientService = new RestClientService(restTemplate);
        maExchangeRequester = new MAExchangeRequester(restClientService);
        exchange = new ExchangeDto(
                true,
                "https://exchange.matraining.com",
                "db6f5360-c667-451b-8e56-cb5f67c56115",
                "MAL1");
    }

    @Test
    void testGetOpenOrders() throws JsonProcessingException {
        List<OrderBookItem> openOrders = maExchangeRequester.getOpenOrders("AAPL", exchange);
        assertTrue(!openOrders.equals(null));
    }

    @Test
    void testGetClosedOrders() throws JsonProcessingException {
        List<OrderBookItem> closedOrders = maExchangeRequester.getClosedOrders("AAPL", exchange);
        System.out.println(closedOrders);
        assertTrue(!closedOrders.equals(null));
    }

    @Test
    void testGetCancelledOrders() throws JsonProcessingException {
        List<OrderBookItem> cancelledOrders = maExchangeRequester.getCancelledOrders("AAPL", exchange);
        System.out.println(cancelledOrders);
        assertTrue(!cancelledOrders.equals(null));
    }
    @Test
    void testGetProductData() {
        List<ProductData> productDataList = maExchangeRequester.getProductData(exchange);
        assertTrue(!(productDataList.equals(null)));
    }
}
