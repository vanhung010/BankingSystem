package org.vhung.enity;

import org.vhung.enity.enums.AccountStatus;
import org.vhung.pattern.strategy.InterestStrategy;

import java.time.LocalDate;
import java.util.Objects;

public abstract class Account {
    private int accountId;
    private double balance;
    private AccountStatus accountStatus;
    private Customer owner;
    private LocalDate createdAt;
    private InterestStrategy interestStrategy;

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }



    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public Customer getOwner() {
        return owner;
    }

    public void setOwner(Customer owner) {
        this.owner = owner;
    }

    public InterestStrategy getInterestStrategy() {
        return interestStrategy;
    }

    public void setInterestStrategy(InterestStrategy interestStrategy) {
        this.interestStrategy = interestStrategy;
    }

    public abstract void deposit(double amount);
    public abstract void withdraw(double amount);

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return accountId == account.accountId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(accountId);
    }
}
