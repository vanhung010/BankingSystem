package org.vhung.System;

import java.time.LocalDate;

public class BankSystem {
    private static double minCheckingBalance;
    private static LocalDate systemDate;
    private static double demandInterestRate;
    private static double interestRate1M;
    private static double interestRate6M;
    private static double interestRate12M;
    private static double baseLoanInterestRate;
    private static double minSavingDeposit;


    private static BankSystem instance;
    public BankSystem() {
    }

    public double getMinCheckingBalance() {
        return minCheckingBalance;
    }

    public void setMinCheckingBalance(double minCheckingBalance) {
        this.minCheckingBalance = minCheckingBalance;
    }

    public static BankSystem getInstance(){
        instance = new BankSystem();
        return instance;
    }


    public  double getMinSavingDeposit() {
        return minSavingDeposit;
    }

    public  void setMinSavingDeposit(double minSavingDeposit) {
        BankSystem.minSavingDeposit = minSavingDeposit;
    }

    public  LocalDate getSystemDate() {
        return systemDate;
    }

    public  void setSystemDate(LocalDate systemDate) {
        BankSystem.systemDate = systemDate;
    }

    public  double getDemandInterestRate() {
        return demandInterestRate;
    }

    public  void setDemandInterestRate(double demandInterestRate) {
        BankSystem.demandInterestRate = demandInterestRate;
    }

    public  double getInterestRate1M() {
        return interestRate1M;
    }

    public  void setInterestRate1M(double interestRate1M) {
        BankSystem.interestRate1M = interestRate1M;
    }

    public  double getInterestRate6M() {
        return interestRate6M;
    }

    public  void setInterestRate6M(double interestRate6M) {
        BankSystem.interestRate6M = interestRate6M;
    }

    public double getInterestRate12M() {
        return interestRate12M;
    }

    public void setInterestRate12M(double interestRate12M) {
        BankSystem.interestRate12M = interestRate12M;
    }

    public  double getBaseLoanInterestRate() {
        return baseLoanInterestRate;
    }

    public  void setBaseLoanInterestRate(double baseLoanInterestRate) {
        BankSystem.baseLoanInterestRate = baseLoanInterestRate;
    }

    public static void setInstance(BankSystem instance) {
        BankSystem.instance = instance;
    }
}
