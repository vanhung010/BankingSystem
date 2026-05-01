package org.vhung.service;

import org.vhung.dao.AccountDao;
import org.vhung.dao.LoanDao;
import org.vhung.dao.TransactionDao;
import org.vhung.enity.*;
import org.vhung.enity.enums.AccountStatus;
import org.vhung.enity.enums.TransactionType;

import javax.sound.midi.MidiFileFormat;
import java.time.LocalDateTime;
import java.util.List;

public class LoanService {

    private LoanDao loanDao;
    private AccountDao accountDao;
    private TransactionService transactionService;
    private TransactionDao transactionDao;

    public LoanService() {
        this.loanDao = new LoanDao();
        this.accountDao = new AccountDao();
        this.transactionService = new TransactionService();
        this.transactionDao = new TransactionDao();
    }
    //thêm yêu cầu vay
    public void addLoanRequest(Customer customerOwner, double requestAmount, int term){
        //kiểm tra khoản vay, Hệ số 10
        if(requestAmount > customerOwner.getMonthlyIncome() * 10){
            throw new RuntimeException("Lỗi thu nhập không đạt yêu cầu vay");
        }
        LoanRequest loanRequest = new LoanRequest();

        loanRequest.setCustomerOwner(customerOwner);
        loanRequest.setRequestAmount(requestAmount);
        loanRequest.setLoanTerm(term);

        loanDao.addLoanRequest(loanRequest);

    }
    //lấy danh sách tất cả tài khoản vay đang chờ phê duyệt
    public List<LoanRequest> getALlLoanRequestPending(){
        List<LoanRequest> loanRequestList = loanDao.getAllLoanRequestPending();

        if(loanRequestList == null || loanRequestList.size()==0){
            throw new RuntimeException("Hiện tại không có khoản vay nào đang chờ phê duyệt");
        }
        return loanRequestList;

    }
    //Đồng ý khoản vay
    public void approvedLoanRequest(LoanRequest loanRequest, int idAccountChecking){


        Account account = accountDao.getAccountById(idAccountChecking);
        List<Account> listAccountOfCustomer = accountDao.getAllAccountOfCustomerDao(loanRequest.getCustomerOwner().getUserId());
        if (account == null) {
            throw new RuntimeException("Không tìm thấy tài khoản thanh toán!");
        }

        else if (!(account instanceof CheckingAccount)) {
            throw new RuntimeException("tài khoản không phải tài khoản thanh toán!");
        } else if (!listAccountOfCustomer.contains(account)) {
            throw new RuntimeException("Tài khoản đã chọn không có trong danh sách tài khoản của khách hàng!");

        }
        //cập nhajat trạng thái
        loanDao.updateStatusLoanRequest(loanRequest.getLoanRequestId(), "APPROVED");
        //mở tài khoản
        accountDao.addLoanAccount(loanRequest.getCustomerOwner().getUserId(), loanRequest.getRequestAmount(), loanRequest.getLoanTerm());
        account.getAccountStatus().handle();
        //ép kiểu xuống
        CheckingAccount checkingAccount = (CheckingAccount) account;
        //thực hiện cộng tiền
        checkingAccount.deposit(loanRequest.getRequestAmount());
        //lưu giao dịch
        Transaction transaction = new Transaction(TransactionType.LOAN_DISBURSEMENT, loanRequest.getRequestAmount(), LocalDateTime.now(), idAccountChecking, null, "Nhận tiền từ tài khoản vay");
        transactionDao.addTransactionPlus(transaction);

        accountDao.updateBalance(account.getAccountId(), account.getBalance());
    }
    //Từ chối khoa vay
    public void rejectLoanRequest(LoanRequest loanRequest){
        loanDao.updateStatusLoanRequest(loanRequest.getLoanRequestId(), "REJECTED");
    }
    //tự động cập nhật tiền phải trả của vay mỗi tháng
    public void autoUpdateMonthlyRequỉedPayment(){
        List<LoanAccount> loanAccountList = accountDao.getAllLoanAccountActive();
        for(LoanAccount loanAccount : loanAccountList){
            //thực hiện cập nhật số tiền
            accountDao.updateMonlyRequiredPayment(loanAccount);
        }
    }
    //kiểm tra xem đã trả đủ hàng tháng chưa
    public void lockLoanAccountMonthly(){
        List<LoanAccount> loanAccountList = accountDao.getAllLoanAccountActive();
        for(LoanAccount loanAccount: loanAccountList){
            //Nếu chưa trả đủ
            if(!loanAccount.checkPaid()){
                accountDao.lockAccount(loanAccount);
            }
        }
    }
    //Thực hiện ném lỗi
    public void checkLockLoanAccount(){
        List<LoanAccount> loanAccountList = accountDao.getAllLoanAccountActive();
        for(LoanAccount loanAccount : loanAccountList){
            try{
                loanAccount.getAccountStatus().handle();
            } catch (RuntimeException e) {
                System.out.println("Tài khoản của bạn do không thanh toán đủ tháng trước nên đã bị khóa, vui lòng liên hệ nhân viên để thực hiện mở khóa");
                System.out.println(e.getMessage());
            }
        }
    }
    //cập nhật số tiền đã trả hàng tháng của mỗi tài khoản về 0
    public void updateAmountPaidMonthly(){
        List<LoanAccount> loanAccountList = accountDao.getAllLoanAccountActive();
        for(LoanAccount loanAccount : loanAccountList){
            //cập nhật về 0
            loanDao.updatePaidThisMonth(loanAccount);
        }
    }
    //cập nhật số tiền phải trả của mỗi tháng
    public void updateAmountMustPaidMonthly(){
        List<LoanAccount> loanAccountList = accountDao.getAllLoanAccountActive();
        for(LoanAccount loanAccount : loanAccountList){
            loanDao.updatePaidMonth(loanAccount);
        }

    }
    //cập nhật ngày trả nợ mỗi tháng
    public void updateDatePaidMonthly(){
        List<LoanAccount> loanAccountList = accountDao.getAllLoanAccountActive();
        for (LoanAccount loanAccount : loanAccountList){
            loanDao.updateNextPaymentDate(loanAccount);
        }
    }
    //thanh toans nợ khoản vay
    public void payInstallerLoan(LoanAccount loanAccount, double amount, CheckingAccount checkingAccount){
        //check balance
        if(checkingAccount.getBalance() < amount ){
            throw new RuntimeException("Tài khoanr thanh toán không đủ để thực hiện giao dịch");
        }
        //kiểm tra lỗi
        loanAccount.getAccountStatus().handle();

        double amountReal = amount;
        //trường hợp người dùng lỡ nhập số tiền trả nợ lố số nợ
        if(amount > loanAccount.getBalance()){
            amountReal = loanAccount.getBalance();
            System.out.println("Bạn đã thanh toán vượt quá số nợ, chúng tôi đã tự động điều chỉnh");
        }

        //thực hiêện trừ tiền tài khoản thanh toán
        checkingAccount.withdraw(amountReal);
       //thực hiện trừ tiền tài khoản nợ
        loanAccount.withdraw(amountReal);
        //cập nhật số nợ đã trả trong tháng
        loanDao.updateAmountPaidThisMonthAfterPay(loanAccount, amountReal);
        //nếu trả hết nợ thì đóng acc
        if(loanAccount.getBalance() <= 0){
          loanAccount.setAccountStatus(AccountStatus.CLOSED);
          accountDao.updateStatusAccount(loanAccount, "CLOSED");
        }
        //lưu giao dịch
        Transaction transactionCheck = new Transaction(TransactionType.LOAN_PAYMENT, amountReal, LocalDateTime.now(), null, checkingAccount.getAccountId(), "Thanh toán khoản vay");
        Transaction transactionLoan = new Transaction(TransactionType.LOAN_PAYMENT, amountReal, LocalDateTime.now(), null, loanAccount.getAccountId(), "Thanh toán khoản vay");
        //lưu
        transactionDao.addTransactionMinus(transactionCheck);
        transactionDao.addTransactionMinus(transactionLoan);
        //cập nhật tài khoản
        accountDao.updateBalance(loanAccount.getAccountId(), loanAccount.getBalance());
        accountDao.updateBalance(checkingAccount.getAccountId(), checkingAccount.getBalance());

    }


}
