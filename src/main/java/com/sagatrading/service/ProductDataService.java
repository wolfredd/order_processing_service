package com.sagatrading.service;

import com.sagatrading.contract.ExchangeConfigurator;
import com.sagatrading.contract.ExchangeRequester;
import com.sagatrading.contract.ProductDataManager;
import com.sagatrading.dto.ExchangeDto;
import com.sagatrading.exception.InvalidOrderPriceException;
import com.sagatrading.exception.OrderQuantityExceedsLimitException;
import com.sagatrading.model.Order;
import com.sagatrading.model.OrderSide;
import com.sagatrading.model.OrderType;
import com.sagatrading.model.ProductData;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductDataService implements ProductDataManager {
    ExchangeRequester exchangeRequesterService;

    ExchangeConfigurator exchangeConfigService;

    public ProductDataService(ExchangeRequester exchangeRequesterService, ExchangeConfigurator exchangeConfigService) {
        this.exchangeRequesterService = exchangeRequesterService;
        this.exchangeConfigService = exchangeConfigService;
    }

    @Override
    public Map<ExchangeDto, List<ProductData>> getExchangeProductData() {
        Map<ExchangeDto, List<ProductData>> exchangeProductData = new HashMap<>();
        List<ExchangeDto> exchanges = exchangeConfigService.getExchanges();
        for(ExchangeDto exchange : exchanges) {
            List<ProductData> productDataList = exchangeRequesterService.getProductData(exchange);
            exchangeProductData.put(exchange, productDataList );
        }
        return exchangeProductData;
    }

    @Override
    public Map<ExchangeDto, ProductData> getExchangeProductDataByTicker(String ticker) {
        Map<ExchangeDto, ProductData> foundExchangeTicker = new HashMap<>();
        for (Map.Entry<ExchangeDto, List<ProductData>> entry : getExchangeProductData().entrySet()) {
            List<ProductData> foundProduct = entry.getValue().stream().filter(productData -> productData.getTicker().equals(ticker)).toList();
            if (foundProduct.size() > 0) {
                foundExchangeTicker.put(entry.getKey(), foundProduct.get(0));
            }
        }
        return foundExchangeTicker;
    }

    @Override
    public List<ExchangeDto> getExchangesAvailableForOrder(Order order) throws InvalidOrderPriceException, OrderQuantityExceedsLimitException {
        Map<ExchangeDto, ProductData> exchangeProductData = getExchangeProductDataByTicker(order.getProduct());

        validateOrderQuantity(exchangeProductData, order);

        if (order.getType() == OrderType.MARKET) {
            return exchangeProductData.keySet().stream().toList();
        }
        List<ExchangeDto> exchanges = new ArrayList<>();
        List<Map<String, Double>> priceRanges = new ArrayList<>();
        for (Map.Entry<ExchangeDto, ProductData> entry : exchangeProductData.entrySet()) {
            ExchangeDto exchange = entry.getKey();
            if (priceIsValid(entry.getValue(), order.getPrice(), order.getSide())) {
                exchanges.add(exchange);
            }
            priceRanges.add(getPriceRange(entry.getValue(), order.getSide()));
        }
        if (exchanges.size() > 0) return exchanges;

        throw new InvalidOrderPriceException("Your price " + order.getPrice() + " is unreasonable. It should be in the range: " + priceRanges);
    }

    public double getHighestBidPrice(Order order) {
        double highestBidPrice = 0;
        Map<ExchangeDto, ProductData> exchangePrices = getExchangeProductDataByTicker(order.getProduct());

        if (order.getType() == OrderType.LIMIT) {
            return order.getPrice();
        }
        for (Map.Entry<ExchangeDto, ProductData> entry : exchangePrices.entrySet()) {
            double bidPrice = entry.getValue().getBidPrice();
            highestBidPrice = Math.max(bidPrice, highestBidPrice);
        }
        return highestBidPrice;
    }

    private boolean priceIsValid(com.sagatrading.model.ProductData productData, Double price, OrderSide side) {
        Map<String, Double> priceRange = getPriceRange(productData, side);
        return priceRange.get("max") >= price && priceRange.get("min") <= price;
    }

    private Map<String, Double> getPriceRange(com.sagatrading.model.ProductData productData, OrderSide side) {
        Map<String, Double> priceRange = new HashMap<>();
        Double maxShiftPrice = productData.getMaxPriceShift();
        Double bidPrice = productData.getBidPrice();
        Double askPrice = productData.getAskPrice();
        Double minBidPrice = bidPrice - maxShiftPrice;
        if (side == OrderSide.BUY) {
            priceRange.put("min", minBidPrice < 0 ? 0.00000000000000000000000000000000001 : minBidPrice); // You got yourself a deal! :)
            priceRange.put("max", bidPrice + maxShiftPrice);
        } else {
            priceRange.put("min", askPrice - maxShiftPrice);
            priceRange.put("max", askPrice + maxShiftPrice);
        }
        return priceRange;
    }

    private void validateOrderQuantity(Map<ExchangeDto, ProductData> exchanges, Order order) throws OrderQuantityExceedsLimitException {
        for (Map.Entry<ExchangeDto, ProductData> entry : exchanges.entrySet()) {
            ProductData productData = entry.getValue();
            Integer limit = order.getSide() == OrderSide.BUY ? productData.getBuyLimit() : productData.getSellLimit();
            if (order.getQuantity() > limit)
                throw new OrderQuantityExceedsLimitException(order.getSide() + " order quantity must not exceed " + limit);
        }
    }

}
