package org.vhung.service;

import org.vhung.dao.AccountDao;
import org.vhung.dao.SystemDao;
import org.vhung.dao.TransactionDao;
import org.vhung.dao.UserDao;
import org.vhung.enity.*;
import org.vhung.enity.enums.TransactionType;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class AccountService {
    private AccountDao accountDao;
    private TransactionDao transactionDao;
    private SystemDao systemDao;
    private UserDao userDao = new UserDao();

    public AccountService() {
        accountDao = new AccountDao();
        transactionDao = new TransactionDao();
        systemDao = new SystemDao();
    }
    //mở tài khoản thanh toán
    public boolean openCheckingAccount(int idOwner, double balance) {
        return accountDao.addCheckingAccount(idOwner, balance);
    }
    //lấy danh sách tất cả tài khoản
    public List<Account> getAllAccount(int idCustomer) {
        return accountDao.getAllAccountOfCustomerDao(idCustomer);
    }
    //Mở tài khoản tiết kiệm
    public void openSavingAccount(CheckingAccount checkingAccount, double amount, int term){
        if(amount<1000000){
            throw new RuntimeException("Số tiền tối thiểu 1 triệu");
        }
        else if(checkingAccount.getBalance() < amount){
            throw new RuntimeException("Không đủ số dư tài khoản thanh toán để mở tài khoản");
        }
        //nếu không có lỗi thì tạo tài khoản
        accountDao.addSavingAccount(checkingAccount.getOwner().getUserId(), amount, term);
        //trừ tiền taài khoản gốc
        checkingAccount.withdraw(amount);
        Transaction transaction = new Transaction(TransactionType.OPEN_SAVING, amount, systemDao.getTimeSystem().atTime(LocalTime.now()), null, checkingAccount.getAccountId(), "Mở tài khoản tiết kiệm");
        transactionDao.addTransactionMinus(transaction);
        //dữ liệu

        accountDao.updateBalance(checkingAccount.getAccountId(), checkingAccount.getBalance());

    }
    public Account getAccountById(int idAccount){
        Account account = accountDao.getAccountById(idAccount);
        if(account == null){
            throw new RuntimeException("Lỗi không tìm thấy tài khoản");
        }
        return account;
    }

    public List<SavingAccount> getAllAccountSavingOfCustomer(int idCustomer){
        return accountDao.getAllAccountSavingOfCustomer(idCustomer);
    }
    public List<LoanAccount> getActiveLoanAccountsByCustomer(Customer customer) {
        return accountDao.getActiveLoanAccountsByCustomer(customer);
    }

    public List<CheckingAccount> getActiveCheckingAccountsByCustomer(Customer customer) {
        return accountDao.getActiveCheckingAccountsByCustomer(customer);
    }

    public void updateStatusAccount(Account account, String status) {
        accountDao.updateStatusAccount(account, status);
    }

    public Customer getCustomerbyId(int idCus){
        Customer customer = userDao.getCustomerById(idCus);
        if(customer == null){
            throw new RuntimeException("Không tìm thấy khách hàng");
        }
        return customer;
    }


}
