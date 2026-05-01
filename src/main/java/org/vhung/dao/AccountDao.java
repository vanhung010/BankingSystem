package org.vhung.dao;

import org.vhung.enity.*;
import org.vhung.enity.enums.AccountStatus;
import org.vhung.pattern.factory.AccountFactory;
import org.vhung.pattern.strategy.TermInterestStrategy;
import org.vhung.util.DBConnect;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AccountDao {
    private SystemDao systemDao;
    private UserDao userDao;

    public AccountDao(){
        systemDao = new SystemDao();
        userDao = new UserDao();
    }

    public boolean addCheckingAccount(int idOwner, double balance){
        double minBalance = systemDao.getMinBalance();
        boolean isSucces = false;
        //không đủ số dư
        if(balance < minBalance){
            throw new RuntimeException("Số dư phải đạt tối thiếu: "+ minBalance);
        }
        String queryAccount = "INSERT INTO account (balance, status, created_at, user_id, account_type) " +
                "VALUES (?, 'ACTIVE', ?, ?, 'CHECKING')";
        String queryChecking = "INSERT INTO checking_account (account_id) " +
                "VALUES (?)";

        Connection connection= DBConnect.getConnection();
        try(PreparedStatement preparedStatementAccount = connection.prepareStatement(queryAccount, Statement.RETURN_GENERATED_KEYS)){
            connection.setAutoCommit(false);
            LocalDate nowTime = LocalDate.now();
            preparedStatementAccount.setDouble(1, balance);
            preparedStatementAccount.setDate(2, Date.valueOf(nowTime));
            preparedStatementAccount.setInt(3, idOwner);

            int arrowEffect = preparedStatementAccount.executeUpdate();

            //lấy id của account vừa tạo
            int idAccountGene = -1;
            ResultSet resultSet = preparedStatementAccount.getGeneratedKeys();
            if(resultSet.next()){
                idAccountGene = resultSet.getInt(1);
            }
            try(PreparedStatement preparedStatementChecking = connection.prepareStatement(queryChecking)){
                preparedStatementChecking.setInt(1, idAccountGene);
                preparedStatementChecking.executeUpdate();

                connection.commit();
                connection.setAutoCommit(true);
                isSucces = true;
            }

        }
        catch (SQLException e){
            try{
                connection.rollback();
            }
            catch (SQLException e1){
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        finally {
           try {
               connection.close();
           }
           catch (SQLException e1){
               e1.printStackTrace();
           }
        }
        return isSucces;
    }

    public void addLoanAccount(int idOwner, double amount, int term){
        String queryAccount = "INSERT INTO account (balance, status, created_at, user_id, account_type) " +
                "VALUES (?, 'ACTIVE', ?, ?, 'LOAN')";
        String queryLoan = "INSERT INTO loan_account (account_id, principal_amount, interest_rate, next_payment_date, term) " +
                "VALUES (?, ?, ?, ? ,?)";

        Connection connection= DBConnect.getConnection();
        try(PreparedStatement preparedStatementAccount = connection.prepareStatement(queryAccount, Statement.RETURN_GENERATED_KEYS)){
            connection.setAutoCommit(false);
            LocalDate nowTime = LocalDate.now();
            preparedStatementAccount.setDouble(1, amount);
            preparedStatementAccount.setDate(2, Date.valueOf(nowTime));
            preparedStatementAccount.setInt(3, idOwner);

            int arrowEffect = preparedStatementAccount.executeUpdate();

            //lấy id của account vừa tạo
            int idAccountGene = -1;
            ResultSet resultSet = preparedStatementAccount.getGeneratedKeys();
            if(resultSet.next()){
                idAccountGene = resultSet.getInt(1);
            }
            //thêm bảng loanAccount
            try(PreparedStatement preparedStatementLoan = connection.prepareStatement(queryLoan)){

                Account account = AccountFactory.createAccount("LOAN", userDao.getCustomerById(idOwner));
                LoanAccount loanAccount = (LoanAccount) account;

                double interest;
                try (PreparedStatement ps = connection.prepareStatement(
                        "SELECT base_loan_interest_rate FROM bank_setting WHERE id = 1")) {
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        interest = rs.getDouble(1);
                    }
                }

                preparedStatementLoan.setInt(1, idAccountGene);
                preparedStatementLoan.setDouble(2, amount);
                preparedStatementLoan.setDouble(3, interest);
                preparedStatementLoan.setDate(4, java.sql.Date.valueOf(loanAccount.getCreatedAt().plusMonths(1).withDayOfMonth(15)));
                preparedStatementLoan.setInt(5, term);

                preparedStatementLoan.executeUpdate();

                connection.commit();
                connection.setAutoCommit(true);

            }

        }
        catch (SQLException e){
            try{
                if(!connection.isClosed())
                connection.rollback();
            }
            catch (SQLException e1){
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        finally {
            try {
                connection.close();
            }
            catch (SQLException e1){
                e1.printStackTrace();
            }
        }
    }

    public void addSavingAccount(int idOwner, double amount, int term){
        String queryAccount = "INSERT INTO account (balance, status, created_at, user_id, account_type) " +
                "VALUES (?, 'ACTIVE', ?, ?, 'SAVING')";
        String querySaving = "INSERT INTO saving_account (account_id, term_months, interest_rate, deposit_date, maturity_date) " +
                "VALUES (?, ?, ?, ? ,?)";

        Connection connection= DBConnect.getConnection();
        try(PreparedStatement preparedStatementAccount = connection.prepareStatement(queryAccount, Statement.RETURN_GENERATED_KEYS)){
            connection.setAutoCommit(false);
            LocalDate nowTime = LocalDate.now();
            preparedStatementAccount.setDouble(1, amount);
            preparedStatementAccount.setDate(2, Date.valueOf(nowTime));
            preparedStatementAccount.setInt(3, idOwner);

            int arrowEffect = preparedStatementAccount.executeUpdate();

            //lấy id của account vừa tạo
            int idAccountGene = -1;
            ResultSet resultSet = preparedStatementAccount.getGeneratedKeys();
            if(resultSet.next()){
                idAccountGene = resultSet.getInt(1);
            }
            //thêm bảng loanAccount
            try(PreparedStatement preparedStatementSaving = connection.prepareStatement(querySaving)){

                Account account = AccountFactory.createAccount("SAVING", userDao.getCustomerById(idOwner));
                SavingAccount savingAccount = (SavingAccount) account;

                //lấy lãi
                double interest = 0;
                String queryRate = "SELECT * " +
                        "FROM bank_setting " +
                        "WHERE id = 1";
                try(PreparedStatement preparedStatementRate = connection.prepareStatement(queryRate)){
                    ResultSet resultSetRate = preparedStatementRate.executeQuery();
                    if(resultSetRate.next()){
                        if(term == 1) {
                            interest = resultSetRate.getDouble("interest_rate_1M");
                        }
                        else if(term == 6) {
                            interest = resultSetRate.getDouble("interest_rate_6M");
                        }
                        else if(term == 12) {
                            interest = resultSetRate.getDouble("interest_rate_12M");
                        }
                    }
                }


                preparedStatementSaving.setInt(1, idAccountGene);
                preparedStatementSaving.setDouble(2, term);
                preparedStatementSaving.setDouble(3, interest);
                preparedStatementSaving.setDate(4, java.sql.Date.valueOf(savingAccount.getDepositDate()));
                preparedStatementSaving.setDate(5, java.sql.Date.valueOf(savingAccount.getDepositDate().plusMonths(term)));

                preparedStatementSaving.executeUpdate();

                connection.commit();
                connection.setAutoCommit(true);

            }

        }
        catch (SQLException e){
            try{
                if(!connection.isClosed())
                    connection.rollback();
            }
            catch (SQLException e1){
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        finally {
            try {
                connection.close();
            }
            catch (SQLException e1){
                e1.printStackTrace();
            }
        }

    }

    public Account getAccountById(int idAccount){
        String query = "SELECT * FROM account " +
                "WHERE account_id = ?";
        String queryLoan = "SELECT * FROM loan_account WHERE account_id = ?";
        String querySaving = "SELECT * FROM saving_account WHERE account_id = ?";
        try(Connection connection = DBConnect.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setInt(1, idAccount);

            ResultSet resultSet = preparedStatement.executeQuery();



            if(resultSet.next()){
                //nếu là tài khoản thanh toán
                if(resultSet.getString("account_type").equals("CHECKING")){
                    CheckingAccount account = new CheckingAccount();
                    //set thuộc tính
                    account.setAccountId(resultSet.getInt("account_id"));
                    account.setOwner(userDao.getCustomerById(resultSet.getInt("user_id")));
                    account.setBalance(resultSet.getDouble("balance"));
                    account.setAccountStatus(AccountStatus.valueOf(resultSet.getString("status")));
                    account.setCreatedAt(Date.valueOf(resultSet.getDate("created_at").toLocalDate()).toLocalDate());

                    return account;
                }
                //nếu là tài khoản tiết kiệm
                else if(resultSet.getString("account_type").equals("SAVING")){
                   try(PreparedStatement preparedStatementSaving = connection.prepareStatement(querySaving)){
                    SavingAccount account = new SavingAccount();
                    //set thuộc tính
                    account.setAccountId(resultSet.getInt("account_id"));
                    account.setOwner(userDao.getCustomerById(resultSet.getInt("user_id")));
                    account.setBalance(resultSet.getDouble("balance"));
                    account.setAccountStatus(AccountStatus.valueOf(resultSet.getString("status")));
                    account.setCreatedAt(Date.valueOf(resultSet.getDate("created_at").toLocalDate()).toLocalDate());
                    //set thuộc tính thuộc về tài khoản vay
                       preparedStatementSaving.setInt(1, idAccount);
                       try(ResultSet resultSetSaving = preparedStatementSaving.executeQuery()){
                       if(resultSetSaving.next()){
                    account.setInterestStrategy(new TermInterestStrategy());
                    account.setDepositDate(resultSetSaving.getObject("deposit_date", LocalDate.class));
                    account.setMaturityDate(resultSetSaving.getObject("maturity_date", LocalDate.class));
                    account.setTerm(resultSetSaving.getInt("term_months"));
                       }
                       }

                    return account;}
                }
                else { //nếu là tài khoản vay
                    try(PreparedStatement preparedStatementloan = connection.prepareStatement(queryLoan)) {
                        LoanAccount account = new LoanAccount();
                        //set thuộc tính
                        account.setAccountId(resultSet.getInt("account_id"));
                        account.setOwner(userDao.getCustomerById(resultSet.getInt("user_id")));
                        account.setBalance(resultSet.getDouble("balance"));
                        account.setAccountStatus(AccountStatus.valueOf(resultSet.getString("status")));
                        account.setCreatedAt(Date.valueOf(resultSet.getDate("created_at").toLocalDate()).toLocalDate());
                        //set những thuộc tính của tài khoản vay
                        preparedStatementloan.setInt(1, idAccount);
                        try(ResultSet resultSetLoan = preparedStatementloan.executeQuery()){
                            if(resultSetLoan.next()){

                                account.setPricipalAmount(resultSetLoan.getDouble("principal_amount"));
                                account.setNextPaymentDate(resultSetLoan.getObject("next_payment_date", LocalDate.class));
                                account.setLoanTerm(resultSetLoan.getInt("term"));
                                account.setAmountPaidThisMonth(resultSetLoan.getDouble("amount_paid_this_month"));
                            }
                        }
                        return account;
                    }
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public List<Account> getAllAccountOfCustomerDao (int idCustomer){
        String query = "SELECT * FROM account As a " +
                "LEFT JOIN checking_account As c ON a.account_id = c.account_id " +
                "LEFT JOIN loan_account As l ON l.account_id = c.account_id " +
                "LEFT JOIN saving_account As s ON s.account_id = c.account_id " +
                "WHERE a.user_id = ?";
        List<Account> accountList = new ArrayList<>();
        try(Connection connection = DBConnect.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setInt(1, idCustomer);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                if(resultSet.getString("account_type").equals("CHECKING")){
                    Account account = getAccountById(resultSet.getInt("account_id"));
                    CheckingAccount checkingAccount = (CheckingAccount)  account;
                    accountList.add(checkingAccount);
                }
                else if(resultSet.getString("account_type").equals("SAVING")){
                    Account account = getAccountById(resultSet.getInt("account_id"));
                    SavingAccount checkingAccount = (SavingAccount)  account;
                    accountList.add(checkingAccount);
                }
                else if(resultSet.getString("account_type").equals("LOAN")){
                    Account account = getAccountById(resultSet.getInt("account_id"));
                    LoanAccount checkingAccount = (LoanAccount)  account;
                    accountList.add(checkingAccount);
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return  accountList;
    }

    public void updateBalance(int idAccount, double amount){
        String query = "UPDATE account " +
                "SET balance = ? " +
                "WHERE account_id = ?";
        try(Connection connection = DBConnect.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setDouble(1, amount);
            preparedStatement.setInt(2, idAccount);

            preparedStatement.executeUpdate();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    //cập nhật trạng thái tài khoản
    public void updateStatusAccount(Account account, String accountStatus){
        String query = "UPDATE account " +
                "SET status = ? " +
                "WHERE account_id =?";
        try(Connection connection = DBConnect.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setString(1, accountStatus.toUpperCase());
            preparedStatement.setInt(2, account.getAccountId());
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public List<LoanAccount> getAllLoanAccountActive(){
        List<LoanAccount> accountList = new ArrayList<>();
        String query = "SELECT l.account_id, l.principal_amount, l.amount_paid_this_month, l.monthly_required_payment, l.next_payment_date, l.term, a.balance, a.created_at, a.user_id, a.status " +
                "FROM loan_account As l " +
                "JOIN account As a ON l.account_id = a.account_id " +
                "WHERE a.status = ?";
        try(Connection connection = DBConnect.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)){

            preparedStatement.setString(1, "ACTIVE");

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                LoanAccount loanAccount = new LoanAccount();

                loanAccount.setAccountId(resultSet.getInt("account_id"));
                loanAccount.setBalance(resultSet.getDouble("balance"));
                loanAccount.setAccountStatus(AccountStatus.valueOf(resultSet.getString("status").toUpperCase()));
                loanAccount.setCreatedAt(resultSet.getObject("created_at", LocalDate.class));
                loanAccount.setOwner(userDao.getCustomerById(resultSet.getInt("user_id")));
                loanAccount.setPricipalAmount(resultSet.getDouble("principal_amount"));
                loanAccount.setInterestRate(systemDao.getInterestLoan());
                loanAccount.setNextPaymentDate(resultSet.getObject("next_payment_date", LocalDate.class));
                loanAccount.setLoanTerm(resultSet.getInt("term"));
                loanAccount.setAmountPaidThisMonth(resultSet.getDouble("amount_paid_this_month"));
                loanAccount.setMonthlyRequiredPayment(resultSet.getDouble("monthly_required_payment"));
                accountList.add(loanAccount);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return accountList;
    }

    public List<SavingAccount> getAllSavingAccountActive(){
        List<SavingAccount> accountList = new ArrayList<>();
        String query = "SELECT s.account_id, s.term_months, s.interest_rate, s.deposit_date, s.maturity_date, a.balance, a.user_id, a.status " +
                "FROM saving_account As s " +
                "JOIN account As a ON s.account_id = a.account_id " +
                "WHERE a.status = ?";
        try(Connection connection = DBConnect.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)){

            preparedStatement.setString(1, "ACTIVE");


            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                SavingAccount savingAccount = new SavingAccount();

                savingAccount.setAccountId(resultSet.getInt("account_id"));
                savingAccount.setBalance(resultSet.getDouble("balance"));
                savingAccount.setAccountStatus(AccountStatus.valueOf(resultSet.getString("status").toUpperCase()));
                savingAccount.setDepositDate(resultSet.getObject("deposit_date", LocalDate.class));
                savingAccount.setOwner(userDao.getCustomerById(resultSet.getInt("user_id")));
                savingAccount.setTerm(resultSet.getInt("term_months"));
                savingAccount.setInterest(systemDao.getInterestTerm(savingAccount.getTerm()));
                savingAccount.setMaturityDate(resultSet.getObject("maturity_date", LocalDate.class));
                savingAccount.setInterestStrategy(new TermInterestStrategy());
                accountList.add(savingAccount);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return accountList;
    }

    public List<SavingAccount> getAllAccountSavingOfCustomer(int id){
        List<SavingAccount> accountList = new ArrayList<>();
        String query = "SELECT s.account_id, s.term_months, s.interest_rate, s.deposit_date, s.maturity_date, a.balance, a.user_id, a.status " +
                "FROM saving_account As s " +
                "JOIN account As a ON s.account_id = a.account_id " +
                "WHERE a.status = ? AND a.user_id =?";
        try(Connection connection = DBConnect.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)){

            preparedStatement.setString(1, "ACTIVE");
            preparedStatement.setInt(2, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                SavingAccount savingAccount = new SavingAccount();

                savingAccount.setAccountId(resultSet.getInt("account_id"));
                savingAccount.setBalance(resultSet.getDouble("balance"));
                savingAccount.setAccountStatus(AccountStatus.valueOf(resultSet.getString("status").toUpperCase()));
                savingAccount.setDepositDate(resultSet.getObject("deposit_date", LocalDate.class));
                savingAccount.setOwner(userDao.getCustomerById(resultSet.getInt("user_id")));
                savingAccount.setTerm(resultSet.getInt("term_months"));
                savingAccount.setInterest(systemDao.getInterestTerm(savingAccount.getTerm()));
                savingAccount.setMaturityDate(resultSet.getObject("maturity_date", LocalDate.class));
                accountList.add(savingAccount);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return accountList;
    }

    public void updateMonlyRequiredPayment(LoanAccount loanAccount){
        String query = "UPDATE loan_account SET monthly_required_payment = ? " +
                "WHERE account_id = ?";
        try(Connection connection = DBConnect.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setDouble(1, loanAccount.getMonthlyRequiredPayment()); //set tiền phải trả trong tháng, tài khoản sẽ tự tính đã khai báo trong cóstructor
            preparedStatement.setInt(2, loanAccount.getAccountId());

            preparedStatement.executeUpdate();
        }
        catch(SQLException e){
            e.printStackTrace();
        }

    }

    //danh sách những tài khoản vay đang actice của khách hàng
    public List<LoanAccount> getActiveLoanAccountsByCustomer(Customer customer) {
        List<LoanAccount> activeLoans = new ArrayList<>();

        String query = "SELECT account_id FROM account WHERE user_id = ? AND account_type = 'LOAN' AND status = 'ACTIVE'";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, customer.getUserId());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int accountId = rs.getInt("account_id");
                // Tái sử dụng hàm getAccountById hiện có để lấy full thông tin
                Account acc = getAccountById(accountId);
                if (acc instanceof LoanAccount) {
                    activeLoans.add((LoanAccount) acc);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return activeLoans;
    }
    //danh sách nhưững tài khoản đang active của khaách hàng
    public List<CheckingAccount> getActiveCheckingAccountsByCustomer(Customer customer) {
        List<CheckingAccount> activeCheckingAccounts = new ArrayList<>();
        // Truy vấn tài khoản THANH TOÁN đang HOẠT ĐỘNG
        String query = "SELECT account_id FROM account WHERE user_id = ? AND account_type = 'CHECKING' AND status = 'ACTIVE'";

        try (Connection connection = DBConnect.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, customer.getUserId());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int accountId = rs.getInt("account_id");

                Account acc = getAccountById(accountId);

                if (acc instanceof CheckingAccount) {
                    activeCheckingAccounts.add((CheckingAccount) acc);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return activeCheckingAccounts;
    }
    //cập nhật ngày tháng của tiết kiệm
    public void updateDateSavingAccount(SavingAccount savingAccount){
        String query = "UPDATE saving_account " +
                "SET deposit_date = ?, maturity_date =? " +
                "WHERE account_id = ?";
        try(Connection connection = DBConnect.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setObject(1, savingAccount.getDepositDate());
            preparedStatement.setObject(2, savingAccount.getMaturityDate());
            preparedStatement.setInt(3, savingAccount.getAccountId());

            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
    //khóa tài khoản
    public void lockAccount(Account account){
        String query = "UPDATE account " +
                "SET status = 'LOCKED' " +
                "WHERE account_id = ?";
        try(Connection connection = DBConnect.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setInt(1, account.getAccountId());
            preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }



}
