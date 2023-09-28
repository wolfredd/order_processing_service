package com.sagatrading.contract;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sagatrading.model.Order;

public interface OrderValidator {
    public void validateOrder(Order order);
    public boolean isBalanceSufficient(Order order);
    public boolean doesUserHaveSufficientStock(Order order) throws JsonProcessingException;
}
