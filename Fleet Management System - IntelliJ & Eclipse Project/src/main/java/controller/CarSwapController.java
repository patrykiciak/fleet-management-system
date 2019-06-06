package controller;

import dao.CarSwapDao;
import model.CarSwap;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;

public class CarSwapController {
    private static CarSwapController carSwapController = null;
    private final CarSwapDao carSwapDao = new CarSwapDao();

    private CarSwapController() {}

    public static synchronized CarSwapController getInstance() {
        if(carSwapController == null) {
            carSwapController = new CarSwapController();
        }
        return carSwapController;
    }

    public CarSwap create(Date startDate, Date endDate, int carId, int previousDriverId, int currentDriverId) throws SQLException {
        return carSwapDao.create(startDate, endDate, carId, previousDriverId, currentDriverId);
    }

    public CarSwap readOne(int id) throws SQLException {
        return carSwapDao.readOne(id);
    }

    public ArrayList<CarSwap> readAll() throws SQLException {
        return carSwapDao.readAll();
    }

    public CarSwap update(int id, Date beginningDate, Date endDate, int carId, int previousDriverId, int currentDriverId) throws SQLException {
        return carSwapDao.update(id, beginningDate, endDate, carId, previousDriverId, currentDriverId);
    }

    public CarSwap delete(int id) throws SQLException {
        return carSwapDao.delete(id);
    }

    public ArrayList<String> getColumns() throws SQLException {
        return carSwapDao.getColumns();
    }
}
