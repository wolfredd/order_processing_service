package com.sagatrading.dto;

import java.util.Objects;

public class ExchangeDto {
    private Boolean isActive;
    private String exchangeUrl;
    private String apiKey;
    private String exchangeName;

    public ExchangeDto(Boolean isActive, String exchangeUrl, String apiKey, String exchangeName) {
        this.isActive = isActive;
        this.exchangeUrl = exchangeUrl;
        this.apiKey = apiKey;
        this.exchangeName = exchangeName;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getExchangeUrl() {
        return exchangeUrl;
    }

    public void setExchangeUrl(String exchangeUrl) {
        this.exchangeUrl = exchangeUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    @Override
    public String toString() {
        return exchangeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeDto that = (ExchangeDto) o;
        return exchangeUrl.equals(that.exchangeUrl) && exchangeName.equals(that.exchangeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exchangeUrl, exchangeName);
    }
}
