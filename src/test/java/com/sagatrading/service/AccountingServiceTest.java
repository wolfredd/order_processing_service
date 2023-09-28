package com.sagatrading.service;

import com.sagatrading.contract.AccountBalanceManager;
import com.sagatrading.model.AccountBalance;
import com.sagatrading.repository.AccountBalanceRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static net.bytebuddy.matcher.ElementMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class AccountingServiceTest {
    AccountBalanceManager accountBalanceManager;

    AccountBalanceRepository accountBalanceRepositoryMock = Mockito.mock(AccountBalanceRepository.class);

    @BeforeEach
    void setUp() {
        accountBalanceManager = new AccountingService(accountBalanceRepositoryMock);
    }

    @Test
    void getAccountBalance() {
        Double accountBalance = accountBalanceManager.getAccountBalance(4);
        AccountBalance testAccountBalance = new AccountBalance();
        testAccountBalance.setClientId(4);
        testAccountBalance.setAmount(0.0);
        Assertions.assertEquals(0.0, accountBalance);
        verify(accountBalanceRepositoryMock, times(1))
                .save(testAccountBalance);
    }

    @Test
    void topUpAccount() {
        Double accountBalance = accountBalanceManager.topUpAccount(4, 20.0);
        Assertions.assertEquals(20, accountBalance);
    }

    @Test
    void deductFromAccount() {
    }
}