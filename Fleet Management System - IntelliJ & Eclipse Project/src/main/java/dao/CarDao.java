package dao;

import dao.interfaces.CarDaoInterface;
import model.Car;
import other.DbConnection;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CarDao implements CarDaoInterface {

    public Car create(String carManufacturer, String model, Date year, String numberPlate, String insuranceNr, Date technicalExpirationDate, Date insuranceExpirationDate) throws SQLException {

        Statement statement = DbConnection.getConnection().createStatement();
        boolean isCreated = statement.executeUpdate(
                "insert into Car" +
                        "(carManufacturer, model, year, numberPlate, insuranceNr, technicalExpirationDate, insuranceExpirationDate) " +
                        "values ('" + carManufacturer +
                        "', '" + model +
                        "', '" + year +
                        "', '" + numberPlate +
                        "', '" + insuranceNr +
                        "', '" + technicalExpirationDate +
                        "', '" + insuranceExpirationDate + "');"
        ) > 0;

        Car car = null;

        if (isCreated) {
            ResultSet resultSet = statement.executeQuery(
                    "select id from Car where carManufacturer='" + carManufacturer +
                            "' and model='" + model +
                            "' and year='" + year +
                            "' and numberPlate='" + numberPlate +
                            "' and insuranceNr='" + insuranceNr +
                            "' and technicalExpirationDate='" + technicalExpirationDate +
                            "' and insuranceExpirationDate='" + insuranceExpirationDate + "';"
            );

            while (resultSet.next()) {
                car = new Car(
                        resultSet.getInt("id"),
                        carManufacturer, model, year, numberPlate, insuranceNr, technicalExpirationDate, insuranceExpirationDate
                );
            }
            resultSet.close();
        }
        statement.close();
        return car;
    }

    public ArrayList<Car> readAll() throws SQLException {
        ArrayList<Car> cars = new ArrayList<>();
        Statement statement = DbConnection.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery("select * from Car;");

        while (resultSet.next()) {
            cars.add(new Car(
                    resultSet.getInt("id"),
                    resultSet.getString("carManufacturer"),
                    resultSet.getString("model"),
                    resultSet.getDate("year"),
                    resultSet.getString("numberPlate"),
                    resultSet.getString("insuranceNr"),
                    resultSet.getDate("technicalExpirationDate"),
                    resultSet.getDate("insuranceExpirationDate")
            ));
        }
        resultSet.close();
        statement.close();
        return cars;
    }

    public Car readOne(int id) throws SQLException {
        Statement statement = DbConnection.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery("select * from Car where id='" + id + "';");

        Car car = null;
        while (resultSet.next()) {
            car = new Car(
                    resultSet.getInt("id"),
                    resultSet.getString("carManufacturer"),
                    resultSet.getString("model"),
                    resultSet.getDate("year"),
                    resultSet.getString("numberPlate"),
                    resultSet.getString("insuranceNr"),
                    resultSet.getDate("technicalExpirationDate"),
                    resultSet.getDate("insuranceExpirationDate")
            );
        }

        resultSet.close();
        resultSet.close();
        return car;
    }

    public Car update(int id, String carManufacturer, String model, Date year, String numberPlate, String insuranceNr, Date technicalExpirationDate, Date insuranceExpirationDate) throws SQLException {
        Statement statement = DbConnection.getConnection().createStatement();
        boolean isUpdated = statement.executeUpdate("update Car set " +
                "carManufacturer='" + carManufacturer +
                "', model= '" + model +
                "', year= '" + year +
                "', numberPlate= '" + numberPlate +
                "', insuranceNr= '" + insuranceNr +
                "', technicalExpirationDate= '" + technicalExpirationDate +
                "', insuranceExpirationDate= '" + insuranceExpirationDate +
                "' where id='" + id + "';")
                > 0;

        Car car = null;

        if (isUpdated) {
            car = new Car(id, carManufacturer, model, year, numberPlate, insuranceNr, technicalExpirationDate, insuranceExpirationDate);
        }

        statement.close();
        return car;
    }

    public Car delete(int id) throws SQLException {
        Statement statement = DbConnection.getConnection().createStatement();

        Car car = readOne(id);

        boolean isDeleted = statement.executeUpdate("delete from Car where id='" + id + "';") > 0;

        if (!isDeleted) {
            car = null;
        }

        statement.close();
        return car;
    }

    public ArrayList<String> getColumns() throws SQLException {
        Statement statement = DbConnection.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery("select column_name from information_schema.columns where table_name='Car';");

        ArrayList<String> columns = new ArrayList<>();
        while (resultSet.next()) {
            columns.add(resultSet.getString("column_name"));
        }

        resultSet.close();
        statement.close();
        return columns;
    }
}