package org.vhung.dao;

import org.vhung.enity.LoanAccount;
import org.vhung.enity.LoanRequest;
import org.vhung.enity.enums.LoanRequestStatus;
import org.vhung.util.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LoanDao {
    private UserDao userDao = new UserDao();
    //thêm yêu cầu vay
    public void addLoanRequest(LoanRequest loanRequest){
        String query = "INSERT INTO loan_request (user_id, request_amount, status, request_date, loan_term) " +
                "VALUES (?, ?, ?, ?, ?)";
        try(Connection connection = DBConnect.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)){
            //set
            preparedStatement.setInt(1, loanRequest.getCustomerOwner().getUserId());
            preparedStatement.setDouble(2, loanRequest.getRequestAmount());
            preparedStatement.setString(3, loanRequest.getStatus().name());
            preparedStatement.setObject(4, loanRequest.getRequestDate());
            preparedStatement.setInt(5, loanRequest.getLoanTerm());

            preparedStatement.executeUpdate();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
    //lấy danh sách những yêu cầu vay pending
    public List<LoanRequest> getAllLoanRequestPending(){
        List<LoanRequest> loanRequestList = null;
        String query = "SELECT * FROM loan_request " +
                "WHERE status LIKE ?";
        try(Connection connection = DBConnect.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)){
           preparedStatement.setString(1, "PENDING");
            ResultSet resultSet = preparedStatement.executeQuery();

            loanRequestList = new ArrayList<>();

            while(resultSet.next()){
                LoanRequest loanRequest = new LoanRequest();
                //set

                loanRequest.setLoanRequestId(resultSet.getInt("request_id"));
                loanRequest.setCustomerOwner(userDao.getCustomerById(resultSet.getInt("user_id")));
                loanRequest.setRequestAmount(resultSet.getDouble("request_amount"));
                loanRequest.setStatus(LoanRequestStatus.valueOf(resultSet.getString("status").toUpperCase()));
                loanRequest.setRequestDate(resultSet.getObject("request_date", LocalDateTime.class));
                loanRequest.setLoanTerm(resultSet.getInt("loan_term"));
                //add
                loanRequestList.add(loanRequest);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return loanRequestList;
    }
    //Cập nhật trạng thái khoản vay
    public void updateStatusLoanRequest(int idLoanRequest, String status){
        String query = "UPDATE loan_request " +
                "SET status = ? " +
                "WHERE request_id = ?";
        try(Connection connection = DBConnect.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setString(1, status.toUpperCase());
            preparedStatement.setInt(2, idLoanRequest);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
    //laays loanRequest
    public LoanRequest getLoanRequestById(int id){
        LoanRequest loanRequest = null;
        String query = "SELECT * FROM loan_request " +
                "WHERE request_id = ?";
        try(Connection connection = DBConnect.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                loanRequest = new LoanRequest();

                loanRequest.setLoanRequestId(resultSet.getInt("request_id"));
                loanRequest.setCustomerOwner(userDao.getCustomerById(resultSet.getInt("user_id")));
                loanRequest.setRequestAmount(resultSet.getDouble("request_amount"));
                loanRequest.setStatus(LoanRequestStatus.valueOf(resultSet.getString("status").toUpperCase()));
                loanRequest.setRequestDate(resultSet.getObject("request_date", LocalDateTime.class));
                loanRequest.setLoanTerm(resultSet.getInt("loan_term"));

            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return loanRequest;
    }
    //cập nhật số tiền đã trả trong tháng về 0
    public void updatePaidThisMonth(LoanAccount loanAccount){
        String query = "UPDATE loan_account " +
                "SET amount_paid_this_month = 0 " +
                "WHERE account_id = ?";
        try (Connection connection = DBConnect.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setInt(1, loanAccount.getAccountId());
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
    //cập nhật ngày trả nợ
    public void updateNextPaymentDate(LoanAccount loanAccount){
        String query = "UPDATE loan_account la " +
                "SET next_payment_date = date_trunc('month', bs.system_date) + INTERVAL '14 days' " +
                "FROM bank_setting bs " +
                "WHERE la.account_id = ?";
        try(Connection connection = DBConnect.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)){

            preparedStatement.setInt(1, loanAccount.getAccountId());
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
    //cập nhật soos tiền phải trả hàng tháng
    public void updatePaidMonth(LoanAccount loanAccount){
        String query = "UPDATE loan_account " +
                "SET monthly_required_payment = ? " +
                "WHERE account_id = ?";
        try (Connection connection = DBConnect.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)){
            //tính số tiền phải trả
            double amount = (loanAccount.getPricipalAmount() / loanAccount.getLoanTerm() ) + (loanAccount.getPricipalAmount() * (loanAccount.getInterestRate() /12) );
            preparedStatement.setDouble(1, amount);
            preparedStatement.setInt(2, loanAccount.getAccountId());
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
    //cập nhật số tiền đã trả mỗi khi trả nợ
    public void updateAmountPaidThisMonthAfterPay(LoanAccount loanAccount, double amount){
        String query = "UPDATE loan_account " +
                "SET amount_paid_this_month = amount_paid_this_month + ? " +
                "WHERE account_id = ?";
        try(Connection connection = DBConnect.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setDouble(1, amount);
            preparedStatement.setInt(2, loanAccount.getAccountId());
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
}
