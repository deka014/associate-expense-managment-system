package com.adp.ExpenseService.dtos;


import com.adp.ExpenseService.enums.ExpenseCategory;
import com.adp.ExpenseService.enums.ExpenseStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.sql.Date;

public class ExpenseDTO {
    private Integer userId;
    @Enumerated(EnumType.STRING)
    private ExpenseCategory category;
    private Date expenseDate;
    private ExpenseStatus expenseStatus;
    private String mgrComment;
    private String description;
    private Float amount;


    public ExpenseDTO() {
    }


    public ExpenseDTO(Integer userId, ExpenseCategory category, Date expenseDate, ExpenseStatus expenseStatus,
                      String mgrComment, String description, Float amount) {
        super();
        this.userId = userId;
        this.category = category;
        this.expenseDate = expenseDate;
        this.expenseStatus = expenseStatus;
        this.mgrComment = mgrComment;
        this.description = description;
        this.amount = amount;
    }


    public Integer getUserId() {
        return userId;
    }


    public void setUserId(Integer userId) {
        this.userId = userId;
    }


    public ExpenseCategory getCategory() {
        return category;
    }


    public void setCategory(ExpenseCategory category) {
        this.category = category;
    }


    public Date getExpenseDate() {
        return expenseDate;
    }


    public void setExpenseDate(Date expenseDate) {
        this.expenseDate = expenseDate;
    }


    public ExpenseStatus getExpenseStatus() {
        return expenseStatus;
    }


    public void setExpenseStatus(ExpenseStatus expenseStatus) {
        this.expenseStatus = expenseStatus;
    }


    public String getMgrComment() {
        return mgrComment;
    }


    public void setMgrComment(String mgrComment) {
        this.mgrComment = mgrComment;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public Float getAmount() {
        return amount;
    }


    public void setAmount(Float amount) {
        this.amount = amount;
    }


    @Override
    public String toString() {
        return "ExpenseDTO [userId=" + userId + ", category=" + category + ", expenseDate=" + expenseDate
                + ", expenseStatus=" + expenseStatus + ", mgrComment=" + mgrComment + ", description=" + description
                + ", amount=" + amount + "]";
    }

}