package org.vhung.service;

import org.vhung.dao.SystemDao;
import org.vhung.dao.UserDao;
import org.vhung.enity.Customer;
import org.vhung.enity.Staff;
import org.vhung.enity.User;

import java.time.LocalDate;

public class AuthService {
    private UserDao userDao;
    private SystemDao systemDao = new SystemDao();
    public AuthService() {
        userDao = new UserDao();
    }

    public boolean registerCustomer(String username, String password, String fullName, String email, double monthlyIncome){
        Customer user = new Customer();

        user.setUserName(username);
        user.setPassword(password);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setMonthlyIncome(monthlyIncome);

        return userDao.registerUser(user);
    }

    public User loginCustomer(String userName, String password){
        User user = userDao.getUserByUserNameOrEmail(userName);
        if(user == null){
            throw new RuntimeException("Tài khoản không tồn tại");
        }
        if(!user.getPassword().equals(password)){
            throw  new RuntimeException("Sai mật khẩu");
        }

       if(user instanceof Customer){
           Customer customer = (Customer) user;
           return  customer;
       }
       else{
           Staff staff = (Staff) user;
           return staff;
       }
    }
    public LocalDate getSystemDate(){
        return systemDao.getTimeSystem();
    }
}
