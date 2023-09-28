package com.sagatrading.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sagatrading.contract.ExchangeRequester;
import com.sagatrading.contract.ProductDataManager;
import com.sagatrading.dto.ExchangeDto;
import com.sagatrading.model.Order;
import com.sagatrading.model.OrderSide;
import com.sagatrading.model.OrderType;
import com.sagatrading.model.ProductData;
import com.sagatrading.model.dto.OrderBookItem;
import com.sagatrading.model.dto.OrderExchangeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Service
public class OrderSplitter {
    ExchangeRequester exchangeRequester;

    ProductDataManager productDataManager;

    public OrderSplitter(ExchangeRequester exchangeRequester, ProductDataManager productDataManager) {
        this.exchangeRequester = exchangeRequester;
        this.productDataManager = productDataManager;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    private static class DealAvailability {
        private Integer Quantity;
        private Double Price;

        @Override
        public String toString() {
            return "DealAvailability{" +
                    "Quantity=" + Quantity +
                    ", Price=" + Price +
                    '}';
        }
    }

    @AllArgsConstructor
    @Getter
    @Setter
    private static class ExchangeDeal {
        private ExchangeDto exchange;
        private DealAvailability dealAvailability;

        @Override
        public String toString() {
            return "ExchangeDeal{" +
                    "exchange=" + exchange +
                    ", dealAvailability=" + dealAvailability +
                    '}';
        }
    }

    public List<OrderExchangeInfo> splitOrder(Order order, List<ExchangeDto> exchanges) throws JsonProcessingException {
        List<OrderExchangeInfo> splitOrders = new ArrayList<>();
        Map<ExchangeDto, ProductData> exchangeProductDataByTicker = productDataManager.getExchangeProductDataByTicker(order.getProduct());
        List<ExchangeDeal> bestDealsExchanges = getExchangeBestDeals(order, exchanges, exchangeProductDataByTicker);

        ExchangeDto bestPriceExchange = getExchangeWithBestPrice(order.getSide(), exchangeProductDataByTicker);
        Map<ExchangeDto, DealAvailability> sortedExchangeDeals = sortExchangeDeals(bestDealsExchanges, order, bestPriceExchange);

        Integer orderQuantityRemaining = order.getQuantity();

        for (Map.Entry<ExchangeDto, DealAvailability> exchangeDeal : sortedExchangeDeals.entrySet()) {
            if (orderQuantityRemaining < 1) break;
            int quantity = exchangeDeal.getValue().getQuantity();
            int newOrderQty = quantity > orderQuantityRemaining ? orderQuantityRemaining : quantity;
            orderQuantityRemaining = orderQuantityRemaining - newOrderQty;
            splitOrders.add(createOrderWithExchangeInfo(order, newOrderQty, exchangeDeal.getKey()));
        }

        return splitOrders;
    }

    private OrderExchangeInfo createOrderWithExchangeInfo(Order order, int quantity, ExchangeDto exchange) {
        Order newOrder = new Order();
        newOrder.setQuantity(quantity);
        newOrder.setSide(order.getSide());
        newOrder.setPrice(order.getPrice());
        newOrder.setType(order.getType());
        newOrder.setClientId(order.getClientId());
        newOrder.setPrice(order.getPrice());
        newOrder.setProduct(order.getProduct());
        newOrder.setId(order.getId());
        return new OrderExchangeInfo(exchange, newOrder);
    }

    private Map<ExchangeDto, DealAvailability> sortExchangeDeals(List<ExchangeDeal> exchangeDeals, Order order, ExchangeDto bestExchange) {
        Map<ExchangeDto, DealAvailability> resultMap = new HashMap<>();
        int totalDealQuantity = exchangeDeals.stream().mapToInt(exchangeDeal -> exchangeDeal.dealAvailability.getQuantity()).sum();
        System.out.println(totalDealQuantity);

        for (ExchangeDeal exchangeDeal : exchangeDeals) {
            if (resultMap.containsKey(exchangeDeal.getExchange())) {
                DealAvailability existingDeal = resultMap.get(exchangeDeal.getExchange());
                int existingQuantity = existingDeal.getQuantity();
                double existingPrice = existingDeal.getPrice();

                int curQuantity = exchangeDeal.getDealAvailability().getQuantity();
                double curPrice = exchangeDeal.getDealAvailability().getPrice();
                int newQuantity = existingQuantity + curQuantity;
                double newPrice = (existingQuantity * existingPrice + (curQuantity * curPrice)) / newQuantity;

                existingDeal.setQuantity(newQuantity);
                existingDeal.setPrice(newPrice);
            } else {
                resultMap.put(exchangeDeal.getExchange(), exchangeDeal.getDealAvailability());
            }
        }
        if (totalDealQuantity < order.getQuantity()) {
            int remainingQuantity = order.getQuantity() - totalDealQuantity;
            if (resultMap.containsKey(bestExchange)) {
                DealAvailability existingDeal = resultMap.get(bestExchange);
                existingDeal.setQuantity(existingDeal.getQuantity() + remainingQuantity);
            } else {
                resultMap.put(bestExchange, new DealAvailability(remainingQuantity, 0.0));
            }
        }
        return resultMap;
    }

    private Integer getOpenMarketOrdersQuantityAvailable(List<OrderBookItem> orders, OrderSide side) {
        return orders.stream()
                .filter(orderItem -> orderItem.getSide().equals(side.equals(OrderSide.BUY) ? OrderSide.SELL : OrderSide.BUY)
                        && orderItem.getOrderType().equals(OrderType.MARKET))
                .mapToInt(orderItem -> orderItem.getQuantity() - orderItem.getCumulatitiveQuantity())
                .sum();
    }

    private Integer getOpenLimitOrdersQuantityAvailable(List<OrderBookItem> orders, OrderSide side, Double price) {
        return orders.stream()
                .filter(orderItem -> orderItem.getSide().equals(side.equals(OrderSide.BUY) ? OrderSide.SELL : OrderSide.BUY)
                        && (side.equals(OrderSide.BUY) ? orderItem.getPrice() <= price : orderItem.getPrice() >= price)
                        && orderItem.getOrderType().equals(OrderType.LIMIT)
                )
                .filter(orderItem -> orderItem.getPrice() < price)
                .mapToInt(orderItem -> orderItem.getQuantity() - orderItem.getCumulatitiveQuantity())
                .sum();
    }

    private ExchangeDeal createExchangeDeal(ExchangeDto exchange, Integer quantity, Double price) {
        return new ExchangeDeal(exchange, new DealAvailability(quantity, price));
    }

    private List<ExchangeDeal> getExchangeBestDeals(Order order, List<ExchangeDto> exchanges, Map<ExchangeDto, ProductData> exchangeDtoProductDataMap) throws JsonProcessingException {
        List<ExchangeDeal> bestDealsExchanges = new ArrayList<>();
        for (ExchangeDto exchange : exchanges) {
            ProductData exchangeProductData = exchangeDtoProductDataMap.get(exchange);
            List<OrderBookItem> openOrders = exchangeRequester.getOpenOrders(order.getProduct(), exchange);

            Integer marketOrdersQuantityAvailable = getOpenMarketOrdersQuantityAvailable(openOrders, order.getSide());
            Double marketPrice = order.getSide().equals(OrderSide.BUY) ? exchangeProductData.getBidPrice() : exchangeProductData.getAskPrice();
            Double limitPrice = order.getType().equals(OrderType.LIMIT) ? order.getPrice() : marketPrice;

            if (marketOrdersQuantityAvailable > 0) {
                bestDealsExchanges.add(createExchangeDeal(exchange, marketOrdersQuantityAvailable, marketPrice));
            }

            Integer limitOrdersQuantityAvailable = getOpenLimitOrdersQuantityAvailable(
                    openOrders, order.getSide(),
                    limitPrice
            );

            if (limitOrdersQuantityAvailable > 0) {
                bestDealsExchanges.add(createExchangeDeal(exchange, limitOrdersQuantityAvailable, limitPrice));
            }
        }
        return bestDealsExchanges;
    }

    private ExchangeDto getExchangeWithBestPrice(OrderSide orderSide, Map<ExchangeDto, ProductData> exchangeDtoProductDataMap) {
        Stream<Map.Entry<ExchangeDto, ProductData>> exchangeProductStream = exchangeDtoProductDataMap.entrySet().stream();

        Optional<Map.Entry<ExchangeDto, ProductData>> bestExchangeMap = orderSide.equals(OrderSide.BUY) ? exchangeProductStream.min(Comparator.comparingDouble(entry -> entry.getValue().getBidPrice())) :
                exchangeProductStream.max(Comparator.comparingDouble(entry -> entry.getValue().getAskPrice()));

        return bestExchangeMap.map(Map.Entry::getKey)
                .orElse(null);
    }
}
