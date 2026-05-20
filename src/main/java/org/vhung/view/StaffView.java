package org.vhung.view;

import org.vhung.System.BankSystem;
import org.vhung.controller.CustomerController;
import org.vhung.controller.StaffController;
import org.vhung.enity.Customer;
import org.vhung.enity.LoanRequest;
import org.vhung.enity.Staff;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class StaffView {
    private Scanner scanner;
    private final Staff staff;
    private CustomerView customerUI;
    private StaffController staffController;
    private CustomerController customerController;

    public StaffView(Staff staff) {
        scanner = new Scanner(System.in);
        this.staff = staff;
        this.staffController = new StaffController();
        this.customerController = new CustomerController();
    }
    public void run(){
        while(true){
            LocalDate currentDate = staffController.getSystemTime();
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


            boolean isSuccess = false;
            switch (choice) {
                case "1":
                    System.out.println(staffController.updateConfigValue("min_checking_balance", valueStr));
                    break;
                case "2":
                    System.out.println(staffController.updateConfigValue("min_saving_deposit", valueStr));
                    break;
                case "3":
                    System.out.println(staffController.updateConfigValue("base_loan_interest_rate", valueStr));
                    break;
                case "4":
                    System.out.println(staffController.updateConfigValue("demand_interest_rate", valueStr));
                    break;
                case "5":
                    System.out.println(staffController.updateConfigValue("interest_rate_1M", valueStr));
                    break;
                case "6":
                    System.out.println(staffController.updateConfigValue("interest_rate_6M", valueStr));
                    break;
                case "7":
                    System.out.println(staffController.updateConfigValue("interest_rate_12M", valueStr));
                    break;
                default:
                    System.out.println("❌ Lựa chọn không hợp lệ!");
                    continue; // Bỏ qua cập nhật nếu chọn sai
            }

            displayBankSettings();
        }
    }

        public void displayBankSettings() {
            // Lấy dữ liệu cấu hình thông qua SystemDao (Sử dụng hàm bạn mới viết ở bước trước)
            BankSystem settings = staffController.getBankSystemConfig();

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



       if(choice.equals("1")){
        String mess = staffController.updateStatusAccount(idAccountString, "ACTIVE");
           System.out.println(mess);
       }
       else if(choice.equals("2")){
           String mess = staffController.updateStatusAccount(idAccountString, "LOCKED");
           System.out.println(mess);
       }
       else if(choice.equals("3")){
           String mess = staffController.updateStatusAccount(idAccountString, "CLOSED");
           System.out.println(mess);
       }


    }

    public void showInformationCutomer(){
        System.out.println("Nhập id khách hàng cần tra cứu");
        String idCustomerString = scanner.nextLine();

       Customer customer = staffController.getCustomerById(idCustomerString);

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
            List<LoanRequest> loanRequestList = staffController.getAllLoanRequestPending();
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
                        req.getCustomerOwner().getUserId(),
                        req.getRequestAmount(),
                        req.getLoanTerm(),
                        req.getRequestDate().toString());
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

        LoanRequest loanRequest = staffController.getLoanRequestById(idLoanRequestString);

        if(loanRequest == null){
            System.out.println("Không tìm thấy khoản vay");
            return;
        }

        System.out.println("1. Phê duyệt.");
        System.out.println("2. Từ chối.");
        String choice = scanner.nextLine();
        if(choice.equals("1")){
            Customer customer = staffController.getCustomerById(String.valueOf(loanRequest.getCustomerOwner().getUserId()));
            customerUI = new CustomerView(customer);
            // using directly customerUI method is fine or we should have an option, but keep logic
            customerUI.checkAllAccount(loanRequest.getCustomerOwner().getUserId());
            System.out.println("Nhập id tài khoản thanh toán nhận tiền");
            String idAccountString = scanner.nextLine();

            staffController.approveLoanRequest(loanRequest, idAccountString);

        }
        else if(choice.equals("2")){
            staffController.rejectLoanRequest(loanRequest);
        }
        else {
            System.out.println("Vui lòng chọn đúng lựa chọn");
            return;
        }
    }

    public void handleUpdateTime(){
        staffController.handleUpdateTime();
    }
    //xử lí cộng ngày
    public void handlePlusDaySystem(){
        System.out.println("Nhập số ngày muốn cộng: ");
        String dayString = scanner.nextLine();
        staffController.handPlusDaySystem(dayString);
    }
    //xử lí trừ ngày
    public void handleMinusDaySystem(){
        System.out.println("Nhập số ngày muốn trừ: ");
        String dayString = scanner.nextLine();
        staffController.handMinusDaySystem(dayString);
    }
}
