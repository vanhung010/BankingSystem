package org.vhung.service;

import org.vhung.dao.AccountDao;
import org.vhung.dao.TransactionDao;
import org.vhung.enity.Account;
import org.vhung.enity.CheckingAccount;
import org.vhung.enity.Transaction;
import org.vhung.enity.enums.TransactionType;

import java.time.LocalDateTime;
import java.util.List;

public class TransactionService {
    private AccountDao accountDao;
    private TransactionDao transactionDao;

    public TransactionService() {
        accountDao = new AccountDao();
        transactionDao = new TransactionDao();
    }
//nạp tiền
    public void DepositCheckingAccount(int idCustomer, int idAccount, double amount) {
        Account account = accountDao.getAccountById(idAccount);
        List<Account> listAccountOfCustomer = accountDao.getAllAccountOfCustomerDao(idCustomer);
        if (account == null) {
            throw new RuntimeException("Không tìm thấy tài khoản thanh toán!");
        }

        else if (!(account instanceof CheckingAccount)) {
            throw new RuntimeException("tài khoản không phải tài khoản thanh toán!");
        } else if (!listAccountOfCustomer.contains(account)) {
            throw new RuntimeException("Tài khoản đã chọn không có trong danh sách tài khoản của khách hàng!");

        }
        account.getAccountStatus().handle();
        //ép kiểu xuống
        CheckingAccount checkingAccount = (CheckingAccount) account;
        //thực hiện cộng tiền
        checkingAccount.deposit(amount);
        //lưu giao dịch
        Transaction transaction = new Transaction(TransactionType.DEPOSIT, amount, LocalDateTime.now(), idAccount, null, "Nạp tiền");
        transactionDao.addTransactionPlus(transaction);

        accountDao.updateBalance(account.getAccountId(), account.getBalance());
    }
    //rút tiền
    public void withdrawCheckingAccount(int idCustomer, int idAccount, double amount) {
        Account account = accountDao.getAccountById(idAccount);
        List<Account> listAccountOfCustomer = accountDao.getAllAccountOfCustomerDao(idCustomer);
        if (account == null) {
            throw new RuntimeException("Không tìm thấy tài khoản thanh toán!");
        }
        else if (!(account instanceof CheckingAccount)) {
            throw new RuntimeException("tài khoản không phải tài khoản thanh toán!");
        } else if (!listAccountOfCustomer.contains(account)) {
            throw new RuntimeException("Tài khoản đã chọn không có trong danh sách tài khoản của khách hàng!");
        }
        account.getAccountStatus().handle();
        //ép kiểu xuống
        CheckingAccount checkingAccount = (CheckingAccount) account;
        checkingAccount.withdraw(amount);
        //lưu giao dịch
        Transaction transaction = new Transaction(TransactionType.WITHDRAW, -amount, LocalDateTime.now(), idAccount, null, "Rút tiền");
        transactionDao.addTransactionMinus(transaction);

        accountDao.updateBalance(account.getAccountId(), account.getBalance());
    }
    //Chuyển tiền
    public void tranfer(double amount, int idAccontSource, int idAccountTarget, String description){
        Account accountSource = accountDao.getAccountById(idAccontSource);
        Account accountTarget = accountDao.getAccountById(idAccountTarget);

        if(accountSource == null || accountTarget == null){
            throw new RuntimeException("Tài khoản nguồn hoặc tài khoản nhận không hợp lệ!");
        }
        else if(!(accountSource instanceof CheckingAccount) || !(accountTarget instanceof CheckingAccount)){
            throw new RuntimeException("Tài khoản nhận hoặc tài khoản nguồn không phải tài khoản thanh toán!");
        }
        else if(accountSource.getBalance() < amount){
            throw new RuntimeException("Số dư không đủ mày nghèo quá!");
        }
        else {
            //trừ tiền
            accountSource.withdraw(amount);
            //cộng tiền
            accountTarget.deposit(amount);
            //tạo giao dichj
            Transaction transaction = new Transaction(TransactionType.TRANSFER, amount, LocalDateTime.now(), idAccountTarget, idAccontSource, description);
            //lưu giao dịch
            transactionDao.addTransactionPlus(transaction);
            transactionDao.addTransactionMinus(transaction);
            //update tài khoản
            accountDao.updateBalance(idAccontSource, accountSource.getBalance());
            accountDao.updateBalance(idAccountTarget, accountTarget.getBalance());
        }
    }
    public List<Transaction> getTransactionHistory(int customerId) {
        return transactionDao.getTransactionsByCustomer(customerId);
    }
}
