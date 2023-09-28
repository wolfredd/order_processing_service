package com.sagatrading.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sagatrading.contract.*;
import com.sagatrading.model.Order;
import com.sagatrading.model.OrderSide;
import com.sagatrading.model.OrderStatus;
import com.sagatrading.model.dto.MarketDataUpdate;
import com.sagatrading.model.dto.OrderBookItem;
import com.sagatrading.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MarketDataProcessingService implements MarketDataProcessor {
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ExchangeRequester exchangeRequester;

    @Autowired
    ExchangeConfigurator exchangeConfigurator;

    @Autowired
    PortfolioManager portfolioManager;

    @Autowired
    AccountBalanceManager accountBalanceManager;

    public MarketDataProcessingService(OrderRepository orderRepository, ExchangeRequester exchangeRequester, ExchangeConfigurator exchangeConfigurator) {
        this.orderRepository = orderRepository;
        this.exchangeRequester = exchangeRequester;
        this.exchangeConfigurator = exchangeConfigurator;
    }

    public void updateOrders(MarketDataUpdate marketDataUpdate) {
        int qtyChange = 0;
        double priceChange = 0;
        System.out.println(marketDataUpdate);
        Optional<Order> orderOptional = orderRepository.findById(marketDataUpdate.getOrderID());
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            if (marketDataUpdate.getQty() == marketDataUpdate.getCumQty()) {
                System.out.println("-----------------------FILLED");
                order.setStatus(OrderStatus.FILLED);
                qtyChange = marketDataUpdate.getCumQty() - order.getCumulativeQuantity();
                priceChange = qtyChange * marketDataUpdate.getCumPrx();
                order.setCumulativeQuantity(marketDataUpdate.getCumQty());
            } else if (marketDataUpdate.getCumQty() > order.getCumulativeQuantity()) {
                qtyChange = marketDataUpdate.getCumQty() - order.getCumulativeQuantity();
                priceChange = qtyChange * marketDataUpdate.getCumPrx();
                order.setCumulativeQuantity(marketDataUpdate.getCumQty());
            } else {
                try {
                    List<OrderBookItem> cancelledOrders = exchangeRequester.getCancelledOrders(
                            order.getProduct(),
                            exchangeConfigurator.getExchangeByName(order.getExchange())
                    );

                    Optional<OrderBookItem> cancelledOrder = cancelledOrders.stream()
                            .filter(orderBookItem -> UUID.fromString(orderBookItem.getOrderID()) == order.getId())
                            .findFirst();
                    if (cancelledOrder.isPresent()) {
                        order.setStatus(OrderStatus.CANCELLED);
                    }

                } catch (JsonProcessingException ex) {
                    // Todo: Log exception
                    throw new RuntimeException("Failed to retrieve cancelled orders");
                }
            }
            System.out.println("==================" + order);
            Order savedOrder = orderRepository.save(order);
            System.out.println("Saved Order: " + savedOrder); // Log the saved order
            if (order.getSide().equals(OrderSide.BUY)) {
                // Increase stocks
                portfolioManager.updateStockData(order.getClientId(), order.getProduct(), qtyChange);
                accountBalanceManager.deductFromAccount(order.getClientId(),priceChange );

            } else {
                portfolioManager.updateStockData(order.getClientId(), order.getProduct(), -qtyChange);
                accountBalanceManager.topUpAccount(order.getClientId(),priceChange );
            }
        }
    }
}
