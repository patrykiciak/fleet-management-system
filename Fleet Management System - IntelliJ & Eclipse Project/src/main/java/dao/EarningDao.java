package dao;

import dao.interfaces.EarningDaoInterface;
import model.Earning;
import other.DbConnection;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class EarningDao implements EarningDaoInterface {
    public Earning create(int driverId, Date startDate, Date endDate, BigDecimal turnover, boolean isPaid) throws SQLException {
        Statement statement = DbConnection.getConnection().createStatement();
        boolean isCreated = statement.executeUpdate(
                "insert into Earning" +
                        "(driverId, startDate, endDate, turnover, isPaid) " +
                        "values ('" + driverId +
                        "', '" + startDate +
                        "', '" + endDate +
                        "', '" + turnover +
                        "', '" + isPaid + "');"
        ) > 0;

        Earning earning = null;

        if(isCreated) {
            ResultSet resultSet = statement.executeQuery(
                    "select * from Earning where driverId='" + driverId +
                            "' and startDate='" + startDate +
                            "' and endDate='" + endDate +
                            "' and turnover='" + turnover +
                            "' and isPaid='" + isPaid + "';"
            );

            while(resultSet.next()) {
                earning = new Earning(
                        resultSet.getInt("id"),
                        resultSet.getInt("driverId"),
                        resultSet.getDate("startDate"),
                        resultSet.getDate("endDate"),
                        resultSet.getBigDecimal("turnover"),
                        resultSet.getBoolean("isPaid")
                );
            }
            resultSet.close();
        }
        statement.close();
        return earning;
    }

    public ArrayList<Earning> readAll() throws SQLException {
        ArrayList<Earning> earnings = new ArrayList<>();
        Statement statement = DbConnection.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery("select * from Earning;");

        while(resultSet.next()) {
            earnings.add(new Earning(
                    resultSet.getInt("id"),
                    resultSet.getInt("driverId"),
                    resultSet.getDate("startDate"),
                    resultSet.getDate("endDate"),
                    resultSet.getBigDecimal("turnover"),
                    resultSet.getBoolean("isPaid")
            ));
        }

        resultSet.close();
        statement.close();
        return earnings;
    }

    public Earning readOne(int id) throws SQLException {
        Statement statement = DbConnection.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery("select * from Earning where id='" + id + "';");

        Earning earnings = null;
        while(resultSet.next()) {
            earnings = new Earning(
                    resultSet.getInt("id"),
                    resultSet.getInt("driverId"),
                    resultSet.getDate("startDate"),
                    resultSet.getDate("endDate"),
                    resultSet.getBigDecimal("turnover"),
                    resultSet.getBoolean("isPaid")
            );
        }

        resultSet.close();
        statement.close();
        return earnings;
    }

    public Earning update(int id, int driverId, Date startDate, Date endDate, BigDecimal turnover, boolean isPaid) throws SQLException {
        Statement statement = DbConnection.getConnection().createStatement();
        boolean isUpdated = statement.executeUpdate("update Earning set " +
                "driverId='" + driverId +
                "', startDate='" + startDate +
                "', endDate='" + endDate +
                "', turnover='" + turnover +
                "', isPaid='" + isPaid +
                "' where id='" + id + "';")
                > 0;

        Earning earning = null;

        if(isUpdated) {
            earning = new Earning(id, driverId, startDate, endDate, turnover, isPaid);
        }

        statement.close();
        return earning;
    }

    public Earning delete(int id) throws SQLException {
        Statement statement = DbConnection.getConnection().createStatement();

        Earning earnings = readOne(id);
        boolean isDeleted = statement.executeUpdate("delete from Earning where id='" + id + "';") > 0;

        if(!isDeleted) {
            earnings = null;
        }

        statement.close();
        return earnings;
    }

    public ArrayList<String> getColumns() throws SQLException {
        Statement statement = DbConnection.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery("select column_name from information_schema.columns where table_name='Earning';");

        ArrayList<String> columns = new ArrayList<>();
        while (resultSet.next()) {
            columns.add(resultSet.getString("column_name"));
        }

        resultSet.close();
        statement.close();
        return columns;
    }
}
