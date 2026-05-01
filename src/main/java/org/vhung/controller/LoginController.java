package org.vhung.controller;

import org.vhung.enity.User;
import org.vhung.service.AuthService;
import org.vhung.util.ParseNumber;

public class LoginController {
    private AuthService authService = new AuthService();

    public String register(String userName, String password, String fullName, String email, String monlyIncomeDouble){
        try{
            authService.registerCustomer(userName, password, fullName, email, ParseNumber.parseDouble(monlyIncomeDouble));
            return "Đăng kí thành công";
        }
        catch (RuntimeException e){
            return e.getMessage();
        }
    }

    public User login(String userName, String password){
        if (userName == null || userName.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new RuntimeException("Tên đăng nhập hoặc mật khẩu không được để trống!");
        }
        return authService.loginCustomer(userName, password);


    }
}
