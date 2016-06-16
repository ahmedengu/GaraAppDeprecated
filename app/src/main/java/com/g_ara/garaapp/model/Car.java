package com.g_ara.garaapp.model;

/**
 * Created by ahmedengu on 6/13/2016.
 */
public class Car {

    String ID,driverID,plateNumber,platePic,carModelID,frontPic,backPic,sidePic,insidePic,licenseNumber,licensePic,licenseExpireDate,DistLongitude,DistLatitude,availableSeats,state;

    public Car(String ID, String driverID, String plateNumber, String platePic, String carModelID, String frontPic, String backPic, String sidePic, String insidePic, String licenseNumber, String licensePic, String licenseExpireDate, String distLongitude, String distLatitude, String availableSeats, String state) {
        this.ID = ID;
        this.driverID = driverID;
        this.plateNumber = plateNumber;
        this.platePic = platePic;
        this.carModelID = carModelID;
        this.frontPic = frontPic;
        this.backPic = backPic;
        this.sidePic = sidePic;
        this.insidePic = insidePic;
        this.licenseNumber = licenseNumber;
        this.licensePic = licensePic;
        this.licenseExpireDate = licenseExpireDate;
        DistLongitude = distLongitude;
        DistLatitude = distLatitude;
        this.availableSeats = availableSeats;
        this.state = state;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getDriverID() {
        return driverID;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getPlatePic() {
        return platePic;
    }

    public void setPlatePic(String platePic) {
        this.platePic = platePic;
    }

    public String getCarModelID() {
        return carModelID;
    }

    public void setCarModelID(String carModelID) {
        this.carModelID = carModelID;
    }

    public String getFrontPic() {
        return frontPic;
    }

    public void setFrontPic(String frontPic) {
        this.frontPic = frontPic;
    }

    public String getBackPic() {
        return backPic;
    }

    public void setBackPic(String backPic) {
        this.backPic = backPic;
    }

    public String getSidePic() {
        return sidePic;
    }

    public void setSidePic(String sidePic) {
        this.sidePic = sidePic;
    }

    public String getInsidePic() {
        return insidePic;
    }

    public void setInsidePic(String insidePic) {
        this.insidePic = insidePic;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getLicensePic() {
        return licensePic;
    }

    public void setLicensePic(String licensePic) {
        this.licensePic = licensePic;
    }

    public String getLicenseExpireDate() {
        return licenseExpireDate;
    }

    public void setLicenseExpireDate(String licenseExpireDate) {
        this.licenseExpireDate = licenseExpireDate;
    }

    public String getDistLongitude() {
        return DistLongitude;
    }

    public void setDistLongitude(String distLongitude) {
        DistLongitude = distLongitude;
    }

    public String getDistLatitude() {
        return DistLatitude;
    }

    public void setDistLatitude(String distLatitude) {
        DistLatitude = distLatitude;
    }

    public String getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(String availableSeats) {
        this.availableSeats = availableSeats;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
