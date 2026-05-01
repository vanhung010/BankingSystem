package org.vhung.service;

import org.vhung.dao.*;
import org.vhung.enity.Account;
import org.vhung.enity.CheckingAccount;
import org.vhung.enity.SavingAccount;
import org.vhung.enity.Transaction;
import org.vhung.enity.enums.TransactionType;
import org.vhung.pattern.strategy.DemandInterestStrategy;
import org.vhung.view.CustomerView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class SavingService {

    private AccountDao accountDao;
    private SystemDao systemDao;
    private TransactionDao transactionDao;
    private CustomerView customerUI;
    private UserDao userDao;
    public SavingService() {
        this.accountDao = new AccountDao();
        this.systemDao = new SystemDao();
        this.transactionDao = new TransactionDao();
       this.userDao = new UserDao();
    }

    public void checkSavingAccountExpried(){
        List<SavingAccount> savingAccountList = accountDao.getAllSavingAccountActive();
        for(SavingAccount savingAccount : savingAccountList){
            //Nếu đã lố ngày tất toán thực hiện gia hạn
            if(savingAccount.getMaturityDate().isBefore(systemDao.getTimeSystem())){
                //tạo lịch sử giao dịch
                //số tiền lãi cộng vào
                double amount = savingAccount.getInterestStrategy().calcInterest(savingAccount.getBalance(), savingAccount.getInterest(), savingAccount.getTerm());
                Transaction transaction = new Transaction(TransactionType.INTEREST_PAYMENT, amount, systemDao.getTimeSystem().atTime(LocalTime.now()), savingAccount.getAccountId(), null, "Cộng tiền lãi");
                transactionDao.addTransactionPlus(transaction);

                savingAccount.savingExtension();//thực hiện gia hạn, cập nhật số tiền, cập nhật ngày tháng
                //cập nhật soos dư
                accountDao.updateBalance(savingAccount.getAccountId(), savingAccount.getBalance());
                //cập nhật ngày tháng
                accountDao.updateDateSavingAccount(savingAccount);
            }
        }
    }

    public void closeddSavingAccount(CheckingAccount checkingAccount, SavingAccount savingAccount){
        LocalDate systemDate = systemDao.getTimeSystem();
        double interestAmount =0;

        //xử lí trước hạn
        if(systemDate.isBefore(savingAccount.getMaturityDate())){
            //xác thực
            customerUI = new CustomerView(userDao.getCustomerById(checkingAccount.getOwner().getUserId()));
            boolean choice = customerUI.validSaving();
            if(choice == false){
                throw new RuntimeException("Thoát thành công");
            }
            //đổi chiến lược
            savingAccount.setInterestStrategy(new DemandInterestStrategy());
            //cập nhật lãi không kì hạn
            savingAccount.setInterest(systemDao.getInterestDemand());
            //tính lãi
            long totalTimeDay = ChronoUnit.DAYS.between(savingAccount.getDepositDate(), systemDate);
            int totalDay = (int) totalTimeDay;
            interestAmount = savingAccount.getInterestStrategy().calcInterest(savingAccount.getBalance(), savingAccount.getInterest(), totalDay);

        }
        else {
            //Nếu đúng hạng thì ấy
            interestAmount =savingAccount.calcInterestAmount();
        }


        //tổng tiền nhận được
        double totalAmount = interestAmount + savingAccount.getBalance();
        //trừ tiền tài khoản tiết kiệm
        savingAccount.setBalance(0);
        //cập nhật trạng thái
        accountDao.updateStatusAccount(savingAccount, "CLOSED");
        //cộng tiền tài khoản thanh toán
        checkingAccount.deposit(totalAmount);
        //Tạo giao dịch
        Transaction transaction = new Transaction(TransactionType.CLOSE_SAVING, totalAmount, systemDate.atTime(LocalTime.now()), checkingAccount.getAccountId(), savingAccount.getAccountId(),"Đóng sổ tiết kiệm");
        transactionDao.addTransactionMinus(transaction);
        transactionDao.addTransactionPlus(transaction);
        //cập nhật số dư 2 tài khoản

        accountDao.updateBalance(checkingAccount.getAccountId(), checkingAccount.getBalance());
        accountDao.updateBalance(savingAccount.getAccountId(), 0);

    }
    }

