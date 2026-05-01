package org.vhung.dao;

import org.vhung.enity.Transaction;
import org.vhung.enity.enums.TransactionType;
import org.vhung.util.DBConnect;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class TransactionDao {

    public void addTransactionPlus(Transaction transaction){
        String query = "INSERT INTO transaction (type, amount, timestamp, account_id, description) " +
                "VALUES (?, ?, ?, ?, ?)";
        try(Connection connection = DBConnect.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)){
            //set giá trị
            preparedStatement.setString(1, transaction.getTransactionType().name());
            preparedStatement.setDouble(2, transaction.getAmount());
            LocalDateTime nowTime = LocalDateTime.now();
            preparedStatement.setObject(3, nowTime);
            preparedStatement.setInt(4, transaction.getPlusAccountId());


            preparedStatement.setString(5, transaction.getDescription());
            preparedStatement.executeUpdate();

        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
    public void addTransactionMinus(Transaction transaction){
        String query = "INSERT INTO transaction (type, amount, timestamp, account_id, description) " +
                "VALUES (?, ?, ?, ?, ?)";
        try(Connection connection = DBConnect.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)){
            //set giá trị
            preparedStatement.setString(1, transaction.getTransactionType().name());
            preparedStatement.setDouble(2, -transaction.getAmount());
            LocalDateTime nowTime = LocalDateTime.now();
            preparedStatement.setObject(3, nowTime);
            preparedStatement.setInt(4, transaction.getMinustAccountId());

            preparedStatement.setString(5, transaction.getDescription());
            preparedStatement.executeUpdate();

        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
    public List<Transaction> getTransactionsByCustomer(int customerId) {
        java.util.List<Transaction> transactions = new java.util.ArrayList<>();
        // Query Lấy tất cả transaction có account_id thuộc về customer hiện tại
        String query = "SELECT t.transaction_id, t.type, t.amount, t.timestamp, t.account_id, t.description " +
                "FROM transaction t " +
                "WHERE t.account_id = ? " +
                "ORDER BY t.timestamp DESC";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, customerId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Transaction transaction = new Transaction();

                transaction.setTransactionId(resultSet.getInt("transaction_id"));
                transaction.setTransactionType(TransactionType.valueOf(resultSet.getString("type")));
                transaction.setAmount(resultSet.getDouble("amount"));
                transaction.setTimestamp(resultSet.getObject("timestamp", LocalDateTime.class));

                // Mặc định gán vào PlusAccountId để tiện lấy ra in (Bạn có thể điều chỉnh mapping theo ý muốn)
                transaction.setPlusAccountId(resultSet.getInt("account_id"));
                transaction.setDescription(resultSet.getString("description"));

                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

}
