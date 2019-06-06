package controller;

import dao.DriverDao;
import model.Driver;
import other.TaxationType;

import java.sql.SQLException;
import java.util.ArrayList;

public class DriverController {
    private static DriverController driverController = null;
    private final DriverDao driverDao = new DriverDao();

    private DriverController() {}

    public static synchronized DriverController getInstance() {
        if(driverController == null) {
            driverController = new DriverController();
        }
        return driverController;
    }

    public Driver create(String name, String surname, String address,
                         String fatherName, String personalNr,
                         String idSerialNr, String taxationType,
                         String bankNumber) throws SQLException {

        return driverDao.create(name, surname, address, fatherName,
                personalNr, idSerialNr, taxationType, bankNumber);
    }

    public Driver readOne(int id) throws SQLException {
        return driverDao.readOne(id);
    }

    public ArrayList<Driver> readAll() throws SQLException {
        return driverDao.readAll();
    }

    public Driver update(int id, String name, String surname, String address, String fatherName, String personalNr, String idSerialNr, TaxationType taxationType, String bankNumber) throws SQLException {
        return driverDao.update(id, name, surname, address, fatherName, personalNr, idSerialNr, taxationType, bankNumber);
    }

    public Driver delete(int id) throws SQLException {
        return driverDao.delete(id);
    }

    public ArrayList<String> getColumns() throws SQLException {
        return driverDao.getColumns();
    }
}
