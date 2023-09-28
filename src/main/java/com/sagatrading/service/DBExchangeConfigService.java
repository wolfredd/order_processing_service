package com.sagatrading.service;

import com.sagatrading.contract.ExchangeConfigurator;
import com.sagatrading.dto.ExchangeDto;

import java.util.List;

public class DBExchangeConfigService implements ExchangeConfigurator {
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

    @Override
    public List<ExchangeDto> getExchanges() {
        return List.of(exchange1, exchange2);
    }

    public ExchangeDto getExchangeByName(String exchangeName) {
        return exchangeName.equals("MAL1") ? exchange1 : exchange2;
    }

}
