package com.sagatrading.contract;

import com.sagatrading.dto.ExchangeDto;
import com.sagatrading.exception.InvalidOrderPriceException;
import com.sagatrading.exception.OrderQuantityExceedsLimitException;
import com.sagatrading.model.Order;
import com.sagatrading.model.ProductData;

import java.util.List;
import java.util.Map;

public interface ProductDataManager {
    public Map<ExchangeDto, ProductData> getExchangeProductDataByTicker(String ticker);

    public Map<ExchangeDto, List<ProductData>> getExchangeProductData();

    public List<ExchangeDto> getExchangesAvailableForOrder(Order order) throws InvalidOrderPriceException, OrderQuantityExceedsLimitException;

    public double getHighestBidPrice(Order order);
}
