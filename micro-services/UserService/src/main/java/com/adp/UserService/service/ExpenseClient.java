package com.adp.UserService.service;


import com.adp.UserService.entities.Expense;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(url = "http://localhost:5003/expense", value = "Expense-Client")
public interface ExpenseClient {

    @GetMapping("/{id}")
    List<Expense> getExpenseByUserId(@PathVariable Integer id);

    @GetMapping("/{id}/{category}")
    List<Expense> getExpensesByCategoryAndId(@PathVariable Integer id,@PathVariable String category);

}
