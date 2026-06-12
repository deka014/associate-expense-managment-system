package com.adp.ExpenseService.repository;

import com.adp.ExpenseService.entities.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptRepository extends JpaRepository<Receipt, Integer> {

}

