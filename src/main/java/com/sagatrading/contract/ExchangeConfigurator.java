package com.sagatrading.contract;

import com.sagatrading.dto.ExchangeDto;

import java.util.List;

public interface ExchangeConfigurator {
    public List<ExchangeDto> getExchanges();

    public ExchangeDto getExchangeByName(String exchangeName);
}
