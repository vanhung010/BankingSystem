package org.vhung.view;

import org.vhung.controller.CustomerController;
import org.vhung.dao.AccountDao;
import org.vhung.dao.SystemDao;
import org.vhung.enity.*;
import org.vhung.service.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;



public class CustomerView {
    private Scanner scanner;
    private Customer customer;

    private CustomerController customerController = new CustomerController();

    public CustomerView(Customer customer) {
        scanner = new Scanner(System.in);

        this.customer = customer;

    }

    public void run() {
        while (true) {
            LocalDate currentDate = customerController.getDateSystem();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedDate = currentDate.format(formatter);

            System.out.println("===================================================");
            System.out.printf("Xin chào, %s | Role: CUSTOMER %n", customer.getUserName());
            System.out.printf("Ngày hệ thống: %s %n", formattedDate);
            System.out.println("===================================================");
            System.out.println("--- QUẢN LÝ TÀI KHOẢN ---");
            System.out.println("1. Xem thông tin tài khoản & Số dư");
            System.out.println("2. Mở tài khoản Giao dịch (Checkings)");
            System.out.println("3. Mở tài khoản Tiết kiệm (Savings)");
            System.out.println("4. Yêu cầu tạo khoản Vay (Loan)");
            System.out.println("--- GIAO DỊCH ---");
            System.out.println("5. Nạp tiền (Deposit)");
            System.out.println("6. Rút tiền (Withdraw)");
            System.out.println("7. Chuyển khoản (Transfer)");
            System.out.println("8. Thanh toán nợ khoản vay");
            System.out.println("9. Tất toán sổ tiết kiệm");
            System.out.println("10. Xem lịch sử giao dịch (Transaction History)");
            System.out.println("11. Tra cứu chi tiết thông tin tài khoản");
            System.out.println("0. Đăng xuất");
            System.out.println("---------------------------------------------------");
            System.out.println("Nhập lựa chọn của bạn: ");
            String choice = scanner.nextLine();
            switch (choice){
                case "1":
                    checkAllAccount(customer.getUserId());
                    break;
                case "2":
                    handleOpenCheckingAccount();
                    break;
                case "3":
                    handleOpenSavingAccount();
                    break;
                case "4":
                    handleLoanRequest();
                    break;
                case "5":
                    hanldeDeposite();
                    break;
                case "6":
                    handleWithdraw();
                    break;
                case "7":
                    handleTransfer();
                    break;
                case "8":
                    handlePaymentLoan();
                    break;
                case "9":
                    handClosedSavingAccount();
                    break;
                case "10":
                    handleViewTransactionHistory();
                    break;
                case "11":
                    handleViewAccountDetails();
                    break;
                case "0":
                    System.out.println("Đã đăng xuất!");
                    return;
                default:
                    System.out.println("Lựa chọn không hợp lệ!");
            }
        }
    }

    public void handleViewAccountDetails() {

        checkAllAccount(customer.getUserId());

        System.out.print("Nhập ID tài khoản muốn tra cứu chi tiết: ");
        String input = scanner.nextLine();


        // 2. Lấy thông tin tài khoản qua DB
        Account account = customerController.getAccountById(input);

        // Validate: Đảm bảo tài khoản tồn tại và thuộc về khách hàng đang đăng nhập
        if (account == null || account.getOwner().getUserId() != customer.getUserId()) {
            System.out.println("❌ Không tìm thấy tài khoản hoặc bạn không có quyền truy cập!");
            return;
        }

        System.out.println("\n================ CHI TIẾT TÀI KHOẢN ================");
        System.out.printf("ID Tài khoản   : %d\n", account.getAccountId());
        System.out.printf("Chủ tài khoản  : %s\n", account.getOwner().getFullName());
        System.out.printf("Số dư          : %.2f VNĐ\n", account.getBalance());
        System.out.printf("Ngày tạo       : %s\n", account.getCreatedAt());
        System.out.printf("Trạng thái     : %s\n", account.getAccountStatus());

        // 3. Hiển thị chi tiết theo từng loại tài khoản xác định
        if (account instanceof CheckingAccount) {
            System.out.println("Loại tài khoản : Thanh toán (Checking)");
            System.out.println("Mô tả          : Tài khoản dùng để giao dịch, rút, nạp và chuyển tiền linh hoạt.");

        } else if (account instanceof SavingAccount) {
            SavingAccount savingAcc = (SavingAccount) account;
            System.out.println("Loại tài khoản : Tiết kiệm (Saving)");
            System.out.printf("Kỳ hạn         : %d tháng\n", savingAcc.getTerm());
            System.out.printf("Lãi suất       : %.2f%%\n", savingAcc.getInterest());
            System.out.printf("Ngày gửi       : %s\n", savingAcc.getDepositDate());
            System.out.printf("Ngày đáo hạn   : %s\n", savingAcc.getMaturityDate());
            System.out.printf("Tiền lãi dự tính: %.2f VNĐ\n", savingAcc.calcInterestAmount());

        } else if (account instanceof LoanAccount) {
            LoanAccount loanAcc = (LoanAccount) account;
            System.out.println("Loại tài khoản : Khoản vay (Loan)");
            System.out.printf("Dư nợ gốc      : %.2f VNĐ\n", loanAcc.getPricipalAmount());
            System.out.printf("Lãi suất vay   : %.2f%%\n", loanAcc.getInterestRate());
            System.out.printf("Kỳ hạn vay     : %d tháng\n", loanAcc.getLoanTerm());
            System.out.printf("Số tiền trả/tháng: %.2f VNĐ\n", loanAcc.getMonthlyRequiredPayment());
            System.out.printf("Tiền đã trả    : %.2f VNĐ\n", loanAcc.getAmountPaidThisMonth());
            System.out.printf("Ngày trả tiếp  : %s\n", loanAcc.getNextPaymentDate());
        } else {
            System.out.println("Loại tài khoản : Không xác định (Unknown)");
        }
        System.out.println("====================================================");

    }


    public void handleViewTransactionHistory() {
        checkAllAccount(customer.getUserId());
        System.out.println("Nhập id tài khoản muốn xem giao dịch");
        String idAccountString = scanner.nextLine();


        System.out.println("\n===================================================================================================");
        System.out.println("                                LỊCH SỬ GIAO DỊCH CỦA BẠN                                          ");
        System.out.println("===================================================================================================");

        List<Transaction> history = customerController.getTransactionHistory(idAccountString);

        if (history == null || history.isEmpty()) {
            System.out.println("Bạn chưa có giao dịch nào trên hệ thống.");
            System.out.println("===================================================================================================\n");
            return;
        }

        // In tiêu đề bảng
        System.out.printf("%-10s | %-16s | %-12s | %-20s | %-20s\n",
                "ID Giao Dịch", "Loại Giao Dịch", "Tài Khoản", "Số Tiền (VNĐ)", "Thời Gian");
        System.out.println("---------------------------------------------------------------------------------------------------");

        // Format thời gian hiển thị
        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        for (Transaction t : history) {
            // Định dạng chuỗi tiền tệ (Nếu là trừ tiền do DB lưu số âm thì sẽ hiện kèm dấu "-")
            String amountStr = String.format("%,.0f", t.getAmount());
            // Có thể hiện dấu "+" nếu số tiền lớn hơn 0 cho rõ ràng
            if (t.getAmount() > 0) {
                amountStr = "+" + amountStr;
            }

            System.out.printf("%-12d | %-16s | %-12d | %-20s | %-20s\n",
                    t.getTransactionId(),
                    t.getTransactionType(),
                    t.getPlusAccountId(),
                    amountStr,
                    t.getTimestamp() != null ? t.getTimestamp().format(dtFormatter) : "N/A"
            );

            // In phần mô tả thụt vào một tí cho đẹp mắt
            System.out.printf("   └─ Nội dung: %s\n", t.getDescription());
            System.out.println("---------------------------------------------------------------------------------------------------");
        }
        System.out.println("===================================================================================================\n");
    }
    public void handClosedSavingAccount(){
        showallSavingAccountActiveOfCustomer();

        System.out.println("Nhập id tài khoản tiết kiệm muốn tất toán");
        String idAccountSavingString = scanner.nextLine();
        displayActiveCheckingAccounts();
        System.out.println("Nhập id tài khoản tiết kiệm nhận tiền");
        String idAccountCheckingString = scanner.nextLine();

        String mess = customerController.closedSavingAccount(idAccountCheckingString, idAccountSavingString);
        System.out.println(mess);
    }



    public void showallSavingAccountActiveOfCustomer(){
        List<SavingAccount> savingAccountList = customerController.getAllAccountSavingOfCustomer(customer.getUserId());
        System.out.println("\n=========================================================================================================");
        System.out.println("                                DANH SÁCH TÀI KHOẢN TIẾT KIỆM ĐANG HOẠT ĐỘNG                             ");
        System.out.println("=========================================================================================================");

        // Căn lề các cột: %-10s (chuỗi căn trái 10 ký tự), %15s (chuỗi căn phải 15 ký tự)
        System.out.printf("%-12s | %-15s | %-20s | %-12s | %-10s | %-12s | %-12s\n",
                "ID Tài Khoản", "Mã Khách Hàng", "Số Dư (VNĐ)", "Kỳ Hạn (Tháng)", "Lãi Suất", "Ngày Gửi", "Ngày Đáo Hạn");
        System.out.println("---------------------------------------------------------------------------------------------------------");

        if (savingAccountList == null || savingAccountList.isEmpty()) {
            System.out.println("                            Không có tài khoản tiết kiệm nào đang hoạt động.                             ");
        } else {
            for (SavingAccount acc : savingAccountList) {
                // %.2f: định dạng số thập phân có 2 chữ số sau dấu phẩy
                // %.4f: định dạng lãi suất có 4 chữ số thập phân
                System.out.printf("%-12d | %-15s | %-20.2f | %-14d | %-10.4f | %-12s | %-12s\n",
                        acc.getAccountId(),
                        acc.getOwner().getUserId(), // Giả sử lấy ID của khách hàng sở hữu
                        acc.getBalance(),
                        acc.getTerm(),
                        acc.getInterest(), // Lấy lãi suất của sổ
                        acc.getDepositDate().toString(),
                        acc.getMaturityDate().toString()
                );
            }
        }
        System.out.println("=========================================================================================================\n");
    }

    public void handleOpenSavingAccount(){

        System.out.println("Nhập số tiền muốn gửi tiết kiệm(tối thiểu 1 triệu)");
        String amountString = scanner.nextLine();
        System.out.println("Nhập kì hạn (1-6-12)");
        String termString = scanner.nextLine();
        displayActiveCheckingAccounts();
        System.out.println("Nhập id tài khoản thanh toán trừ tiền");
        String idAccountCheckingString = scanner.nextLine();

        if(!termString.equals("1")  && !termString.equals("6")  && !termString.equals("12")){
            System.out.println("Vui lòng chọn đúng kì hạn (1-6-12)");
            return;
        }

        String message = customerController.openSavingAccount(idAccountCheckingString, amountString, termString);

        System.out.println(message);

    }

    public void handleOpenCheckingAccount(){
        System.out.println("Nhập số dư khi mới tạo tài khoản");
        String balanceString = scanner.nextLine();


        int ownerId = customer.getUserId();

        String message = customerController.openCheckingAccount(ownerId, balanceString);

        System.out.println(message);
    }
    public void checkAllAccount(int idCustomer){
        System.out.println("\n=========================================================");
        System.out.println("               THÔNG TIN CÁ NHÂN & SỐ DƯ");
        System.out.println("=========================================================");
        System.out.println("👤 Khách hàng: " + customer.getFullName());
        System.out.println("📧 Email     : " + customer.getEmail());
        System.out.println("Thu nhập     : "+customer.getMonthlyIncome());

        System.out.println("---------------------------------------------------------");
        System.out.println("💳 DANH SÁCH TÀI KHOẢN:");


        List<Account> accounts = customerController.getAllAccountOfCustomer(customer);



        if (accounts == null || accounts.isEmpty()) {
            System.out.println("❌ Bạn chưa mở tài khoản nào tại hệ thống HKL Bank.");
        } else {

            System.out.printf("%-10s | %-15s | %-15s | %-10s\n",
                    "ID", "Loại tài khoản", "Số dư (VNĐ)", "Trạng thái");
            System.out.println("---------------------------------------------------------");

            // Duyệt qua từng tài khoản và in ra
            for (Account acc : accounts) {
                String accountType = getAccountTypeName(acc);

                // %-10d: In số nguyên ID
                // %15.2f: In số thập phân, căn phải (không có dấu -), lấy 2 số sau dấu phẩy
                System.out.printf("%-10d | %-15s | %15.2f | %-10s\n",
                        acc.getAccountId(),
                        accountType,
                        acc.getBalance(),
                        acc.getAccountStatus());
            }
        }
        System.out.println("=========================================================");
    }
    private String getAccountTypeName(Account account) {
        if (account instanceof CheckingAccount) return "Thanh toán";
        if (account instanceof SavingAccount) return "Tiết kiệm";
        if (account instanceof LoanAccount) return "Khoản vay";
        return "Chưa xác định";
    }
    private void hanldeDeposite() {
        checkAllAccount(customer.getUserId());

        System.out.println("Nhập id tài khoản thanh toán muốn nạp tiền.");
        String idAccountString = scanner.nextLine();
        System.out.println("Nhập số tiền muốn nạp");
        String amountString = scanner.nextLine();

        String mess = customerController.deposite(customer, idAccountString, amountString);

        System.out.println(mess);

    }

    private void handleWithdraw(){
        checkAllAccount(customer.getUserId());

        System.out.println("Nhập id tài khoản thanh toán muốn rút tiền.");
        String idAccountString = scanner.nextLine();
        Integer idAccountInt = -1;
        System.out.println("Nhập số tiền muốn rút");
        String amountString = scanner.nextLine();
        double amountDouble = -1;

        String mess = customerController.withdraw(customer, idAccountString, amountString);

        System.out.println(mess);
    }


    //xử lí chuyển khoản
    private void handleTransfer(){
        checkAllAccount(customer.getUserId());

        System.out.println("Nhập id tài khoản thanh toán chuyển tiền");
        String idAccountSourceString = scanner.nextLine();
        System.out.println("Nhập id tài khoản thanh toán nhận tiền");
        String idAccountTargetString = scanner.nextLine();
        System.out.println("Nhập số tiền chuyển.");
        String amountString = scanner.nextLine();
        System.out.println("Nhập mô tả.");
        String description = scanner.nextLine();

        String mess = customerController.transfer(amountString, idAccountSourceString, idAccountTargetString, description);
        System.out.println(mess);



    }
    //xử lis yêu cầu tạo khoản vay
    public void handleLoanRequest() {
        System.out.println("Nhập số tiền muốn vay");
        String amountString = scanner.nextLine();
        System.out.println("Nhập kì hạn vay (1-6-12)");
        String termLoan = scanner.nextLine();


        // xử lí lỗi không đúng kì hạn vay
        if (!termLoan.equals("1") && !termLoan.equals("6") && !termLoan.equals("12")) {
            System.out.println("Lỗi: Kì hạn vay không đúng vui lòng nhập lại");
            return;
        }
        String mess = customerController.addLoanRequest(customer, amountString, termLoan);
        System.out.println(mess);
    }
    //xử lí yêu cầu thanh toán khoản vay
    public void handlePaymentLoan(){
        displayActiveLoanAccounts();
        System.out.println("Nhập id tài khoản vay");
        String idAcocuntLoanString = scanner.nextLine();
        displayActiveCheckingAccounts();
        System.out.println("Nhập id tài khoản thanh toán để thanh toán khoản vay");
        String idAccountCheckingString = scanner.nextLine();
        System.out.println("Nhập số tiền bạn muốn thanh toán");
        String amountString = scanner.nextLine();

        String mess = customerController.paymentLoan(idAcocuntLoanString, idAccountCheckingString, amountString);
        System.out.println(mess);
    }
    //show những tài khoản vay
    public void displayActiveLoanAccounts() {
        // Gọi service lấy danh sách
        List<LoanAccount> activeLoans = customerController.getActiveLoanAccountsByCustomer(customer);

        System.out.println("\n====================== DANH SÁCH KHOẢN VAY ĐANG HOẠT ĐỘNG ======================");

        if (activeLoans.isEmpty()) {
            System.out.println("Bạn không có khoản vay nào đang cần thanh toán.");
            System.out.println("================================================================================");
            return;
        }

        // In Tiêu đề bảng dùng printf để căn lề
        System.out.printf("%-5s | %-15s | %-15s | %-15s | %-15s\n",
                "ID", "Tổng dư nợ", "Chỉ tiêu tháng", "Đã trả tháng này", "Cần trả thêm");
        System.out.println("------------------------------------------------------------------------------------------");

        // Lặp qua danh sách và in từng dòng
        for (LoanAccount loan : activeLoans) {
            // Tính số tiền còn phải trả nốt trong tháng này để không bị khóa
            double remainingThisMonth = loan.getMonthlyRequiredPayment() - loan.getAmountPaidThisMonth();
            if (remainingThisMonth < 0) remainingThisMonth = 0; // Tránh hiển thị số âm nếu trả lố

            System.out.printf("%-5d | %-15.0f | %-15.0f | %-15.0f | %-15.0f\n",
                    loan.getAccountId(),
                    loan.getBalance(),                 // Dư nợ hiện tại
                    loan.getMonthlyRequiredPayment(),  // Chỉ tiêu tháng
                    loan.getAmountPaidThisMonth(),     // Đã trả tháng này
                    remainingThisMonth                // Còn phải trả nốt

            );
        }
        System.out.println("==========================================================================================");
    }
    //show những tài khoản thanh toán đang còn hoạt động
    public void displayActiveCheckingAccounts() {

        List<CheckingAccount> activeChecking = customerController.getActiveCheckingAccountsByCustomer(customer);

        System.out.println("\n================ TÀI KHOẢN THANH TOÁN (NGUỒN TIỀN) ================");

        if (activeChecking.isEmpty()) {
            System.out.println("Bạn không có tài khoản thanh toán nào đang hoạt động.");
            System.out.println("===================================================================");
            return;
        }

        // In Tiêu đề bảng
        System.out.printf("%-5s | %-18s\n",
                "ID", "Tổng số dư");
        System.out.println("-------------------------------------------------------------------");

        // Lặp qua danh sách và in từng dòng
        for (CheckingAccount chk : activeChecking) {


            System.out.printf("%-5d | %-18.0f\n",
                    chk.getAccountId(),
                    chk.getBalance()

            );
        }
        System.out.println("===================================================================");
    }
    public boolean validSaving(){
        System.out.println("Bạn đang tất toán trước hạn, có chắc chắn thực hiện thao tác?");
        System.out.println("1. Tiếp tục");
        System.out.println("2. Thoát");
        String choiceString = scanner.nextLine();
        int choice =0;
        try{
            choice = Integer.parseInt(choiceString);
        }
        catch (NumberFormatException e){
            e.printStackTrace();
        }
        if(choice ==1){
            return true;
        }
        else {
            return false;
        }
    }
}


