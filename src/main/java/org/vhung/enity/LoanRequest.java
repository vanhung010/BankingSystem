package org.vhung.enity;

import org.vhung.enity.enums.LoanRequestStatus;

import java.time.LocalDateTime;

public class LoanRequest {
    private int loanRequestId;
    private Customer customerOwner;
    private double requestAmount;
    private LoanRequestStatus status;
    private LocalDateTime requestDate;
    private int loanTerm;

    public LoanRequest() {
        this.status = LoanRequestStatus.PENDING;
        this.requestDate = LocalDateTime.now();
    }

    public int getLoanRequestId() {
        return loanRequestId;
    }

    public void setLoanRequestId(int loanRequestId) {
        this.loanRequestId = loanRequestId;
    }

    public int getLoanTerm() {
        return loanTerm;
    }

    public void setLoanTerm(int loanTerm) {
        this.loanTerm = loanTerm;
    }

    public Customer getCustomerOwner() {
        return customerOwner;
    }

    public void setCustomerOwner(Customer customerOwner) {
        this.customerOwner = customerOwner;
    }

    public double getRequestAmount() {
        return requestAmount;
    }

    public void setRequestAmount(double requestAmount) {
        this.requestAmount = requestAmount;
    }

    public LoanRequestStatus getStatus() {
        return status;
    }

    public void setStatus(LoanRequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }
}
