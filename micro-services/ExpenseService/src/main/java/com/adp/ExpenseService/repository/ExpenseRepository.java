package com.adp.ExpenseService.repository;

import com.adp.ExpenseService.entities.Expense;
import com.adp.ExpenseService.entities.User;
import com.adp.ExpenseService.enums.ExpenseCategory;
import com.adp.ExpenseService.enums.ExpenseStatus;
import com.adp.ExpenseService.enums.SubmitStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {

    public List<Expense> findByUserId(User userId);

    public Optional<Expense> findById(Integer userId);

    public List<Expense> findExpenseByCategoryAndUserId(ExpenseCategory category, User userId);

    public List<Expense> findByExpenseDateBetweenAndUserId(Date expenseDate, Date expenseDate2, User userId);

    public List<Expense> findByUserIdAndStatus(User user, ExpenseStatus status);

    public List<Expense> findBySubmitStatusAndUserId(SubmitStatus submitStatus, User user);

}
