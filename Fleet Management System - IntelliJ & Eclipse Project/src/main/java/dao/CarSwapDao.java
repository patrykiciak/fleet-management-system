package dao;

import dao.interfaces.CarSwapDaoInterface;
import model.CarSwap;
import other.DbConnection;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CarSwapDao implements CarSwapDaoInterface {
    public CarSwap create(Date startDate, Date endDate, int carId, int previousDriverId, int currentDriverId) throws SQLException {

        Statement statement = DbConnection.getConnection().createStatement();
        boolean isCreated = statement.executeUpdate(
                "insert into CarSwap" +
                        "(startDate, endDate, carId, previousDriverId, currentDriverId) " +
                        "values ('" + startDate +
                        "', '" + endDate +
                        "', '" + carId +
                        "', '" + previousDriverId +
                        "', '" + currentDriverId + "');"
        ) > 0;

        CarSwap carSwap = null;

        if(isCreated) {
            ResultSet resultSet = statement.executeQuery(
                    "select * from CarSwap where startDate='" + startDate +
                            "' and endDate='" + endDate +
                            "' and carId='" + carId +
                            "' and previousDriverId='" + previousDriverId +
                            "' and currentDriverId='" + currentDriverId + "';"
            );

            while(resultSet.next()) {
                carSwap = new CarSwap(
                        resultSet.getInt("id"),
                        resultSet.getDate("startDate"),
                        resultSet.getDate("endDate"),
                        resultSet.getInt("carId"),
                        resultSet.getInt("previousDriverId"),
                        resultSet.getInt("currentDriverId"));
            }
            resultSet.close();
        }
        statement.close();
        return carSwap;
    }

    public ArrayList<CarSwap> readAll() throws SQLException {
        ArrayList<CarSwap> carSwap = new ArrayList<>();
        Statement statement = DbConnection.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery("select * from CarSwap;");

        while (resultSet.next()) {
            carSwap.add(new CarSwap(
                    resultSet.getInt("id"),
                    resultSet.getDate("startDate"),
                    resultSet.getDate("endDate"),
                    resultSet.getInt("carId"),
                    resultSet.getInt("previousDriverId"),
                    resultSet.getInt("currentDriverId")
            ));
        }
        resultSet.close();
        statement.close();
        return carSwap;
    }

    public CarSwap readOne(int id) throws SQLException {
        Statement statement = DbConnection.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery("select * from CarSwap where id='" + id + "';");

        CarSwap carSwap = null;
        while(resultSet.next()) {
            carSwap = new CarSwap (
                    resultSet.getInt("id"),
                    resultSet.getDate("startDate"),
                    resultSet.getDate("endDate"),
                    resultSet.getInt("carId"),
                    resultSet.getInt("previousDriverId"),
                    resultSet.getInt("currentDriverId")
            );
        }

        resultSet.close();
        resultSet.close();
        return carSwap;
    }

    public CarSwap update(int id, Date startDate, Date endDate, int carId, int previousDriverId, int currentDriverId) throws SQLException {
        Statement statement = DbConnection.getConnection().createStatement();
        boolean isUpdated = statement.executeUpdate("update CarSwap set " +
                "startDate='" + startDate +
                "', endDate= '" + endDate +
                "', carId= '" + carId +
                "', previousDriverId= '" + previousDriverId +
                "', currentDriverId= '" + currentDriverId +
                "' where id = '" + id + "';")
                > 0;

        CarSwap carSwap = null;

        if (isUpdated) {
            carSwap = new CarSwap(id, startDate, endDate, carId, previousDriverId, currentDriverId);
        }

        statement.close();
        return carSwap;
    }

    public CarSwap delete(int id) throws SQLException {
        Statement statement = DbConnection.getConnection().createStatement();

        CarSwap carSwap = readOne(id);
        boolean isDeleted = statement.executeUpdate("delete from CarSwap where id='" + id + "';") > 0;

        if (!isDeleted) {
            carSwap = null;
        }

        statement.close();
        return carSwap;
    }

    public ArrayList<String> getColumns() throws SQLException {
        Statement statement = DbConnection.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery("select column_name from information_schema.columns where table_name='CarSwap';");

        ArrayList<String> columns = new ArrayList<>();
        while (resultSet.next()) {
            columns.add(resultSet.getString("column_name"));
        }

        resultSet.close();
        statement.close();
        return columns;
    }
}
