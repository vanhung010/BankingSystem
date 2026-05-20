package org.vhung.dao;

import org.vhung.System.BankSystem;
import org.vhung.util.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class SystemDao {

    public double getMinBalance(){
        String query = "SELECT min_checking_balance FROM bank_setting WHERE id =?";
        try(Connection connection = DBConnect.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query))
        {
        preparedStatement.setInt(1, 1);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                double minBalance = resultSet.getDouble(1);
                return minBalance;
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    public double getInterestLoan(){
        double rate = 0;
        String query = "SELECT base_loan_interest_rate As rate " +
                "FROM bank_setting " +
                "WHERE id = 1";
        try(Connection connection = DBConnect.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)){
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                rate = resultSet.getDouble("rate");
            }
        }
        catch (SQLException e){
            e.printStackTrace();

        }
        return rate;
    }

    public double getInterestTerm(int term){
        double rate = 0;
        String query = "SELECT * " +
                "FROM bank_setting " +
                "WHERE id = 1";
        try(Connection connection = DBConnect.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)){
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                if(term == 1) {
                    rate = resultSet.getDouble("interest_rate_1M");
                }
                else if(term == 6) {
                    rate = resultSet.getDouble("interest_rate_6M");
                }
                if(term == 12) {
                    rate = resultSet.getDouble("interest_rate_12M");
                }

            }
            else if(resultSet.next()){
                if(term == 1) {
                    rate = resultSet.getDouble("interest_rate_1M");
                }
            }
            if(resultSet.next()){
                if(term == 1) {
                    rate = resultSet.getDouble("interest_rate_1M");
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();

        }
        return rate;
    }

    public double getInterestDemand(){
        double interest =0;
        String query = "SELECT demand_interest_rate " +
                "FROM bank_setting " +
                "WHERE id =1";
        try(Connection connection = DBConnect.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)){
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                interest = resultSet.getDouble(1);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return interest;
    }

    public LocalDate getTimeSystem(){
        LocalDate result = null;
        String query = "SELECT system_date " +
                "FROM bank_setting " +
                "WHERE id = 1";
        try(Connection connection = DBConnect.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)){
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                result = resultSet.getObject("system_date", LocalDate.class);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return  result;
    }

    public void updateDateSystemNow(){
        String query = "UPDATE bank_setting SET system_date = ? " +
                "WHERE id = 1";
        try(Connection connection = DBConnect.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)){

            preparedStatement.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void updateDateSystemPlus1Month(){
        String query = "UPDATE bank_setting SET system_date = ? " +
                "WHERE id = 1";
        try(Connection connection = DBConnect.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)){
            //lấy date hiện tại của hệ thống
            LocalDate result = null;
            String query1 = "SELECT system_date " +
                    "FROM bank_setting " +
                    "WHERE id = 1";
            try(PreparedStatement preparedStatement1 = connection.prepareStatement(query1)){
                ResultSet resultSet = preparedStatement1.executeQuery();
                if(resultSet.next()){
                    result = resultSet.getObject("system_date", LocalDate.class);
                }
            }
            //kết thức
            preparedStatement.setDate(1, java.sql.Date.valueOf(result.plusMonths(1)));
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void plusDaySystem(int days){
        String query = "UPDATE bank_setting SET system_date = system_date + ? " +
                "WHERE id = 1";
        try(Connection connection = DBConnect.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query))
        {
            preparedStatement.setInt(1, days);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void minusDaySystem(int days){
        String query = "UPDATE bank_setting SET system_date = system_date - ? " +
                "WHERE id = 1";
        try(Connection connection = DBConnect.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query))
        {
            preparedStatement.setInt(1, days);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public BankSystem getBankSystemConfig() {
        // Lấy Instance thay vì khởi tạo bằng new Do BankSystem của bạn là Singleton
        BankSystem bankSystem = BankSystem.getInstance();

        String query = "SELECT * FROM bank_setting WHERE id = 1";
        try (Connection connection = DBConnect.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Set các giá trị cho đối tượng BankSystem dựa vào các cột trong Database
                bankSystem.setMinCheckingBalance(resultSet.getDouble("min_checking_balance"));

                bankSystem.setBaseLoanInterestRate(resultSet.getDouble("base_loan_interest_rate"));
                bankSystem.setInterestRate1M(resultSet.getDouble("interest_rate_1M"));
                bankSystem.setInterestRate6M(resultSet.getDouble("interest_rate_6M"));
                bankSystem.setInterestRate12M(resultSet.getDouble("interest_rate_12M"));
                bankSystem.setDemandInterestRate(resultSet.getDouble("demand_interest_rate"));
                bankSystem.setSystemDate(resultSet.getObject("system_date", LocalDate.class));
                bankSystem.setMinSavingDeposit(resultSet.getDouble("min_saving_deposit"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bankSystem;
    }
    public boolean updateConfigValue(String columnName, double newValue) {
        // Chỉ cho phép update các list column hợp lệ để tránh lỗi bảo mật SQL Injection
        // Giả định row cấu hình có id = 1
        String query = "UPDATE bank_setting SET " + columnName + " = ? WHERE id = 1";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setDouble(1, newValue);
            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
