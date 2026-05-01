package org.vhung.enity;

import org.vhung.dao.SystemDao;
import org.vhung.pattern.strategy.LoanInterestStrategy;

import java.time.LocalDate;

public class LoanAccount extends Account{

    private Account accountOwner;
    private double pricipalAmount;
    private double interestRate;
    private LocalDate nextPaymentDate;
    private int loanTerm;
    private double amountPaidThisMonth; //số tiền đã trả trong tháng
    private double monthlyRequiredPayment; //số tiền phải trả tối thiểu trong tháng


    public LoanAccount() {
        super.setInterestStrategy(new LoanInterestStrategy());
    }

    public double getMonthlyRequiredPayment() {
        return (this.getBalance() * (this.interestRate / 12) + (this.pricipalAmount / this.loanTerm));
    }

    public void setMonthlyRequiredPayment(double monthlyRequiredPayment) {
        this.monthlyRequiredPayment = monthlyRequiredPayment;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public Account getAccountOwner() {
        return accountOwner;
    }

    public void setAccountOwner(Account accountOwner) {
        this.accountOwner = accountOwner;
    }

    public double getPricipalAmount() {
        return pricipalAmount;
    }

    public void setPricipalAmount(double pricipalAmount) {
        this.pricipalAmount = pricipalAmount;
    }
    public LocalDate getNextPaymentDate() {
        return nextPaymentDate;
    }

    public void setNextPaymentDate(LocalDate nextPaymentDate) {
        this.nextPaymentDate = nextPaymentDate;
    }

    public int getLoanTerm() {
        return loanTerm;
    }

    public void setLoanTerm(int loanTerm) {
        this.loanTerm = loanTerm;
    }
    public double getAmountPaid() {
        // Lấy Tiền gốc trừ đi Dư nợ hiện tại (kế thừa từ class Account cha)
        return this.pricipalAmount - super.getBalance();
    }

    public double getAmountPaidThisMonth() {
        return amountPaidThisMonth;
    }

    public void setAmountPaidThisMonth(double amountPaidThisMonth) {
        this.amountPaidThisMonth = amountPaidThisMonth;
    }

    public LocalDate getDueDate() {
        // Lấy ngày tạo cộng thêm số tháng kỳ hạn
        return super.getCreatedAt().plusMonths(this.loanTerm);
    }
    @Override
    public void deposit(double amount) {
        throw new RuntimeException("Tài khoản vay không được thực hiện nạp tiền");
    }

    @Override
    public void withdraw(double amount) {
        if(this.getBalance() < amount){
            throw new RuntimeException("Không đủ số dư để thực hiện!");
        }
        else{
            this.setBalance(this.getBalance() - amount);
        }
    }
    //kiểm tra trả đủ chưa
    public boolean checkPaid(){
        return this.amountPaidThisMonth >= this.monthlyRequiredPayment;
    }
}
