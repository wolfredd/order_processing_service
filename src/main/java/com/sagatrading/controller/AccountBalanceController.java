package com.sagatrading.controller;

import com.sagatrading.contract.AccountBalanceManager;
import com.sagatrading.model.Order;
import com.sagatrading.model.OrderStatus;
import com.sagatrading.model.dto.OrderBookItem;
import com.sagatrading.model.dto.PlaceOrderDto;
import com.sagatrading.model.dto.TopupAmount;
import com.sagatrading.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/account-balance")
@CrossOrigin(origins = "*")
public class AccountBalanceController {
    @Autowired
    AccountBalanceManager accountBalanceManager;

    @GetMapping("/{clientId}")
    public Double getAccountBalance(@PathVariable int clientId) {
        return accountBalanceManager.getAccountBalance(clientId);
    }
    @PostMapping("/{clientId}")
    public Double topUpAccountBalance(@PathVariable int clientId, @RequestBody TopupAmount topupAmount) {
        return accountBalanceManager.topUpAccount(clientId, topupAmount.getAmount());
    }

}





