package org.vhung.pattern.strategy;

public class TermInterestStrategy implements InterestStrategy{
    @Override
    public double calcInterest(double principal, double rate, int time) {
        return principal * rate *((double) time/12);
    }
}
