package com.sagatrading.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sagatrading.contract.*;
import com.sagatrading.dto.ExchangeDto;
import com.sagatrading.model.Order;
import com.sagatrading.model.ProductData;
import com.sagatrading.model.dto.OrderBookItem;
import com.sagatrading.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Configuration
public class AppConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ExchangeConfigurator exchangeConfig() {
        return new DBExchangeConfigService();
    }

    @Bean
    public PortfolioManager portfolioService() {
        return new PortfolioService();
    }
}
