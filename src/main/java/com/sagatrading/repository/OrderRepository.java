package com.sagatrading.repository;

import com.sagatrading.model.Order;
import com.sagatrading.model.OrderStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByClientId(int clientId, Sort sort);
    List<Order> findByClientIdAndProduct(int clientId, String product);

    List<Order> findAllByStatus(OrderStatus orderStatus);
}
