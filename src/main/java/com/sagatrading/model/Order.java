package com.sagatrading.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "_order")
public class Order {
    @Id
    private UUID id;

    @CreationTimestamp
    @JsonProperty("timestamp")
    @Column(nullable = false)
    private LocalDateTime createdAt;

    private Integer clientId;

    private String product;

    private int quantity;

    private int cumulativeQuantity;

    private double price;

    @Enumerated(EnumType.STRING)
    private OrderSide side;

    @Enumerated(EnumType.STRING)
    private OrderType type;

    private boolean isMultiLeg;

    private String exchange;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private UUID referenceCode;

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public OrderSide getSide() {
        return side;
    }

    public void setSide(OrderSide side) {
        this.side = side;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }


    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public boolean isMultiLeg() {
        return isMultiLeg;
    }

    public void setMultiLeg(boolean multiLeg) {
        isMultiLeg = multiLeg;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public UUID getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(UUID referenceCode) {
        this.referenceCode = referenceCode;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", clientId=" + clientId +
                ", product='" + product + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", side=" + side +
                ", type=" + type +
                ", isMultiLeg=" + isMultiLeg +
                ", exchange='" + exchange + '\'' +
                ", status=" + status +
                ", referenceCode=" + referenceCode +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return quantity == order.quantity && Double.compare(order.price, price) == 0 && isMultiLeg == order.isMultiLeg && id.equals(order.id) && clientId.equals(order.clientId) && product.equals(order.product) && side == order.side && type == order.type && Objects.equals(exchange, order.exchange) && status == order.status && Objects.equals(referenceCode, order.referenceCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clientId, product, quantity, price, side, type, isMultiLeg, exchange, status, referenceCode);
    }

    public int getCumulativeQuantity() {
        return cumulativeQuantity;
    }

    public void setCumulativeQuantity(int cumulativeQuantity) {
        this.cumulativeQuantity = cumulativeQuantity;
    }
}
