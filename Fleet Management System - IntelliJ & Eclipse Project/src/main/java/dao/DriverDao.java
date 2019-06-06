package dao;

import dao.interfaces.DriverDaoInterface;
import model.Driver;
import other.DbConnection;
import other.TaxationType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DriverDao implements DriverDaoInterface {

    public Driver create(String name, String surname, String address, String fatherName, String personalNr, String idSerialNr, String taxationType, String bankNumber) throws SQLException {
        Statement statement = DbConnection.getConnection().createStatement();
        boolean isCreated = statement.executeUpdate(
                "insert into Driver" +
                        "(name, surname, address, fatherName, personalNr, idSerialNr, taxationType, bankNumber) " +
                        "values ('" + name +
                        "', '" + surname +
                        "', '" + address +
                        "', '" + fatherName +
                        "', '" + personalNr +
                        "', '" + idSerialNr +
                        "', '" + taxationType +
                        "', '" + bankNumber + "');"
        ) > 0;

        Driver driver = null;

        if(isCreated) {
            ResultSet resultSet = statement.executeQuery(
                    "select * from Driver where name='" + name +
                            "' and surname='" + surname +
                            "' and address='" + address +
                            "' and fatherName='" + fatherName +
                            "' and personalNr='" + personalNr +
                            "' and idSerialNr='" + idSerialNr +
                            "' and taxationType='" + taxationType +
                            "' and bankNumber='" + bankNumber + "';"
            );

            while(resultSet.next()) {
                driver = new Driver(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("surname"),
                        resultSet.getString("address"),
                        resultSet.getString("fatherName"),
                        resultSet.getString("personalNr"),
                        resultSet.getString("idSerialNr"),
                        TaxationType.getFromLabel(resultSet.getString("taxationType")),
                        resultSet.getString("bankNumber")
                );
            }
            resultSet.close();
        }
        statement.close();
        return driver;
    }

    public ArrayList<Driver> readAll() throws SQLException {
        ArrayList<Driver> drivers = new ArrayList<>();
        Statement statement = DbConnection.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery("select * from Driver;");

        while(resultSet.next()) {
            drivers.add(new Driver(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getString("surname"),
                    resultSet.getString("address"),
                    resultSet.getString("fatherName"),
                    resultSet.getString("personalNr"),
                    resultSet.getString("idSerialNr"),
                    TaxationType.getFromLabel(resultSet.getString("taxationType")),
                    resultSet.getString("bankNumber")
            ));
        }

        resultSet.close();
        statement.close();
        return drivers;
    }

    public Driver readOne(int id) throws SQLException {
        Statement statement = DbConnection.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery("select * from Driver where id='" + id + "';");

        Driver driver = null;
        while(resultSet.next()) {
            driver = new Driver(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getString("surname"),
                    resultSet.getString("address"),
                    resultSet.getString("fatherName"),
                    resultSet.getString("personalNr"),
                    resultSet.getString("idSerialNr"),
                    TaxationType.valueOf(resultSet.getString("taxationType")),
                    resultSet.getString("bankNumber")
            );
        }

        resultSet.close();
        statement.close();
        return driver;
    }

    public Driver update(int id, String name, String surname, String address, String fatherName, String personalNr, String idSerialNr, TaxationType taxationType, String bankNumber) throws SQLException {
        Statement statement = DbConnection.getConnection().createStatement();
        boolean isUpdated = statement.executeUpdate("update Driver set " +
                "name='" + name +
                "', surname='" + surname +
                "', address='" + address +
                "', fatherName='" + fatherName +
                "', personalNr='" + personalNr +
                "', idSerialNr='" + idSerialNr +
                "', taxationType='" + taxationType +
                "', bankNumber='" + bankNumber +
                "' where id='" + id + "';")
                > 0;

        Driver driver = null;

        if(isUpdated) {
            driver = new Driver(id, name, surname, address, fatherName, personalNr, idSerialNr, taxationType, bankNumber);
        }

        statement.close();
        return driver;
    }

    public Driver delete(int id) throws SQLException {
        Statement statement = DbConnection.getConnection().createStatement();

        Driver driver = readOne(id);
        boolean isDeleted = statement.executeUpdate("delete from Driver where id='" + id + "';") > 0;

        if(!isDeleted) {
            driver = null;
        }

        statement.close();
        return driver;
    }

    public ArrayList<String> getColumns() throws SQLException {
        Statement statement = DbConnection.getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery("select column_name from information_schema.columns where table_name='Driver';");

        ArrayList<String> columns = new ArrayList<>();
        while (resultSet.next()) {
            columns.add(resultSet.getString("column_name"));
        }

        resultSet.close();
        statement.close();
        return columns;
    }
}