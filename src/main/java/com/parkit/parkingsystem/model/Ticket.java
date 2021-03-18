package com.parkit.parkingsystem.model;

import java.util.Date;

public final class Ticket {
    private int id;
    private ParkingSpot parkingSpot;
    private String vehicleRegNumber;
    private double price;
    private Date inTime;
    private Date outTime;
    private boolean isPromo; // discount rate eligibility

    public Ticket() {
        this.isPromo = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    public void setParkingSpot(final ParkingSpot parkingSpot) {
        this.parkingSpot = parkingSpot;
    }

    public String getVehicleRegNumber() {
        return vehicleRegNumber;
    }

    public void setVehicleRegNumber(final String vehicleRegNumber) {
        this.vehicleRegNumber = vehicleRegNumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getInTime() {
        return inTime == null ? null : new Date(inTime.getTime());
    }

    public void setInTime(final Date inTime) {
        this.inTime = inTime == null ? null : new Date(inTime.getTime());
    }

    public Date getOutTime() {
        return outTime == null ? null : new Date(outTime.getTime());
    }

    public void setOutTime(final Date outTime) {
        this.outTime = outTime == null ? null : new Date(outTime.getTime());
    }

    public boolean isPromo() {
        return isPromo;
    }

    public void setPromo(boolean promo) {
        isPromo = promo;
    }
}
