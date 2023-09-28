package com.sagatrading.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sagatrading.contract.ExchangeRequester;
import com.sagatrading.dto.ExchangeDto;
import com.sagatrading.model.Order;
import com.sagatrading.model.ProductData;
import com.sagatrading.model.dto.OrderBookItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class MAExchangeRequester implements ExchangeRequester {
    @Autowired
    private RestClientService restClientService;

    @Value("${exchange.apiKey}")
    private String apiKey;

    ObjectMapper objectMapper = new ObjectMapper();

    public MAExchangeRequester(RestClientService restClientService) {
        this.restClientService = restClientService;
    }

    @Override
    public String submitOrder(Order order, ExchangeDto exchange) {
        String url = exchange.getExchangeUrl() + "/" + exchange.getApiKey() + "/order";
        ResponseEntity<String> response = restClientService.makeAPIRequest(url, HttpMethod.POST, order);
        return response.getBody();
    }

    @Override
    public String updateOrder(Order order, UUID orderId, ExchangeDto exchange) {
        String url = exchange.getExchangeUrl() + "/" + exchange.getApiKey() + "/order/" + orderId;
        ResponseEntity<String> response = restClientService.makeAPIRequest(url, HttpMethod.POST, order);
        return response.getBody();
    }

    @Override
    public Boolean cancelOrder(UUID orderID, ExchangeDto exchange) {
        String url = exchange.getExchangeUrl() + "/" + exchange.getApiKey() + "/order/"+ orderID.toString();
        ResponseEntity response = restClientService.makeAPIRequest(url, HttpMethod.DELETE, "");
        System.out.println("Response from the exchange" + response.getBody());
        if (!Objects.equals(response.getBody(), "true")) throw new RuntimeException("Could not cancel your order: " + response.getBody()); // Todo: Use appropriate exception
            return true;
    }

    @Override
    public List<OrderBookItem> getOpenOrders(String product, ExchangeDto exchange) throws JsonProcessingException {
        String url = exchange.getExchangeUrl() + "/orderbook/" + product + "/open";
        ResponseEntity response = restClientService.makeAPIRequest(url, HttpMethod.GET, "");
        return objectMapper.readValue(Objects.requireNonNull(response.getBody()).toString(), new TypeReference<>(){});
    }

    @Override
    public List<OrderBookItem> getClosedOrders(String product, ExchangeDto exchange) throws JsonProcessingException {
        String url = exchange.getExchangeUrl() + "/orderbook/" + product + "/closed";
        ResponseEntity response = restClientService.makeAPIRequest(url, HttpMethod.GET, "");
        return objectMapper.readValue(response.getBody().toString(), new TypeReference<>(){});
    }

    @Override
    public List<OrderBookItem> getCancelledOrders(String product, ExchangeDto exchange) throws JsonProcessingException {
        String url = exchange.getExchangeUrl() + "/orderbook/" + product + "/cancelled";
        ResponseEntity response = restClientService.makeAPIRequest(url, HttpMethod.GET, "");
        return objectMapper.readValue(response.getBody().toString(), new TypeReference<>(){});
    }

    @Override
    public List<OrderBookItem> getProductOrderBook(String product, ExchangeDto exchange) throws JsonProcessingException {
        String url = exchange.getExchangeUrl() + "/orderbook/" + product;
        ResponseEntity response = restClientService.makeAPIRequest(url, HttpMethod.GET, "");
        return objectMapper.readValue(response.getBody().toString(), new TypeReference<>(){});
    }

    @Override
    public List<ProductData> getProductData(ExchangeDto exchange)  {
        String url = exchange.getExchangeUrl() + "/pd";
        ResponseEntity<String> response = restClientService.makeAPIRequest(url, HttpMethod.GET, "");
        String productDetailsStr = response.getBody();
        try {
            return objectMapper.readValue(productDetailsStr, new TypeReference<>() {
            });
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}
