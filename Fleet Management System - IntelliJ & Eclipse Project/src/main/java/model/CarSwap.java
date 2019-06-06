package model;

import java.sql.Date;

public class CarSwap {
    private int id;
    private Date startDate;
    private Date endDate;
    private int carId;
    private int previousDriverId;
    private int currentDriverId;

    public CarSwap(int id, Date startDate, Date endDate, int carId, int previousDriverId, int currentDriverId) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.carId = carId;
        this.previousDriverId = previousDriverId;
        this.currentDriverId = currentDriverId;
    }

    public Object[] toArray() {
        return new Object[]{
                id, startDate, endDate, carId, previousDriverId, currentDriverId
        };
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public int getCarId() {
        return carId;
    }

    public int getPreviousDriverId() {
        return previousDriverId;
    }

    public int getCurrentDriverId() {
        return currentDriverId;
    }
}
