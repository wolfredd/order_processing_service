package com.sagatrading.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sagatrading.contract.AccountBalanceManager;
import com.sagatrading.contract.OrderValidator;
import com.sagatrading.contract.PortfolioManager;
import com.sagatrading.contract.ProductDataManager;
import com.sagatrading.exception.InsufficientBalanceException;
import com.sagatrading.exception.InsufficientStockException;
import com.sagatrading.model.Order;
import com.sagatrading.model.OrderSide;
import com.sagatrading.model.OrderType;
import com.sagatrading.model.dto.StockDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderValidationService implements OrderValidator {
    AccountBalanceManager accountBalanceService;

    ProductDataManager pricingService;

    @Autowired
    PortfolioManager portfolioService;

    public OrderValidationService(AccountBalanceManager accountBalanceService, ProductDataManager pricingService, PortfolioManager portfolioService) {
        this.accountBalanceService = accountBalanceService;
        this.pricingService = pricingService;
        this.portfolioService = portfolioService;
    }

    public void validateOrder(Order order) {
        if (order.getSide() == OrderSide.BUY) {
//            isBalanceSufficient(order);
        } else {
            try {
                doesUserHaveSufficientStock(order);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public boolean isBalanceSufficient(Order order) {
        if (order.getType().equals(OrderType.MARKET)) return true;
        // Get user account balance
        double accountBalance = accountBalanceService.getAccountBalance(order.getClientId());
        double stockPrice = pricingService.getHighestBidPrice(order);
        double requiredAmount = stockPrice * order.getQuantity();

        if (requiredAmount > accountBalance) {
            throw new InsufficientBalanceException("Insufficient balance in your account. You need " + requiredAmount + " to place this order!");
        }
        return true;
    }

    @Override
    public boolean doesUserHaveSufficientStock(Order order) throws JsonProcessingException {
        StockDto stock = portfolioService.getStockData(order.getClientId(), order.getProduct());
        if (stock.getQuantity() < order.getQuantity())
            throw new InsufficientStockException(
                    "Insufficient stocks. You have " + (stock.getQuantity() ) + " in total. You have to buy " + (order.getQuantity() - stock.getQuantity()) + " more"
            );
        return true;
    }
}
