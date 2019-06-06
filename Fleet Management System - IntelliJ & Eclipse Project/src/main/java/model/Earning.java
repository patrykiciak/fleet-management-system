package model;

import java.math.BigDecimal;
import java.sql.Date;

public class Earning {
    private int id;
    private int driverId;
    private Date startDate;
    private Date endDate;
    private BigDecimal turnover;
    private boolean isPaid;

    public Earning(int id, int driverId, Date startDate, Date endDate, BigDecimal turnover, boolean isPaid) {
        this.id = id;
        this.driverId = driverId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.turnover = turnover;
        this.isPaid = isPaid;
    }

    public Object[] toArray() {
        return new Object[] {
                id,
                driverId,
                startDate,
                endDate,
                turnover,
                isPaid
        };
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDriverId() {
        return driverId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public BigDecimal getTurnover() {
        return turnover;
    }

    public boolean isPaid() {
        return isPaid;
    }
}
