package com.sagatrading.contract;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sagatrading.dto.ExchangeDto;
import com.sagatrading.dto.ProductDetails;
import com.sagatrading.model.Order;
import com.sagatrading.model.ProductData;
import com.sagatrading.model.dto.OrderBookItem;

import java.util.List;
import java.util.UUID;

public interface ExchangeRequester {
    public String submitOrder(Order order, ExchangeDto exchange);

    public String updateOrder(Order order, UUID orderId, ExchangeDto exchange);

    public Boolean cancelOrder(UUID orderID, ExchangeDto exchange);

    public List<OrderBookItem> getOpenOrders(String product, ExchangeDto exchange) throws JsonProcessingException;

    public List<OrderBookItem> getClosedOrders(String product, ExchangeDto exchange) throws JsonProcessingException;

    public List<OrderBookItem> getCancelledOrders(String product, ExchangeDto exchange) throws JsonProcessingException;

    public List<OrderBookItem> getProductOrderBook(String product, ExchangeDto exchange) throws JsonProcessingException;

    public List<ProductData> getProductData(ExchangeDto exchange);
}
