package org.vhung.pattern.factory;

import org.vhung.dao.SystemDao;
import org.vhung.dao.UserDao;
import org.vhung.enity.*;
import org.vhung.enity.enums.AccountStatus;
import org.vhung.pattern.strategy.LoanInterestStrategy;
import org.vhung.pattern.strategy.TermInterestStrategy;

import java.time.LocalDate;

public class AccountFactory {
    private static SystemDao systemDao = new SystemDao();
    public static Account createAccount(String type, Customer customerOwner){
        Account account = null;
        switch (type.toUpperCase()) {
            case "CHECKING":
              account = new CheckingAccount();
              CheckingAccount accountReturn = (CheckingAccount) account;
                accountReturn.setAccountStatus(AccountStatus.ACTIVE);
                accountReturn.setOwner(customerOwner);
                accountReturn.setCreatedAt(LocalDate.now());
              return accountReturn;
            case "SAVING":
               account = new SavingAccount();
               SavingAccount accountReturn1 = (SavingAccount) account;
               accountReturn1.setCustomerOwner(customerOwner);
               accountReturn1.setDepositDate(LocalDate.now());
               accountReturn1.setInterestStrategy(new TermInterestStrategy());
                accountReturn1.setAccountStatus(AccountStatus.ACTIVE);
                accountReturn1.setOwner(customerOwner);
                accountReturn1.setCreatedAt(LocalDate.now());

               return accountReturn1;
            case "LOAN":
               account = new LoanAccount();
               LoanAccount accountReturn2 = (LoanAccount) account;
                accountReturn2.setInterestStrategy(new LoanInterestStrategy());
                double rate = systemDao.getInterestLoan();
                accountReturn2.setInterestRate(accountReturn2.getInterestStrategy().calcInterest(accountReturn2.getPricipalAmount(), rate, accountReturn2.getLoanTerm()));
                accountReturn2.setMonthlyRequiredPayment(0);
                accountReturn2.setAccountStatus(AccountStatus.ACTIVE);
                accountReturn2.setOwner(customerOwner);
                accountReturn2.setCreatedAt(LocalDate.now());
                 return accountReturn2;

            default:
                throw new RuntimeException("Loại tài khoản không hợp lệ!");

        }
    }
}
