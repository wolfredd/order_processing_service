package com.sagatrading.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sagatrading.model.dto.MarketDataUpdate;
import com.sagatrading.service.MarketDataProcessingService;
import org.springframework.beans.factory.annotation.Autowired;

public class Receiver {
    @Autowired
    MarketDataProcessingService marketDataProcessingService;

    public void receiveMessage(String message) {
        System.out.println("Message from market data: " + message);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            MarketDataUpdate marketDataUpdate = objectMapper.readValue(message, MarketDataUpdate.class);
            marketDataProcessingService.updateOrders(marketDataUpdate);
        } catch (JsonProcessingException ex) {
            // Todo: Log
        }
    }
}
