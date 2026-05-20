package org.vhung.view;

import org.vhung.controller.LoginController;
import org.vhung.enity.Customer;
import org.vhung.enity.Staff;
import org.vhung.enity.User;

import java.util.Scanner;

public class LoginView {

    private Scanner scanner;
    private LoginController loginController = new LoginController();

    public LoginView() {

        this.scanner = new Scanner(System.in);
    }
    public void display() {
        while (true) {
            System.out.println("\n=== HLK BANK ===");
            System.out.println("1. Đăng nhập");
            System.out.println("2. Đăng ký");
            System.out.println("0. Thoát");
            System.out.print("Chọn: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    User user = handleLogin();
                    if (user instanceof Customer) {
                        new CustomerView((Customer) user).run();
                    } else if (user instanceof Staff) {
                        new StaffView((Staff) user).run();
                    } else {
                        break;
                    }
                    break;
                case "2":
                    handleRegister();
                    break;
                case "0":
                    System.out.println("Cảm ơn bạn đã sử dụng dịch vụ!");
                    return;
                default:
                    System.out.println("Lựa chọn không hợp lệ, vui lòng thử lại!");
            }
        }
    }
    public void handleRegister(){
        System.out.println("Nhập tên đăng nhập");
        String userName = scanner.nextLine();
        System.out.println("Nhập mật khẩu ");
        String password = scanner.nextLine();
        System.out.println("Nhập tên đầy đủ");
        String fullName = scanner.nextLine();
        System.out.println("Nhập email");
        String email = scanner.nextLine();
        System.out.println("Nhập thu nhập hàng tháng");
        String monlyIncomeString = scanner.nextLine();


        String message = loginController.register(userName, password, fullName, email, monlyIncomeString);

        System.out.println(message);
    }

    public User handleLogin(){
        System.out.println("Nhập user name hoặc email");
        String userName = scanner.nextLine();
        System.out.println("Nhập mật khẩu");
        String password = scanner.nextLine();
        try {
            User user = loginController.login(userName, password);
            return user;
        }
        catch (RuntimeException e){
            System.out.println(e.getMessage());
            return null;
        }

    }

}
