package model;

import other.TaxationType;

public class Driver {
    private int id;
    private String name;
    private String surname;
    private String address;
    private String fatherName;
    private String personalNr;
    private String idSerialNr;
    private TaxationType taxationType;
    private String bankNumber;

    @Override
    public String toString() {
        return "Driver{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", address='" + address + '\'' +
                ", fatherName='" + fatherName + '\'' +
                ", personalNr='" + personalNr + '\'' +
                ", idSerialNr='" + idSerialNr + '\'' +
                ", taxationType='" + taxationType + '\'' +
                ", bankNumber='" + bankNumber + '\'' +
                '}';
    }

    public Object[] toArray() {
        return new Object[]{
                id, name, surname, address, fatherName, personalNr, idSerialNr, taxationType, bankNumber
        };
    }

    public Driver(int id, String name, String surname, String address, String fatherName, String personalNr, String idSerialNr, TaxationType taxationType, String bankNumber) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.fatherName = fatherName;
        this.personalNr = personalNr;
        this.idSerialNr = idSerialNr;
        this.taxationType = taxationType;
        this.bankNumber = bankNumber;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public String getSurname() {
        return surname;
    }

    public String getAddress() {
        return address;
    }

    public String getFatherName() {
        return fatherName;
    }

    public String getPersonalNr() {
        return personalNr;
    }

    public String getIdSerialNr() {
        return idSerialNr;
    }

    public TaxationType getTaxationType() {
        return taxationType;
    }

    public String getBankNumber() {
        return bankNumber;
    }
}
