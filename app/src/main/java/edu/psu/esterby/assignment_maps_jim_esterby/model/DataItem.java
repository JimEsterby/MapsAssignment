package edu.psu.esterby.assignment_maps_jim_esterby.model;

import java.io.Serializable;

public class DataItem implements Serializable {

    private String Location;
    private Double Latitude;
    private Double Longitude;

    public DataItem() {}

    public DataItem(Double lat, String name, Double lon) {
        this.Location = name;
        this.Latitude = lat;
        this.Longitude = lon;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        this.Location = location;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        this.Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        this.Longitude = longitude;
    }
}
