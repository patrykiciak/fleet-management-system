package controller;

import dao.EarningDao;
import model.Earning;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;

public class EarningController {
    private static EarningController earningController = null;
    private final EarningDao earningDao = new EarningDao();

    private EarningController() {}

    public static synchronized EarningController getInstance() {
        if(earningController == null) {
            earningController = new EarningController();
        }
        return earningController;
    }

    public Earning create(int driverId, Date startDate, Date endDate, BigDecimal turnover, boolean isPaid) throws SQLException {
        return earningDao.create(driverId, startDate, endDate, turnover, isPaid);
    }

    public Earning readOne(int id) throws SQLException {
        return earningDao.readOne(id);
    }

    public ArrayList<Earning> readAll() throws SQLException {
        return earningDao.readAll();
    }

    public Earning update(int id, int driverId, Date startDate, Date endDate, BigDecimal turnover, boolean isPaid) throws SQLException {
        return earningDao.update(id, driverId, startDate, endDate, turnover, isPaid);
    }

    public Earning delete(int id) throws SQLException {
        return earningDao.delete(id);
    }

    public ArrayList<String> getColumns() throws SQLException {
        return earningDao.getColumns();
    }
}
