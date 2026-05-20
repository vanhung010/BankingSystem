package org.vhung.service;

import org.vhung.dao.AccountDao;
import org.vhung.dao.SystemDao;
import org.vhung.dao.TransactionDao;
import org.vhung.enity.LoanAccount;
import org.vhung.enity.Transaction;
import org.vhung.enity.enums.TransactionType;

import java.time.LocalDateTime;
import java.util.List;

public class InterestService {
    private AccountDao accountDao;
    private SystemDao systemDao;
    private TransactionDao transactionDao;
    public InterestService() {
        this.accountDao = new AccountDao();
        this.systemDao = new SystemDao();
        this.transactionDao = new TransactionDao();
    }

    public void autoUpdateInterestLoanMonthly(){
        List<LoanAccount> loanAccountList = accountDao.getAllLoanAccountActive();
        for(LoanAccount loanAccount: loanAccountList){
            double interestLoan = systemDao.getInterestLoan();
            //số tiên lãi phải trả trong tháng của tài khoản vay
            double interestLoanAccountInMonth = loanAccount.getInterestStrategy().calcInterest(loanAccount.getPricipalAmount(), interestLoan, loanAccount.getLoanTerm());
            //số tiền nợ ban đầu
            double balanceBefore = loanAccount.getBalance();
            //thực hiện cộng tiền
            loanAccount.setBalance(balanceBefore + interestLoanAccountInMonth);
            //cập nật số dư
            accountDao.updateBalance(loanAccount.getAccountId(), loanAccount.getBalance());
            //lưu giao dịch
            Transaction transaction = new Transaction(TransactionType.INTEREST_PAYMENT, interestLoanAccountInMonth, LocalDateTime.now(), loanAccount.getAccountId(), null, "Cộng tiền lãi hàng tháng");
            transactionDao.addTransactionPlus(transaction);
        }

    }



}
