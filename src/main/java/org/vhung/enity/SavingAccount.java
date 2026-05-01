package org.vhung.enity;

import java.time.LocalDate;

public class SavingAccount extends Account{
    private Customer customerOwner;
    private int term;
    private LocalDate depositDate;
    private LocalDate maturityDate;
    private double interest; //% lãi mỗi năm

    public Customer getCustomerOwner() {
        return customerOwner;
    }

    public void setCustomerOwner(Customer customerOwner) {
        this.customerOwner = customerOwner;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public LocalDate getDepositDate() {
        return depositDate;
    }

    public void setDepositDate(LocalDate depositDate) {
        this.depositDate = depositDate;
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(LocalDate maturityDate) {
        this.maturityDate = maturityDate;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    @Override
    public void deposit(double amount) {
      this.setBalance(this.getBalance()+amount);
    }

    @Override
    public void withdraw(double amount) {
        if(this.getBalance() < amount){
            throw new RuntimeException("Không đủ số dư để thực hiện thao tác!");
        }
        else{
            this.setBalance(this.getBalance()-amount);
        }
    }


    public double calcInterestAmount(){
        return super.getInterestStrategy().calcInterest(this.getBalance(), this.getInterest(), this.getTerm());
    }

    public void savingExtension(){
        //cộng tiền lãi vào tài khoản
        //cập nhật ngày bắt đầu = ngày hết hạn
        //cập nhật ngày hết hạn mới
        this.setBalance(calcInterestAmount()+this.getBalance());
        this.setDepositDate(this.getMaturityDate());
        this.setMaturityDate(this.getDepositDate().plusMonths(term));
    }
}
