package org.vhung.enity;

public class CheckingAccount extends Account{
    private double minBalance;

    @Override
    public void deposit(double amount) {
        this.setBalance(this.getBalance() + amount);
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
}
