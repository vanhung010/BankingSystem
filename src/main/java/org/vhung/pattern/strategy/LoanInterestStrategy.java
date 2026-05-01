package org.vhung.pattern.strategy;

public class LoanInterestStrategy implements InterestStrategy{
    @Override
    public double calcInterest(double principal, double rate, int time) {
        return (principal * rate) /12;
    }
}
