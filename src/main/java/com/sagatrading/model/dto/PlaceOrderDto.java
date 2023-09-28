package com.sagatrading.model.dto;

import com.sagatrading.model.Order;
import com.sagatrading.model.OrderSide;
import com.sagatrading.model.OrderType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PlaceOrderDto {
    @NotNull(message = "The client id is required")
    private Integer clientId;

    @NotEmpty(message = "Product is required")
    private String product;

    @NotNull(message = "Stock quantity is required")
    private int quantity;

    private double price;

    @NotNull(message = "Order side is required")
    private OrderSide side;

    @NotNull(message = "Order type is required")
    private OrderType type;

    public Order toOrder() {
        Order order = new Order();
        order.setClientId(clientId);
        order.setProduct(product);
        order.setQuantity(quantity);
        order.setPrice(price);
        order.setSide(side);
        order.setType(type);
        return order;
    }
}
