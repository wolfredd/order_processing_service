package com.sagatrading.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class OrderValidationServiceTest {
    @Mock
    AccountBalanceManager accountBalanceServiceMock = Mockito.mock(AccountBalanceManager.class);

    @Mock
    ProductDataManager pricingServiceMock = Mockito.mock(ProductDataManager.class);

    @Mock
    PortfolioManager portfolioServiceMock = Mockito.mock(PortfolioManager.class);

    Order order;
    OrderValidator orderValidationService;

    @BeforeEach
    void setUp() {
        orderValidationService = new OrderValidationService(accountBalanceServiceMock, pricingServiceMock, portfolioServiceMock);
        order = new Order();
        order.setProduct("AAPL");
        order.setType(OrderType.MARKET);
        order.setSide(OrderSide.BUY);
        order.setQuantity(100);
        order.setClientId(4);
    }

    @Test
    @DisplayName("Should throw an exception for insufficient account balance")
    void testThrowInsufficientAccountBalanceException() {
        when(pricingServiceMock.getHighestBidPrice(order)).thenReturn(3.0);
        when(accountBalanceServiceMock.getAccountBalance(4)).thenReturn(20.0);
        assertThrows(InsufficientBalanceException.class, () -> orderValidationService.validateOrder(order));
    }

    @Test
    @DisplayName("Should not throw an error for sufficient balance")
    void testSufficientAccountBalance() {
        when(pricingServiceMock.getHighestBidPrice(order)).thenReturn(2.0);
        when(accountBalanceServiceMock.getAccountBalance(4)).thenReturn(400.0);
        assertDoesNotThrow(() -> orderValidationService.validateOrder(order));
    }

    @Test
    @DisplayName("Should throw an error for a sell order with insufficient stocks in portfolio")
    void testThrowInsufficientStockExceptionForSellOrder() {
        order.setSide(OrderSide.SELL);
        StockDto testStock = new StockDto();
        testStock.setProduct(order.getProduct());
        testStock.setQuantity(3);
        when(portfolioServiceMock.getStockData(4, order.getProduct())).thenReturn(testStock);
        assertThrows(InsufficientStockException.class, () -> orderValidationService.validateOrder(order));
    }
}
