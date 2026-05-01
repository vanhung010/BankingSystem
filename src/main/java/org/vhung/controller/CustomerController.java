package org.vhung.controller;

import org.vhung.enity.*;
import org.vhung.service.*;
import org.vhung.util.ParseNumber;

import java.time.LocalDate;
import java.util.List;



public class CustomerController {
    private AccountService accountService = new AccountService();
    private LoanService loanService = new LoanService();
    private TransactionService transactionService = new TransactionService();
    private AuthService authService = new AuthService();
    private SavingService savingService = new SavingService();

    public List<Account> getAllAccountOfCustomer(Customer customer) {
        return accountService.getAllAccount(customer.getUserId());
    }

    public String openCheckingAccount(int ownerId, String balance) {
        try {
            double balanceDouble = ParseNumber.parseDouble(balance);
            accountService.openCheckingAccount(ownerId, balanceDouble);
            return "Mở tài khoản thanh toán thành công!";
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }

    public String openSavingAccount(String idAccountCheckingString, String amountString, String termString) {
        String mess = "Mở tài khoản tiết kiệm thành công";
        try {
            int idAccount = ParseNumber.parseint(idAccountCheckingString);
            double amount = ParseNumber.parseDouble(amountString);
            int term = ParseNumber.parseint(termString);

            Account account = accountService.getAccountById(idAccount);

            if (!(account instanceof CheckingAccount)) {
                mess = "Tài khoản đã chọn không phải tài khoản thanh toán";
                return mess;
            }
            CheckingAccount checkingAccount = (CheckingAccount) account;
            accountService.openSavingAccount(checkingAccount, amount, term);
        } catch (RuntimeException e) {
            mess = e.getMessage();
        }
        return mess;
    }

    public String addLoanRequest(Customer customer, String amountString, String termString) {
        String mess = "Thông báo: Yêu cầu tạo khoản vay thành công! Vui lòng chờ nhân viên giải quyết!";
        double amount = 0;
        int term = 0;
        try {
            amount = ParseNumber.parseDouble(amountString);
            term = ParseNumber.parseint(termString);
        } catch (RuntimeException e) {
            mess = e.getMessage();
            return mess;
        }

        try {
            loanService.addLoanRequest(customer, amount, term);
        } catch (RuntimeException e) {
            mess = e.getMessage();
            return mess;
        }
        return mess;
    }

    public String deposite(Customer customer, String accountIdString, String amountString) {
        String mess = "Thông báo: Nạp tiền thành công!";
        int accountId = 0;
        double amount = 0;
        try {
            accountId = ParseNumber.parseint(accountIdString);
            amount = ParseNumber.parseDouble(amountString);
        } catch (RuntimeException e) {
            return e.getMessage();
        }
        try {
            transactionService.DepositCheckingAccount(customer.getUserId(), accountId, amount);
        } catch (RuntimeException e) {
            return e.getMessage();
        }
        return mess;
    }

    public String withdraw(Customer customer, String accountIdString, String amountString) {
        String mess = "Thông báo: Rút tiền thành công!";
        int accountId = 0;
        double amount = 0;
        try {
            accountId = ParseNumber.parseint(accountIdString);
            amount = ParseNumber.parseDouble(amountString);
        } catch (RuntimeException e) {
            return e.getMessage();
        }
        try {
            transactionService.withdrawCheckingAccount(customer.getUserId(), accountId, amount);
        } catch (RuntimeException e) {
            return e.getMessage();
        }
        return mess;
    }

    public String transfer(String amountString, String idAccountSourceString, String idAccountTargetString, String description) {
        int idAccountSource = 0;
        int idAccountTarget = 0;
        double amount = 0;
        //đổi kiểu dữ liệu
        try {
            idAccountSource = ParseNumber.parseint(idAccountSourceString);
            idAccountTarget = ParseNumber.parseint(idAccountTargetString);
            amount = ParseNumber.parseDouble(amountString);
        } catch (RuntimeException e) {
            return e.getMessage();
        }
        try {
            transactionService.tranfer(amount, idAccountSource, idAccountTarget, description);
        } catch (RuntimeException e) {
            return e.getMessage();
        }
        return "Thông báo: Chuyển khoản thành công!";
    }

    public String paymentLoan(String idAccountLoanString, String idAccountCheckingString, String amountString) {
        LocalDate systemDate = authService.getSystemDate();
        int idAccountLoan = 0;
        int idAccountChecking = 0;
        double amount = 0;
        try {
            idAccountLoan = ParseNumber.parseint(idAccountLoanString);
            idAccountChecking = ParseNumber.parseint(idAccountCheckingString);
            amount = ParseNumber.parseDouble(amountString);
        } catch (RuntimeException e) {
            return e.getMessage();
        }
        try {
            Account accountChecking = accountService.getAccountById(idAccountChecking);
            Account accountLoan = accountService.getAccountById(idAccountLoan);

            if(!(accountChecking instanceof CheckingAccount) || !(accountLoan instanceof LoanAccount)){
                return "Lỗi: Vui lòng nhập đúng loại tài khoản!";
            }
            CheckingAccount checking = (CheckingAccount) accountChecking;
            LoanAccount loan = (LoanAccount) accountLoan;

            if(systemDate.isBefore(loan.getNextPaymentDate()) ){
                return "Lỗi: Chưa đến thời hạn thanh toán, vui lòng chờ tới ngày 16";
            }
            loanService.payInstallerLoan(loan, amount, checking);
        } catch (RuntimeException e) {
            return e.getMessage();
        }
        return "Thông báo: Thanh toán khoản vay thành công!";
    }

    public String closedSavingAccount(String idCheckingAccountString, String idSavingAccountString){
        int idCheckingAccount =0;
        int idSavingAccount =0;
        try{
            idCheckingAccount = ParseNumber.parseint(idCheckingAccountString);
            idSavingAccount = ParseNumber.parseint(idSavingAccountString);
        } catch (RuntimeException e) {
            return e.getMessage();
        }
        try {
            Account accountChecking = accountService.getAccountById(idCheckingAccount);
            Account accountSaving = accountService.getAccountById(idSavingAccount);

            if(!(accountSaving instanceof SavingAccount) || !(accountChecking instanceof CheckingAccount)){
                return  "Loại tài khoản không hợp lệ";
            }
            CheckingAccount checking = (CheckingAccount) accountChecking;
            SavingAccount saving = (SavingAccount) accountSaving;

            savingService.closeddSavingAccount(checking, saving);
        }
        catch (RuntimeException e){
            return e.getMessage();
        }

        return "Thông báo: Tất toán sổ thiết kiệm thành công";
    }

    public List<Transaction> getTransactionHistory(String idAccountString){
        int idAccount =0;
        try{
            idAccount = ParseNumber.parseint(idAccountString);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return null;
        }
        try {
            List<Transaction> transactionList = transactionService.getTransactionHistory(idAccount);
            return transactionList;
        }
        catch (RuntimeException e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Account getAccountById(String idAccontString){
        int idAccount = 0;
        try {
            idAccount = ParseNumber.parseint(idAccontString);
        }
        catch (RuntimeException e){
            System.out.println(e.getMessage());
            return null;
        }
        try {
            Account account = accountService.getAccountById(idAccount);
            return account;
        }
        catch (RuntimeException e){
            System.out.println(e.getMessage());
            return null;
        }

    }

    public LocalDate getDateSystem(){
        return authService.getSystemDate();
    }

    public List<SavingAccount> getAllAccountSavingOfCustomer(int idCustomerString){

        try{
            List<SavingAccount> savingAccountList = accountService.getAllAccountSavingOfCustomer(idCustomerString);
            return savingAccountList;
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public List<LoanAccount> getActiveLoanAccountsByCustomer(Customer customer) {
        try {
            return accountService.getActiveLoanAccountsByCustomer(customer);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return null; // Hoặc trả về new ArrayList<>() tùy logic
        }
    }

    public List<CheckingAccount> getActiveCheckingAccountsByCustomer(Customer customer) {
        try {
            return accountService.getActiveCheckingAccountsByCustomer(customer);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
