package com.g_ara.garaapp;

/**
 * Created by ahmedengu on 6/13/2016.
 */

public class DispatchResult {
    private String ID, name, username, pic, longitude, latitude, DistLatitude, DistLongitude, carModelID, availableSeats, frontPic, carid,phoneNumber;

    public DispatchResult() {
    }

    public DispatchResult(String name) {
        this.name = name;
        pic = "http://www.g-ara.com/assets/images/team/2.jpg";
    }

    public DispatchResult(String ID, String name, String username, String pic, String longitude, String latitude, String distLatitude, String distLongitude, String carModelID, String availableSeats, String frontPic, String carid,String phoneNumber) {
        this.ID = ID;
        this.name = name;
        this.username = username;
        this.pic = pic;
        this.longitude = longitude;
        this.latitude = latitude;
        DistLatitude = distLatitude;
        DistLongitude = distLongitude;
        this.carModelID = carModelID;
        this.availableSeats = availableSeats;
        this.frontPic = frontPic;
        this.carid = carid;
        this.phoneNumber = phoneNumber;

        if (this.pic.equals("null")) this.pic = "http://www.g-ara.com/assets/images/team/2.jpg";

    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDistLatitude() {
        return DistLatitude;
    }

    public void setDistLatitude(String distLatitude) {
        DistLatitude = distLatitude;
    }

    public String getDistLongitude() {
        return DistLongitude;
    }

    public void setDistLongitude(String distLongitude) {
        DistLongitude = distLongitude;
    }

    public String getCarModelID() {
        return carModelID;
    }

    public void setCarModelID(String carModelID) {
        this.carModelID = carModelID;
    }

    public String getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(String availableSeats) {
        this.availableSeats = availableSeats;
    }

    public String getFrontPic() {
        return frontPic;
    }

    public void setFrontPic(String frontPic) {
        this.frontPic = frontPic;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getCarid() {
        return carid;
    }

    public void setCarid(String carid) {
        this.carid = carid;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}