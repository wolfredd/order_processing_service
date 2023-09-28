package com.sagatrading.service;

import com.sagatrading.contract.AccountBalanceManager;
import com.sagatrading.model.AccountBalance;
import com.sagatrading.repository.AccountBalanceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountingService implements AccountBalanceManager {
    AccountBalanceRepository accountBalanceRepository;

    public AccountingService(AccountBalanceRepository accountBalanceRepository) {
        this.accountBalanceRepository = accountBalanceRepository;
    }

    @Override
    public double getAccountBalance(Integer clientId) {
        List<AccountBalance> accountBalance = accountBalanceRepository.findByClientId(clientId);
        if (accountBalance.isEmpty()) {
            AccountBalance newClientAccount = new AccountBalance();
            newClientAccount.setClientId(clientId);
            newClientAccount.setAmount(0.0);
            accountBalanceRepository.save(newClientAccount);
            return 0.0;
        } else {
            AccountBalance clientAccount = accountBalance.get(0);
            return clientAccount.getAmount();
        }
    }

    @Override
    public double topUpAccount(int clientId, Double amount) {
        return updateAccountBalance(clientId, amount, true);
    }

    @Override
    public double deductFromAccount(int clientId, Double amount) {
        return updateAccountBalance(clientId, amount, false);
    }

    public double updateAccountBalance(int clientId, Double amount, Boolean isTopUp) {
        List<AccountBalance> accountBalance = accountBalanceRepository.findByClientId(clientId);
        Double currentAmount = 0.0;
        AccountBalance clientAccount;

        if (accountBalance.isEmpty()) {
            // Create new account for client
            AccountBalance newClientAccount = new AccountBalance();
            newClientAccount.setClientId(clientId);
            newClientAccount.setAmount(0.0);
            accountBalanceRepository.save(newClientAccount);
            clientAccount = newClientAccount;
        } else {
            clientAccount = accountBalance.get(0);
            currentAmount = clientAccount.getAmount();
        }

        Double updatedAmount = isTopUp ? currentAmount + amount : currentAmount - amount;
        clientAccount.setAmount(updatedAmount);
        accountBalanceRepository.save(clientAccount);
        return updatedAmount;
    }

}
