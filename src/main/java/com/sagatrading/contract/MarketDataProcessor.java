package com.sagatrading.contract;

import com.sagatrading.model.dto.MarketDataUpdate;

public interface MarketDataProcessor {
    public void updateOrders(MarketDataUpdate marketDataUpdate);
}
