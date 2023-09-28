package com.sagatrading.repository;

import com.sagatrading.model.AccountBalance;
import com.sagatrading.model.Order;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccountBalanceRepository extends JpaRepository<AccountBalance, UUID> {
    List<AccountBalance> findByClientId(int clientId);

}
