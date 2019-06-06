package dao.interfaces;

import model.Car;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;

public interface CarDaoInterface {
    Car create (String carManufacturer, String model, Date year, String numberPlate, String insuranceNr, Date technicalExpirationDate, Date insuranceExpirationDate) throws SQLException;
    ArrayList<Car> readAll() throws SQLException;
    Car readOne(int id) throws SQLException;
    Car update(int id, String carManufacturer, String model, Date year, String numberPlate, String insuranceNr, Date technicalExpirationDate, Date insuranceExpirationDate) throws SQLException;
    Car delete(int id) throws SQLException;
    ArrayList<String> getColumns() throws  SQLException;
}
