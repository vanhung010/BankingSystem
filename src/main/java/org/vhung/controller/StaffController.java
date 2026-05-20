package org.vhung.controller;

import org.vhung.System.BankSystem;
import org.vhung.dao.AccountDao;
import org.vhung.dao.SystemDao;
import org.vhung.dao.UserDao;
import org.vhung.enity.Account;
import org.vhung.enity.Customer;
import org.vhung.enity.LoanRequest;
import org.vhung.service.AccountService;
import org.vhung.service.LoanService;
import org.vhung.service.SavingService;
import org.vhung.service.InterestService;
import org.vhung.service.SystemService;
import org.vhung.util.ParseNumber;

import java.time.LocalDate;
import java.util.List;

public class StaffController {


    private SystemService systemService = new SystemService();
    private AccountService accountService = new AccountService();
    private LoanService loanService = new LoanService();
    private SavingService savingService = new SavingService();
    private InterestService interestService = new InterestService();

    public BankSystem getBankSystemConfig(){
        return systemService.getBankSystemConfig();
    }

    public Customer getCustomerById(String idString){
       try {
           int id = ParseNumber.parseint(idString);
           return accountService.getCustomerbyId(id);
       }
       catch (RuntimeException e){
           System.out.println(e.getMessage());
       }
       return null;
    }

    public String updateStatusAccount(String idAccountString, String status){
        try {
            int idAccount = ParseNumber.parseint(idAccountString);

            Account account = accountService.getAccountById(idAccount);

            accountService.updateStatusAccount(account, status);

            return "Thông báo: Cập nhật trạng thái tài khoản thành công";
        }

        catch (RuntimeException e){
            return  e.getMessage();
        }

    }

    public List<LoanRequest> getAllLoanRequestPending() throws RuntimeException {
        return loanService.getAllLoanRequestPending();
    }

    public LocalDate getSystemTime(){
        return systemService.getTimeSystem();
    }

    public String updateConfigValue(String thongSo, String valueString){
        try {
            systemService.updateConfigValue(thongSo, valueString);
            return "Cập nhật thông số thành công";
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }
    
    public LoanRequest getLoanRequestById(String idString) {
        try {
            int id = ParseNumber.parseint(idString);
            return loanService.getLoanRequestById(id);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void approveLoanRequest(LoanRequest loanRequest, String idAccountString) {
        try {
            int idAccount = ParseNumber.parseint(idAccountString);
            loanService.approvedLoanRequest(loanRequest, idAccount);
            System.out.println("Phê duyệt khoản vay thành công");
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void rejectLoanRequest(LoanRequest loanRequest) {
        loanService.rejectLoanRequest(loanRequest);
        System.out.println("Từ chối khoản vay thành công");
    }

    public void handleUpdateTime() {
        systemService.updateDateSystemPlus1Month();
        savingService.checkSavingAccountExpried();
        loanService.lockLoanAccountMonthly();
        loanService.checkLockLoanAccount();
        interestService.autoUpdateInterestLoanMonthly();
        loanService.autoUpdateMonthlyRequiredPayment();
        loanService.updateAmountPaidMonthly();
        loanService.updateDatePaidMonthly();
        loanService.updateAmountMustPaidMonthly();
    }
    
    public void handPlusDaySystem(String dayString) {
        try {
            int day = ParseNumber.parseint(dayString);
            if (day + systemService.getTimeSystem().getDayOfMonth() > 30) {
                System.out.println("Vui lòng chọn chức năng cộng tháng để sang tháng mới!");
            } else {
                systemService.plusDaySystem(day);
                savingService.checkSavingAccountExpried();
                System.out.println("Cộng ngày thành công");
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void handMinusDaySystem(String dayString) {
        try {
            int day = ParseNumber.parseint(dayString);
            if (systemService.getTimeSystem().getDayOfMonth() - day < 0) {
                System.out.println("Không thể thực hiện, vui lòng chọn số ngày nhỏ hơn");
            } else {
                systemService.minusDaySystem(day);
                System.out.println("Trừ ngày thành công");
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }
}
