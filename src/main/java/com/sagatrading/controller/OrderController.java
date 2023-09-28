package com.sagatrading.controller;

import com.sagatrading.model.Order;
import com.sagatrading.model.OrderStatus;
import com.sagatrading.model.dto.OrderBookItem;
import com.sagatrading.model.dto.PlaceOrderDto;
import com.sagatrading.service.OrderService;
import com.sagatrading.service.OrderValidationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/order")
@CrossOrigin(origins = "*")
public class OrderController {
    // Todo: Validate order values with exchanges
    @Autowired
    OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UUID submitOrder(@Valid @RequestBody PlaceOrderDto placeOrderDto) {
        Order order = placeOrderDto.toOrder();
        return orderService.processOrder(order);
    }

    @PostMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public UUID updateOrder(@Valid @RequestBody PlaceOrderDto placeOrderDto, @PathVariable UUID orderId) {
        Order order = placeOrderDto.toOrder();
        return orderService.updateOrder(order, orderId);
    }

    @GetMapping("/pd")
    public boolean getProductDetails() {
        orderService.getProductDetails();
        return false;
    }

    @GetMapping("/client/{clientId}/all")
    public List<Order> getClientOrders(@PathVariable int clientId) {
        return orderService.getOrdersByClientId(clientId);
    }

    @GetMapping("/trades/{clientId}/{status}")
    public List<Order> getTrades(@PathVariable int clientId,@PathVariable OrderStatus status) {
        return orderService.getOrdersByClientId(clientId);
    }

    @DeleteMapping("/{orderId}")
    public void cancelClientOrders(@PathVariable UUID orderId) {
        orderService.cancelOrder(orderId);
    }

    @GetMapping("/product/{product}/status/{status}/exchange/{exchange}")
    public List<OrderBookItem> getMarketOrders(@PathVariable String product, @PathVariable String status, @PathVariable String exchange){
        return orderService.getMarketOrders(product, status, exchange );
    }

    @GetMapping("/price-history/{product}/{exchange}")
    public List<Double> getPriceHistory(@PathVariable String product, @PathVariable String exchange) {
        return orderService.getPriceHistory(product, exchange);
    }

    @GetMapping("/pendingorders")
    public List<Order> getPendingOrders() {
        return orderService.getPendingOrders();
    }

    @GetMapping("/cancelledorders")
    public List<Order> getCancelledOrders() {
        return orderService.getCancelledOrders();
    }

    @GetMapping("/filledorders")
    public List<Order> getFilledOrders() {
        return orderService.getFilledOrders();
    }

    @GetMapping("/failedorders")
    public List<Order> getFailedOrders() {
        return orderService.getFailedOrders();
    }

    @GetMapping("/getallorders")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }
}





