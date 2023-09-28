package com.sagatrading.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Data
public class AccountBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @CreationTimestamp
    @JsonProperty("timestamp")
    @Column(nullable = false)
    private LocalDateTime createdAt;

    private Integer clientId;

    private Double amount;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountBalance that = (AccountBalance) o;
        return Objects.equals(clientId, that.clientId) && Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, amount);
    }

}
