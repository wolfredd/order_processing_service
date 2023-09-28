package com.sagatrading.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sagatrading.contract.ExchangeConfigurator;
import com.sagatrading.contract.ExchangeRequester;
import com.sagatrading.contract.ProductDataManager;
import com.sagatrading.dto.ExchangeDto;
import com.sagatrading.model.Order;
import com.sagatrading.model.OrderSide;
import com.sagatrading.model.OrderType;
import com.sagatrading.model.dto.OrderBookItem;
import com.sagatrading.model.dto.OrderExchangeInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.mockito.Mockito.when;

public class OrderSplitterTest {
    OrderSplitter orderSplitter;

    ExchangeRequester exchangeRequesterService;

    ExchangeConfigurator exchangeConfigurator = Mockito.mock(ExchangeConfigurator.class);

    Order order;

    List<ExchangeDto> exchanges;

    RestClientService restClientService;

    ProductDataManager productDataManager;

    RestTemplate restTemplate = new RestTemplate();

    ExchangeDto exchange1 = new ExchangeDto(
            true,
            "https://exchange.matraining.com",
            "db6f5360-c667-451b-8e56-cb5f67c56115",
            "MAL1");
    ExchangeDto exchange2 = new ExchangeDto(
            true,
            "https://exchange2.matraining.com",
            "db6f5360-c667-451b-8e56-cb5f67c56115",
            "MAL2");

    @BeforeEach
    void setUp() {
        when(exchangeConfigurator.getExchanges()).thenReturn(List.of(exchange1, exchange2));
        restClientService = new RestClientService(restTemplate);
        exchangeRequesterService = new MAExchangeRequester(restClientService);
        productDataManager = new ProductDataService(exchangeRequesterService, exchangeConfigurator);

        exchanges = List.of(exchange1, exchange2);

        orderSplitter = new OrderSplitter(exchangeRequesterService, productDataManager);
        order = new Order();
        order.setProduct("GOOGL");
        order.setType(OrderType.LIMIT);
        order.setPrice(7);
        order.setSide(OrderSide.BUY);
        order.setQuantity(100);
        order.setClientId(4);
    }

    @Test
    void shouldSplitOrders() throws JsonProcessingException {
        List<OrderExchangeInfo> splitOrders = orderSplitter.splitOrder(order, exchanges);
    }

    @Test
    void testSplitLogic() throws JsonProcessingException {
        List<OrderBookItem> orders = exchangeRequesterService.getOpenOrders("TSLA", exchange1);
        List<OrderBookItem> sellorders = orders.stream().filter(orderItem -> orderItem.getSide().equals(OrderSide.BUY)).toList();
        System.out.println("Sell orders: " + sellorders);
        // The idea is to split an order across different exchange servers
        // In order to do that, each of the exchanges are queried for their open order and product data
        // The total quantity market order available on each exchange is put in a list, get quantity of good limit orders (price reasonable)
        // [1000, 0], [100, 4], [200,5], [400,6]] , remaining order quantity goes to the exchange with the lower price

    }
}
