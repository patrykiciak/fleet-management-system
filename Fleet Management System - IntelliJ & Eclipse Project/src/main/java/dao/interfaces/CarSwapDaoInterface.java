package dao.interfaces;

import model.CarSwap;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;

public interface CarSwapDaoInterface {
    CarSwap create(Date startDate, Date endDate, int carId, int previousDriverId, int currentDriverId) throws SQLException;
    ArrayList<CarSwap> readAll() throws SQLException;
    CarSwap readOne(int id) throws SQLException;
    CarSwap update(int id, Date startDate, Date endDate, int carId, int previousDriverId, int currentDriverId) throws SQLException;
    CarSwap delete(int id) throws SQLException;
    ArrayList<String> getColumns() throws SQLException;
}
