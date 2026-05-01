package org.vhung.pattern.strategy;

public interface InterestStrategy {
    double calcInterest(double principal, double rate, int time);
}
