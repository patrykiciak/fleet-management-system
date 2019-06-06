package dao.interfaces;

import model.Earning;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;

public interface EarningDaoInterface {
    Earning create(int driverId, Date startDate, Date endDate, BigDecimal turnover, boolean isPaid) throws SQLException;
    ArrayList<Earning> readAll() throws SQLException;
    Earning readOne(int id) throws SQLException;
    Earning update(int id, int driverId, Date startDate, Date endDate, BigDecimal turnover, boolean isPaid) throws SQLException;
    Earning delete(int id) throws SQLException;
    ArrayList<String> getColumns() throws SQLException;
}
