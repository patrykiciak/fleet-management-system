package model;

import java.sql.Date;

public class Car {
    private int id;
    private String carManufacturer;
    private String model;
    private Date year;
    private String numberPlate;
    private String insuranceNr;
    private Date technicalExpirationDate;
    private Date insuranceExpirationDate;

    public Car(int id, String carManufacturer, String model, Date year, String numberPlate, String insuranceNr, Date technicalExpirationDate, Date insuranceExpirationDate) {
        this.id = id;
        this.carManufacturer = carManufacturer;
        this.model = model;
        this.year = year;
        this.numberPlate = numberPlate;
        this.insuranceNr = insuranceNr;
        this.technicalExpirationDate = technicalExpirationDate;
        this.insuranceExpirationDate = insuranceExpirationDate;
    }

    public Object[] toArray() {
        return new Object[]{
                id, carManufacturer, model, year, numberPlate, insuranceNr, technicalExpirationDate, insuranceExpirationDate
        };
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCarManufacturer() {
        return carManufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Date getYear() {
        return year;
    }

    public String getNumberPlate() {
        return numberPlate;
    }

    public String getInsuranceNr() {
        return insuranceNr;
    }


    public Date getTechnicalExpirationDate() {
        return technicalExpirationDate;
    }

    public Date getInsuranceExpirationDate() {
        return insuranceExpirationDate;
    }
}
