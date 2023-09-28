package com.sagatrading.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sagatrading.contract.ExchangeConfigurator;
import com.sagatrading.contract.ExchangeRequester;
import com.sagatrading.model.Order;
import com.sagatrading.model.OrderStatus;
import com.sagatrading.model.dto.MarketDataUpdate;
import com.sagatrading.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.postgresql.hostchooser.HostRequirement.any;

class MarketDataProcessorServiceTest {
    MarketDataProcessingService marketDataProcessingService;

    OrderRepository orderRepositoryMock = Mockito.mock(OrderRepository.class);

    MarketDataUpdate marketDataUpdate;
    MarketDataUpdate marketDataUpdate2;

    ExchangeConfigurator exchangeConfigurator = Mockito.mock(ExchangeConfigurator.class);

    ExchangeRequester exchangeRequesterMock = Mockito.mock(ExchangeRequester.class);

    ExchangeRequester exchangeRequester;
    RestClientService restClientService;
    RestTemplate restTemplate;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        String sampleMarketDataUpdate = "{\"orderType\":\"LIMIT\",\"product\":\"GOOGL\",\"side\":\"SELL\",\"orderID\":\"dd705ee1-9614-4951-96cc-a2601bf2ad22\",\"price\":1.2,\"qty\":2000,\"cumQty\":2000,\"cumPrx\":\"1.20\",\"exchange\":\"MAL1\",\"timestamp\":\"2023-06-08T04:10:17.425\"}";
        String sampleMarketDataUpdate2 = "{\"orderType\":\"LIMIT\",\"product\":\"GOOGL\",\"side\":\"SELL\",\"orderID\":\"aa705ee1-9614-4951-96cc-a2601bf2ad22\",\"price\":1.2,\"qty\":2000,\"cumQty\":1800,\"cumPrx\":\"1.20\",\"exchange\":\"MAL1\",\"timestamp\":\"2023-06-08T04:10:17.425\"}";
        ObjectMapper objectMapper = new ObjectMapper();
        marketDataUpdate = objectMapper.readValue(sampleMarketDataUpdate, MarketDataUpdate.class );
        marketDataUpdate2 = objectMapper.readValue(sampleMarketDataUpdate2, MarketDataUpdate.class );

        restClientService = new RestClientService(restTemplate);
        exchangeRequester = new MAExchangeRequester(restClientService);

        marketDataProcessingService = new MarketDataProcessingService(orderRepositoryMock, exchangeRequester, exchangeConfigurator );
    }

    @Test
    @DisplayName("Test order is updated for market data matching an existing order")
    void testOrderIsUpdatedWithMarketDataMatchingOrder() {
        UUID sampleOrderId = UUID.fromString("dd705ee1-9614-4951-96cc-a2601bf2ad22");
        Order testOrder = new Order();
        testOrder.setId(sampleOrderId);
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.setClientId(5);
        testOrder.setExchange("MAL1");
        testOrder.setProduct("GOOGL");
        Optional<Order> optionalTestOrder = Optional.of(testOrder);

        Order expectedUpdatedOrder = new Order();
        expectedUpdatedOrder.setStatus(OrderStatus.FILLED);
        expectedUpdatedOrder.setId(sampleOrderId);
        expectedUpdatedOrder.setClientId(5);
        expectedUpdatedOrder.setExchange("MAL1");
        expectedUpdatedOrder.setProduct("GOOGL");

        when(orderRepositoryMock.findById(sampleOrderId)).thenReturn(optionalTestOrder);
        when(orderRepositoryMock.save(any())).then(AdditionalAnswers.returnsFirstArg());

        marketDataProcessingService.updateOrders(marketDataUpdate);

        verify(orderRepositoryMock, times(1)).findById(sampleOrderId);
        verify(orderRepositoryMock, times(1)).save(expectedUpdatedOrder);
    }

    @Test
    @DisplayName("Test order is not updated for market data not matching any of the existing orders")
    void testOrderIsNotUpdatedWithMarketDataNotMatchingOrder() {
        UUID sampleOrderId = UUID.fromString("aa705ee1-9614-4951-96cc-a2601bf2ad22");

        Order expectedUpdatedOrder = new Order();
        expectedUpdatedOrder.setStatus(OrderStatus.FILLED);
        expectedUpdatedOrder.setId(sampleOrderId);
        expectedUpdatedOrder.setClientId(5);
        expectedUpdatedOrder.setExchange("MAL1");
        expectedUpdatedOrder.setProduct("GOOGL");

        when(orderRepositoryMock.findById(sampleOrderId)).thenReturn(Optional.empty());
        when(orderRepositoryMock.save(any())).then(AdditionalAnswers.returnsFirstArg());

        marketDataProcessingService.updateOrders(marketDataUpdate2);

        verify(orderRepositoryMock, times(1)).findById(sampleOrderId);
        verify(orderRepositoryMock, times(0)).save(expectedUpdatedOrder);
    }
}