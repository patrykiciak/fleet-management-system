package dao.interfaces;

import model.Driver;
import other.TaxationType;

import java.sql.SQLException;
import java.util.ArrayList;

public interface DriverDaoInterface {
    Driver create(String name, String surname, String address, String fatherName, String personalNr, String idSerialNr, String taxationType, String bankNumber) throws SQLException;
    ArrayList<Driver> readAll() throws SQLException;
    Driver readOne(int id) throws SQLException;
    Driver update(int id, String name, String surname, String address, String fatherName, String personalNr, String idSerialNr, TaxationType taxationType, String bankNumber) throws SQLException;
    Driver delete(int id) throws SQLException;
    ArrayList<String> getColumns() throws SQLException;
}