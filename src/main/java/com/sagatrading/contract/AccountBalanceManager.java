package com.sagatrading.contract;

import java.util.UUID;

public interface AccountBalanceManager {
    public double getAccountBalance(Integer clientId);

    public double topUpAccount(int clientId, Double amount);

    public double deductFromAccount(int clientId, Double amount);
}
