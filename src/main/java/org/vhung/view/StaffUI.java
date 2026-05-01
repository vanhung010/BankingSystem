package org.vhung.view;

import org.vhung.System.BankSystem;
import org.vhung.dao.AccountDao;
import org.vhung.dao.LoanDao;
import org.vhung.dao.SystemDao;
import org.vhung.dao.UserDao;
import org.vhung.enity.Account;
import org.vhung.enity.Customer;
import org.vhung.enity.LoanRequest;
import org.vhung.enity.Staff;
import org.vhung.service.AuthService;
import org.vhung.service.InterestService;
import org.vhung.service.LoanService;
import org.vhung.service.SavingService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class StaffUI {
    private Scanner scanner;
    private AuthService authService;
    private final Staff staff;
    private final LoanService loanService;
    private final LoanDao loanDao;
    private CustomerView customerUI;
    private final AccountDao accountDao;
    private final UserDao userDao;
    private final SystemDao systemDao;
    private final InterestService interestService;
    private final SavingService savingService;

    public StaffUI(Staff staff) {
        scanner = new Scanner(System.in);
        authService = new AuthService();
        this.staff = staff;
        loanService = new LoanService();
        this.loanDao = new LoanDao();
        this.userDao = new UserDao();
        this.accountDao = new AccountDao();
        this.systemDao = new SystemDao();
        this.interestService = new InterestService();
        this.savingService = new SavingService();

    }
    public void run(){
        while(true){
            LocalDate currentDate = systemDao.getTimeSystem();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedDate = currentDate.format(formatter);
            System.out.println("===================================================");
            System.out.printf("Xin chào, %s | Role: STAFF %n", staff.getUserName());
            System.out.printf("Ngày hệ thống: %s %n", formattedDate);
            System.out.println("===================================================");
            System.out.println("--- QUẢN LÝ KHÁCH HÀNG & TÀI KHOẢN ---");
            System.out.println("1. Tìm kiếm thông tin Khách hàng");
            System.out.println("2. Thay đổi trạng thái tài khoản (Khóa/Mở/Đóng)");
            System.out.println("--- NGHIỆP VỤ TÍN DỤNG ---");
            System.out.println("3. Xem danh sách Yêu cầu vay chờ duyệt (Pending)");
            System.out.println("4. Thẩm định & Quyết định giải ngân khoản vay");
            System.out.println("--- QUẢN TRỊ HỆ THỐNG ---");
            System.out.println("5. Xem cấu hình & Lãi suất ngân hàng");
            System.out.println("6. Cập nhật cấu hình hệ thống (Bank Settings)");
            System.out.println("--- QUẢN LÍ THỜI GIAN HỆ THỐNG ---");
            System.out.println("7. Tăng 1 tháng thời gian hệ thống");
            System.out.println("8. Tăng ngày thời gian hệ thống");
            System.out.println("9. Giảm thời gian hệ thống");
            System.out.println("--- KIỂM SOÁT GIAO DỊCH ---");
            System.out.println("10. Tra cứu lịch sử giao dịch toàn hệ thống");
            System.out.println("0. Đăng xuất");
            System.out.println("---------------------------------------------------");
            System.out.print("Nhập lựa chọn của bạn: ");
            String choice = scanner.nextLine();

            switch (choice){
                case "1":
                    showInformationCutomer();
                    break;
                case "2":
                    changeAccountStatus();
                    break;
                case "3":
                    handleCheckAllLoanRequestPending();
                    break;
                case "4":
                    handleLoanRequest();
                    break;
                case "5":
                    displayBankSettings();
                    break;
                case "6":
                    handleUpdateBankSettings();
                    break;
                case "7":
                    handleUpdateTime();
                    break;

                case "8":
                    handlePlusDaySystem();
                    break;
                case "9":
                    handleMinusDaySystem();
                    break;
                case "0":
                    System.out.println("Đã đăng xuất!");
                    return;
                default:
                    System.out.println("Lựa chọn không hợp lệ!");
            }
        }
    }

    public void handleUpdateBankSettings() {
        while (true) {
            System.out.println("\n=======================================================");
            System.out.println("             CẬP NHẬT CẤU HÌNH NGÂN HÀNG               ");
            System.out.println("=======================================================");
            System.out.println("1. Cập nhật số tiền khởi tạo tối thiểu (Checking)");
            System.out.println("2. Cập nhật số tiền gửi tiết kiệm tối thiểu");
            System.out.println("3. Cập nhật lãi suất vay cơ sở");
            System.out.println("4. Cập nhật lãi suất tiền gửi không kỳ hạn");
            System.out.println("5. Cập nhật lãi suất tiết kiệm 1 Tháng");
            System.out.println("6. Cập nhật lãi suất tiết kiệm 6 Tháng");
            System.out.println("7. Cập nhật lãi suất tiết kiệm 12 Tháng");
            System.out.println("0. Kết thúc và dời đi");
            System.out.println("-------------------------------------------------------");
            System.out.print("Nhập lựa chọn của bạn: ");
            String choice = scanner.nextLine();

            if (choice.equals("0")) {
                System.out.println("Đã kết thúc quá trình cập nhật cấu hình.");
                break;
            }

            System.out.print("Nhập giá trị mới cần cập nhật (Nhập số thập phân, vd: 0.05 cho 5% hoặc 50000 cho VNĐ): ");
            String valueStr = scanner.nextLine();
            double value = 0;
            try {
                value = Double.parseDouble(valueStr);
            } catch (NumberFormatException e) {
                System.out.println("❌ Lỗi: Giá trị nhập vào không hợp lệ, vui lòng nhập số!");
                continue;
            }

            boolean isSuccess = false;
            switch (choice) {
                case "1":
                    isSuccess = systemDao.updateConfigValue("min_checking_balance", value);
                    break;
                case "2":
                    isSuccess = systemDao.updateConfigValue("min_saving_deposit", value);
                    break;
                case "3":
                    isSuccess = systemDao.updateConfigValue("base_loan_interest_rate", value);
                    break;
                case "4":
                    isSuccess = systemDao.updateConfigValue("demand_interest_rate", value);
                    break;
                case "5":
                    isSuccess = systemDao.updateConfigValue("interest_rate_1M", value);
                    break;
                case "6":
                    isSuccess = systemDao.updateConfigValue("interest_rate_6M", value);
                    break;
                case "7":
                    isSuccess = systemDao.updateConfigValue("interest_rate_12M", value);
                    break;
                default:
                    System.out.println("❌ Lựa chọn không hợp lệ!");
                    continue; // Bỏ qua cập nhật nếu chọn sai
            }

            if (isSuccess) {
                System.out.println("✅ Cập nhật thành công!");
                // Cập nhật lại đối tượng Singleton trên RAM để đồng bộ với DB
                displayBankSettings();
            } else {
                System.out.println("❌ Cập nhật thất bại, vui lòng kiểm tra lại hệ thống.");
            }
        }
    }

        public void displayBankSettings() {
            // Lấy dữ liệu cấu hình thông qua SystemDao (Sử dụng hàm bạn mới viết ở bước trước)
            BankSystem settings = systemDao.getBankSystemConfig();

            if (settings == null) {
                System.out.println("❌ Lỗi: Không thể tải thông số cấu hình ngân hàng.");
                return;
            }

            System.out.println("\n=======================================================");
            System.out.println("              THÔNG SỐ CẤU HÌNH NGÂN HÀNG              ");
            System.out.println("=======================================================");

            // Hiển thị ngày hệ thống
            System.out.printf("%-35s: %s\n", "Ngày hệ thống hiện tại",
                    settings.getSystemDate() != null ? settings.getSystemDate().toString() : "N/A");

            System.out.println("-------------------------------------------------------");
            // Hiển thị số dư
            System.out.printf("%-35s: %,.0f VNĐ\n", "Số tiền khởi tạo tối thiểu (Checking)", settings.getMinCheckingBalance());
            System.out.printf("%-35s: %,.0f VNĐ\n", "Số tiền gửi tiết kiệm tối thiểu", settings.getMinSavingDeposit());

            System.out.println("-------------------------------------------------------");
            // Hiển thị Lãi suất chung
            System.out.printf("%-35s: %.2f%%/năm\n", "Lãi suất vay cơ sở", settings.getBaseLoanInterestRate() * 100);
            System.out.printf("%-35s: %.2f%%/năm\n", "Lãi suất tiền gửi không kỳ hạn", settings.getDemandInterestRate() * 100);

            System.out.println("-------------------------------------------------------");
            // Hiển thị Lãi suất các kỳ hạn
            System.out.printf("%-35s: %.2f%%/năm\n", "Lãi suất tiết kiệm kỳ hạn 1 Tháng", settings.getInterestRate1M() * 100);
            System.out.printf("%-35s: %.2f%%/năm\n", "Lãi suất tiết kiệm kỳ hạn 6 Tháng", settings.getInterestRate6M() * 100);
            System.out.printf("%-35s: %.2f%%/năm\n", "Lãi suất tiết kiệm kỳ hạn 12 Tháng", settings.getInterestRate12M() * 100);

            System.out.println("=======================================================\n");
        }


    public void changeAccountStatus(){
        System.out.println("Nhập id tài khoản muốn thay đổi trạng thái");
        String idAccountString = scanner.nextLine();
        System.out.println("Chọn trạng thái muốn thay đổi");
        System.out.println("1. Mở tài khoản");
        System.out.println("2. Khóa tài khoản");
        System.out.println("3. Đóng tài khoản");
       String choice = scanner.nextLine();
       int idAccount = 0;
       try{
           idAccount = Integer.parseInt(idAccountString);
       }
       catch (NumberFormatException e){
           System.out.println("Vui lòng nhập số");
       }

       Account account =accountDao.getAccountById(idAccount);
       if(choice.equals("1")){
           accountDao.updateStatusAccount(account, "ACTIVE");
       }
       else if(choice.equals("2")){
           accountDao.updateStatusAccount(account, "LOCKED");
       }
       else if(choice.equals("3")){
           accountDao.updateStatusAccount(account, "CLOSED");
       }


    }

    public void showInformationCutomer(){
        System.out.println("Nhập id khách hàng cần tra cứu");
        String idCustomerString = scanner.nextLine();

        int idCustomer = 0;
        try{
            idCustomer = Integer.parseInt(idCustomerString);
        }
        catch (NumberFormatException e){
            e.printStackTrace();
        }
        Customer customer = userDao.getCustomerById(idCustomer);
        if (customer == null) {
            System.out.println("❌ Lỗi: Không có thông tin khách hàng để hiển thị.");
            return;
        }

        System.out.println("\n=======================================================");
        System.out.println("                 THÔNG TIN KHÁCH HÀNG                  ");
        System.out.println("=======================================================");

        // Sử dụng định dạng %-20s để căn trái nhãn tên với độ rộng 20 ký tự
        // Lưu ý: Đổi tên các hàm get() dưới đây cho khớp với thuộc tính trong class Customer của bạn
        System.out.printf("%-20s: %s\n", "Mã Khách Hàng", customer.getUserId());
        System.out.printf("%-20s: %s\n", "Họ và Tên", customer.getFullName());

        // Các thông tin cá nhân cơ bản

        System.out.printf("%-20s: %s\n", "Email", customer.getEmail());
        System.out.printf("%-20s: %s\n", "Thu nhập", customer.getMonthlyIncome());

        System.out.println("=======================================================\n");

    }

    public void handleCheckAllLoanRequestPending(){

        try{
            List<LoanRequest> loanRequestList = loanService.getALlLoanRequestPending();
            System.out.println("\n===============================================================================");
            System.out.println("                 DANH SÁCH YÊU CẦU VAY CHỜ DUYỆT (PENDING)");
            System.out.println("===============================================================================");
            System.out.printf("%-5s | %-10s | %-18s | %-12s | %-20s\n",
                    "ID", "Mã Khách", "Số tiền vay (VNĐ)", "Kỳ hạn (Tháng)", "Ngày gửi yêu cầu");
            System.out.println("-------------------------------------------------------------------------------");
            for (LoanRequest req : loanRequestList) {
                System.out.printf("%-5d | %-10d | %,18.0f | %-14d | %-20s\n",
//                        req.getRequestId(),
                        req.getLoanRequestId(),
                        req.getCustomerOwner().getUserId(), // Hoặc getCustomer().getUserId() tùy cách bạn map dữ liệu
                        req.getRequestAmount(),
                        req.getLoanTerm(),
                        req.getRequestDate().toString()); // Chuyển ngày tháng sang chuỗi
            }
            System.out.println("===============================================================================");
        }
        catch(RuntimeException e){
            System.out.println(e.getMessage());
            return;
        }

        }

    public void handleLoanRequest(){
        handleCheckAllLoanRequestPending(); //show danh sách những khoản vay đang chờ xét duyệt
        System.out.println("Nhập id của khoản vay muốn xử lí");
        String idLoanRequestString = scanner.nextLine();
        int idLoanRequest = -1;
        try {
            idLoanRequest = Integer.parseInt(idLoanRequestString);
        }
        catch (NumberFormatException e){
            e.printStackTrace();
            return;
        }
        LoanRequest loanRequest = loanDao.getLoanRequestById(idLoanRequest);

        if(loanRequest == null){
            System.out.println("Không tìm thấy khoản vay");
            return;
        }

        System.out.println("1. Phê duyệt.");
        System.out.println("2. Từ chối.");
        String choice = scanner.nextLine();
        if(choice.equals("1")){
            customerUI = new CustomerView(userDao.getCustomerById(loanRequest.getCustomerOwner().getUserId()));
            customerUI.checkAllAccount(loanRequest.getCustomerOwner().getUserId());
            System.out.println("Nhập id tài khoản thanh toán nhận tiền");
            String idAccountString = scanner.nextLine();
            int idAccount = -1;
            try{
                idAccount = Integer.parseInt(idAccountString);
            }
            catch (NumberFormatException e){
                e.printStackTrace();
                return;
            }

           try{
               loanService.approvedLoanRequest(loanRequest, idAccount);
           }
           catch (RuntimeException e){
               System.out.println(e.getMessage());
               return;
           }
            System.out.println("Phê duyệt khoản vay thành công");

        }
        else if(choice.equals("2")){
            loanService.rejectLoanRequest(loanRequest);
            System.out.println("Từ chối khoản vay thành công");
        }
        else {
            System.out.println("Vui lòng chọn đúng lựa chọn");
            return;
        }
    }

    public void handleUpdateTime(){

        //cập nhật thời gian hệ thống lên 1 tháng
        systemDao.updateDateSystemPlus1Month();
        //cập nhật bên tiết kiệm
        savingService.checkSavingAccountExpried();
        //kiểm tra xem tháng trước có tài khoản vay nào chưa trả đủ không, nếu chưa trả đủ thì khóa tài khoản
        loanService.lockLoanAccountMonthly();
        //Hiển thị thông báo cho hệ thống
        loanService.checkLockLoanAccount();
        //cộng tiền lãi vào số nợ phải trả
        interestService.autoUpdateInterestLoanMonthly();
        //cập nhật số nợ phải trả mỗi tháng
            loanService.autoUpdateMonthlyRequỉedPayment();
       //cập nhật số nợ đã trả mỗi tháng về 0
       loanService.updateAmountPaidMonthly();
       //cập nhajat ngày trả nợ tất cả tài khoản
        loanService.updateDatePaidMonthly();
        //cập nhất số nợ phải trả
        loanService.updateAmountMustPaidMonthly();
    }
    //xử lí cộng ngày
    public void handlePlusDaySystem(){
        System.out.println("Nhập số ngày muốn cộng: ");
        String dayString = scanner.nextLine();
        int day =0;
        try{
            day = Integer.parseInt(dayString);
        }
        catch (NumberFormatException e){
            e.printStackTrace();
        }
        try{
            if(day + systemDao.getTimeSystem().getDayOfMonth() > 30){
                throw new RuntimeException("Vui lòng chọn chức năng cộng tháng để sang tháng mới!");
            }
            else {
                systemDao.plusDaySystem(day);
                savingService.checkSavingAccountExpried();
            }
        }
        catch (RuntimeException e){
            System.out.println(e.getMessage());
        }
    }
    //xử lí trừ ngày
    public void handleMinusDaySystem(){
        System.out.println("Nhập số ngày muốn trừ: ");
        String dayString = scanner.nextLine();
        int day =0;
        try{
            day = Integer.parseInt(dayString);
        }
        catch (NumberFormatException e){
            e.printStackTrace();
        }
        try{
            if(systemDao.getTimeSystem().getDayOfMonth() - day < 0){
                throw new RuntimeException("Không thể thực hiện, vui lòng chọn số ngày nhỏ hơn");
            }
            else {
                systemDao.minusDaySystem(day);
            }
        }
        catch (RuntimeException e){
            System.out.println(e.getMessage());
        }
    }
    }

