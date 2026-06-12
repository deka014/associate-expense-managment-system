package com.adp.ExpenseService.entities;

import java.sql.Date;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import com.adp.ExpenseService.enums.*;

@Entity
@Table(name = "Expense_Group3_Oct3_2")
public class Expense {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "userId")
    private User userId;

    @Column(columnDefinition = "VARCHAR(50)")
    @Enumerated(EnumType.STRING)
    private ExpenseCategory category;

    @Column
    private Date expenseDate;

    @Column(name = "expenseComment")
    private String comment;

    @Column(columnDefinition = "VARCHAR(50)")
    @Enumerated(EnumType.STRING)
    private SubmitStatus submitStatus;

    @JsonManagedReference
    @OneToOne(mappedBy = "expenseId", cascade = CascadeType.ALL)
    private Receipt receipt;

    @Column(columnDefinition = "VARCHAR(50)")
    @Enumerated(EnumType.STRING)
    private ExpenseStatus status = ExpenseStatus.PENDING;

    @Column
    private String description;

    @Column
    private String mgrComment;

    @Column
    private Float amount;

    public Expense() {
        super();
    }

    public Expense(Integer id, User userId, ExpenseCategory category, Date expenseDate, String comment,
                   SubmitStatus submitStatus, Receipt receipt, ExpenseStatus status, String description, String mgrComment,
                   Float amount) {
        super();
        this.id = id;
        this.userId = userId;
        this.category = category;
        this.expenseDate = expenseDate;
        this.comment = comment;
        this.submitStatus = submitStatus;
        this.receipt = receipt;
        this.status = status;
        this.description = description;
        this.mgrComment = mgrComment;
        this.amount = amount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public SubmitStatus getSubmitStatus() {
        return submitStatus;
    }

    public void setSubmitStatus(SubmitStatus submitStatus) {
        this.submitStatus = submitStatus;
    }

    public Receipt getReceipt() {
        return receipt;
    }

    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
    }

    public ExpenseStatus getStatus() {
        return status;
    }

    public void setStatus(ExpenseStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMgrComment() {
        return mgrComment;
    }

    public void setMgrComment(String mgrComment) {
        this.mgrComment = mgrComment;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Expense [id=" + id + ", userId=" + (userId != null ? userId.getId() : null) + ", category=" + category + ", expenseDate=" + expenseDate
                + ", comment=" + comment + ", submitStatus=" + submitStatus + ", status="
                + status + ", description=" + description + ", mgrComment=" + mgrComment + ", amount=" + amount + "]";
    }
}
