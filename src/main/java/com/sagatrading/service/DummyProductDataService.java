package com.sagatrading.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sagatrading.contract.ExchangeConfigurator;
import com.sagatrading.contract.ExchangeRequester;
import com.sagatrading.contract.ProductDataManager;
import com.sagatrading.dto.ExchangeDto;
import com.sagatrading.model.ProductData;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class DummyProductDataService extends ProductDataService{
    String exchange1PD = "[{\"BID_PRICE\":45.0,\"SELL_LIMIT\":5000,\"ASK_PRICE\":48.0,\"BUY_LIMIT\":10000,\"MAX_PRICE_SHIFT\":4.5,\"TICKER\":\"MSFT\",\"LAST_TRADED_PRICE\":45.0},{\"BID_PRICE\":0.8,\"SELL_LIMIT\":5000,\"ASK_PRICE\":0.01,\"BUY_LIMIT\":10000,\"MAX_PRICE_SHIFT\":1.00,\"TICKER\":\"NFLX\",\"LAST_TRADED_PRICE\":0.01},{\"BID_PRICE\":1.2,\"SELL_LIMIT\":5000,\"ASK_PRICE\":1.2,\"BUY_LIMIT\":10000,\"MAX_PRICE_SHIFT\":1.00,\"TICKER\":\"GOOGL\",\"LAST_TRADED_PRICE\":2.5},{\"BID_PRICE\":1.01,\"SELL_LIMIT\":5000,\"ASK_PRICE\":1.01,\"BUY_LIMIT\":10000,\"MAX_PRICE_SHIFT\":1.00,\"TICKER\":\"AAPL\",\"LAST_TRADED_PRICE\":1.01},{\"BID_PRICE\":10.0,\"SELL_LIMIT\":5000,\"ASK_PRICE\":11,\"BUY_LIMIT\":10000,\"MAX_PRICE_SHIFT\":1,\"TICKER\":\"TSLA\",\"LAST_TRADED_PRICE\":10.0},{\"BID_PRICE\":4,\"SELL_LIMIT\":5000,\"ASK_PRICE\":4.0,\"BUY_LIMIT\":10000,\"MAX_PRICE_SHIFT\":1.00,\"TICKER\":\"IBM\",\"LAST_TRADED_PRICE\":4.0},{\"BID_PRICE\":6.6,\"SELL_LIMIT\":5000,\"ASK_PRICE\":0.00,\"BUY_LIMIT\":10000,\"MAX_PRICE_SHIFT\":1.00,\"TICKER\":\"ORCL\",\"LAST_TRADED_PRICE\":6.6},{\"BID_PRICE\":165.0,\"SELL_LIMIT\":5000,\"ASK_PRICE\":180,\"BUY_LIMIT\":10000,\"MAX_PRICE_SHIFT\":16.5,\"TICKER\":\"AMZN\",\"LAST_TRADED_PRICE\":165.0}]";

    String exchange2PD = "[{\"TICKER\":\"MSFT\",\"ASK_PRICE\":1.58,\"BID_PRICE\":1.58,\"BUY_LIMIT\":10000,\"MAX_PRICE_SHIFT\":1.00,\"SELL_LIMIT\":5000,\"LAST_TRADED_PRICE\":1.58},{\"TICKER\":\"NFLX\",\"ASK_PRICE\":0.00,\"BID_PRICE\":0.00,\"BUY_LIMIT\":10000,\"MAX_PRICE_SHIFT\":20,\"SELL_LIMIT\":5000,\"LAST_TRADED_PRICE\":0.00},{\"TICKER\":\"GOOGL\",\"ASK_PRICE\":5.2,\"BID_PRICE\":5.2,\"BUY_LIMIT\":10000,\"MAX_PRICE_SHIFT\":1.00,\"SELL_LIMIT\":5000,\"LAST_TRADED_PRICE\":5.2},{\"TICKER\":\"AAPL\",\"ASK_PRICE\":1.0,\"BID_PRICE\":1.0,\"BUY_LIMIT\":10000,\"MAX_PRICE_SHIFT\":1.00,\"SELL_LIMIT\":5000,\"LAST_TRADED_PRICE\":1.0},{\"TICKER\":\"TSLA\",\"ASK_PRICE\":0.00,\"BID_PRICE\":0.00,\"BUY_LIMIT\":10000,\"MAX_PRICE_SHIFT\":3,\"SELL_LIMIT\":5000,\"LAST_TRADED_PRICE\":0.00},{\"TICKER\":\"IBM\",\"ASK_PRICE\":0.36,\"BID_PRICE\":0.36,\"BUY_LIMIT\":10000,\"MAX_PRICE_SHIFT\":1.00,\"SELL_LIMIT\":5000,\"LAST_TRADED_PRICE\":0.36},{\"TICKER\":\"ORCL\",\"ASK_PRICE\":1.2,\"BID_PRICE\":1.2,\"BUY_LIMIT\":10000,\"MAX_PRICE_SHIFT\":1.00,\"SELL_LIMIT\":5000,\"LAST_TRADED_PRICE\":1.2},{\"TICKER\":\"AMZN\",\"ASK_PRICE\":0.00,\"BID_PRICE\":0.00,\"BUY_LIMIT\":10000,\"MAX_PRICE_SHIFT\":200,\"SELL_LIMIT\":5000,\"LAST_TRADED_PRICE\":0.00}]";
    ExchangeRequester exchangeRequesterService;

    ExchangeConfigurator exchangeConfigService;

    Map<String, List<ProductDataManager>> exchangeProductData;

    public DummyProductDataService(ExchangeRequester exchangeRequesterService, ExchangeConfigurator exchangeConfigService) throws JsonProcessingException {
        super(exchangeRequesterService, exchangeConfigService);
        ObjectMapper objectMapper = new ObjectMapper();
        List<ProductDataManager> exchange1PDObj = objectMapper.readValue(exchange1PD, new TypeReference<>() {
        });
        List<ProductDataManager> exchange2PDObj = objectMapper.readValue(exchange2PD, new TypeReference<>() {
        });
        exchangeProductData = Map.of("MAL1", exchange1PDObj, "MAL2", exchange2PDObj);
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
}
