package com.example.explorer.creation_android;

import java.security.acl.LastOwnerException;

/**
 * Created by explorer on 15-11-24.
 */
public class Data {
    private double PM = 0;
    private double latitude = 0;
    private double longitude = 0;

    public void setPM(double PM) {
        this.PM = PM;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getPM() {
        return PM;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
