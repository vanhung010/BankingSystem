package org.vhung.dao;

import org.vhung.enity.Customer;
import org.vhung.enity.Staff;
import org.vhung.enity.User;
import org.vhung.util.DBConnect;

import java.sql.*;

import static org.vhung.enity.enums.Role.CUSTOMER;

public class UserDao {
    //đăng kí
    public boolean registerUser(Customer customer)  {
        boolean check = false;
        if(checkEmailDuplicate(customer.getEmail())){
            throw new RuntimeException("Trùng email");
        }
        String queryUser = "INSERT INTO users (user_name, password, fullname, role,  email) " +
                "VALUES (?, ?, ?, 'CUSTOMER', ?)";
        String queryCustomer = "INSERT INTO customer (user_id, monthly_income) " +
                "VALUES (?,?)";
        Connection connection = DBConnect.getConnection();
        try{

           //tắt tự động commuit
            connection.setAutoCommit(false);
            try(PreparedStatement preparedStatementUser = connection.prepareStatement(queryUser, Statement.RETURN_GENERATED_KEYS)){
                //set dữ liệu
                preparedStatementUser.setString(1, customer.getUserName());
                preparedStatementUser.setString(2, customer.getPassword());
                preparedStatementUser.setString(3, customer.getFullName());
                preparedStatementUser.setString(4, customer.getEmail());

                int arrowEffect = preparedStatementUser.executeUpdate();

                if(arrowEffect ==0 ){
                    throw new SQLException("THêm user thất bạiii");
                }
                //lấy iduser vừa tạo
                int userIdInsert = -1;
                try(ResultSet resultSet = preparedStatementUser.getGeneratedKeys()){
                    if(resultSet.next()){
                        userIdInsert = resultSet.getInt(1);
                    }
                }

                try(PreparedStatement preparedStatementCustomer = connection.prepareStatement(queryCustomer)){
                    //set dữ liệu
                    preparedStatementCustomer.setInt(1, userIdInsert);
                    preparedStatementCustomer.setDouble(2, customer.getMonthlyIncome());

                    int arrowEffectCustomer = preparedStatementCustomer.executeUpdate();

                    if(arrowEffectCustomer ==0){
                        throw new SQLException("Thêm khách hàng thất bại");
                    }
                    //chạy không lỗi thì thêm vào
                    connection.commit();
                    check = true;
                }

            }
        } catch (SQLException e) {
            try{
                connection.rollback();
            }
            catch (SQLException e1){
                e1.printStackTrace();
            }
        }

        finally {
            try{
                connection.close();
            }
            catch (SQLException e){
                e.printStackTrace();
            }
        }


        return check;
    }

    public boolean checkEmailDuplicate(String email){
        String query = "SELECT email FROM users " +
                "WHERE email = ?";

        try(Connection connection = DBConnect.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
           return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public User getUserByUserNameOrEmail(String userName){
        String query = "SELECT * FROM users " +
                "LEFT JOIN customer " +
                "ON users.user_id = customer.user_id " +
                "WHERE users.user_name = ? OR users.email = ?";
        try(Connection connection = DBConnect.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)){

            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, userName);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                //chia role
                if(resultSet.getString("role").equals("CUSTOMER") && resultSet.getBoolean("is_active") == true){
                    //nếu là khách hàng
                    Customer customer = new Customer();
                    //set thuộc tính
                    customer.setUserId(resultSet.getInt("user_id"));
                    customer.setUserName(resultSet.getString("user_name"));
                    customer.setPassword(resultSet.getString("password"));
                    customer.setFullName(resultSet.getString("fullname"));
                    customer.setEmail(resultSet.getString("email"));
                    customer.setMonthlyIncome(resultSet.getDouble("monthly_income"));

                    return customer;
                }
                else{
                    Staff staff = new Staff();

                    staff.setUserId(resultSet.getInt("user_id"));
                    staff.setUserName(resultSet.getString("user_name"));
                    staff.setPassword(resultSet.getString("password"));
                    staff.setFullName(resultSet.getString("fullname"));
                    staff.setEmail(resultSet.getString("email"));

                    return staff;
                }
            }
            else {
                throw new RuntimeException("Sai tài khoản hoặc mật khẩu");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public Customer getCustomerById(int id){
        String query = "SELECT users.user_id, users.user_name, users.password, users.fullname, " +
                "users.email, users.role, users.is_active, customer.monthly_income " +
                "FROM users " +
                "INNER JOIN customer ON users.user_id = customer.user_id " +
                "WHERE users.user_id = ?";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Nếu bạn muốn chặn account bị khóa thì check is_active ở đây


                    // Nếu muốn chắc chắn đúng role CUSTOMER:
                    if (!"CUSTOMER".equalsIgnoreCase(resultSet.getString("role"))) {
                        throw new RuntimeException("User này không phải CUSTOMER");
                    }

                    Customer customer = new Customer();
                    customer.setUserId(resultSet.getInt("user_id"));
                    customer.setUserName(resultSet.getString("user_name"));
                    customer.setPassword(resultSet.getString("password"));
                    customer.setFullName(resultSet.getString("fullname"));
                    customer.setEmail(resultSet.getString("email"));
                    customer.setMonthlyIncome(resultSet.getDouble("monthly_income"));
                    return customer;
                } else {
                    return null; // hoặc throw new RuntimeException("Không tìm thấy customer");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi DB khi lấy customer theo id=" + id, e);
    }

}
}
