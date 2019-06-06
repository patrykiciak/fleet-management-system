package controller;

import dao.CarDao;
import model.Car;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;

public class CarController {
    private static CarController carController = null;
    private final CarDao carDao = new CarDao();

    private CarController() {}

    public static synchronized CarController getInstance() {
        if(carController == null) {
            carController = new CarController();
        }
        return carController;
    }

    public Car create(String carManufacturer, String model, Date year, String numberPlate, String insuranceNr, Date technicalExpirationDate, Date insuranceExpirationDate) throws SQLException {
        return carDao.create(carManufacturer, model, year, numberPlate, insuranceNr, technicalExpirationDate, insuranceExpirationDate);
    }

    public Car readOne(int id) throws SQLException {
        return carDao.readOne(id);
    }

    public ArrayList<Car> readAll() throws SQLException {
        return carDao.readAll();
    }

    public Car update(int id, String carManufacturer, String model, Date year, String numberPlate, String insuranceNr, Date technicalExpirationDate, Date insuranceExpirationDate) throws SQLException {
        return carDao.update(id, carManufacturer, model, year, numberPlate, insuranceNr, technicalExpirationDate, insuranceExpirationDate);
    }

    public Car delete(int id) throws SQLException {
        return carDao.delete(id);
    }

    public ArrayList<String> getColumns() throws SQLException {
        return carDao.getColumns();
    }
}
