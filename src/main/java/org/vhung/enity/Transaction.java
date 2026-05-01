package org.vhung.enity;

import org.vhung.enity.enums.TransactionType;

import java.time.LocalDateTime;

public class Transaction {
    private int transactionId;
    private TransactionType transactionType;
    private double amount;
    private LocalDateTime timestamp;
    private Integer plusAccountId;
    private Integer minustAccountId;
    private String description;

    public Transaction() {
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMinustAccountId() {
        return minustAccountId;
    }

    public void setMinustAccountId(Integer minustAccountId) {
        this.minustAccountId = minustAccountId;
    }

    public Integer getPlusAccountId() {
        return plusAccountId;
    }

    public void setPlusAccountId(Integer plusAccountId) {
        this.plusAccountId = plusAccountId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Transaction(TransactionType transactionType, double amount, LocalDateTime timestamp, Integer plusAccountId, Integer minusAccountId, String description) {
        this.transactionType = transactionType;
        this.amount = amount;
        this.timestamp = timestamp;
        this.plusAccountId = plusAccountId;
        this.minustAccountId = minusAccountId;
        this.description = description;
    }
}
