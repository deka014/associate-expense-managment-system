package com.adp.ExpenseService.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "Receipt_Group3_Oct3_2")
public class Receipt {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "expenseId")
    private Expense expenseId;

    @Column
    private String fileType;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String receiptFile;

    public Receipt() {
    }

    public Receipt(Integer id, Expense expenseId, String receiptFile, String fileType) {
        this.id = id;
        this.expenseId = expenseId;
        this.receiptFile = receiptFile;
        this.fileType = fileType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Expense getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(Expense expenseId) {
        this.expenseId = expenseId;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getReceiptFile() {
        return receiptFile;
    }

    public void setReceiptFile(String receiptFile) {
        this.receiptFile = receiptFile;
    }
}
