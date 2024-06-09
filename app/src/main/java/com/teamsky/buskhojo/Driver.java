package com.teamsky.buskhojo;

public class Driver {
    private String DriverBusName;
    private String DriverBusNumber;
    private String DriverName;
    private String DriverContact;
    private String BusSeats;

    // Default constructor (required for Firebase)
    public Driver() {
        // Default constructor required for calls to DataSnapshot.getValue(Driver.class)
    }

    public Driver(String driverBusName, String driverBusNumber, String driverName, String driverContact, String busSeats) {
        this.DriverBusName = driverBusName;
        this.DriverBusNumber = driverBusNumber;
        this.DriverName = driverName;
        this.DriverContact = driverContact;
        this.BusSeats = busSeats;
    }

    // Getter and Setter methods for each property
    public String getDriverBusName() {
        return DriverBusName;
    }

    public void setDriverBusName(String driverBusName) {
        this.DriverBusName = driverBusName;
    }

    public String getDriverBusNumber() {
        return DriverBusNumber;
    }

    public void setDriverBusNumber(String driverBusNumber) {
        this.DriverBusNumber = driverBusNumber;
    }

    public String getDriverName() {
        return DriverName;
    }

    public void setDriverName(String driverName) {
        this.DriverName = driverName;
    }

    public String getDriverContact() {
        return DriverContact;
    }

    public void setDriverContact(String driverContact) {
        this.DriverContact = driverContact;
    }

    public String getBusSeats() {
        return BusSeats;
    }

    public void setBusSeats(String busSeats) {
        this.BusSeats = busSeats;
    }
}
