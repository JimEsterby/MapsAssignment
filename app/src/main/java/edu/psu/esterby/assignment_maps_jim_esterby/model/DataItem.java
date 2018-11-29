package edu.psu.esterby.assignment_maps_jim_esterby.model;

import java.io.Serializable;

public class DataItem implements Serializable {

    private String location;
    private Double latitude;
    private Double longitude;

    DataItem(String name, Double lat, Double lon) {
        location = name;
        latitude = lat;
        longitude = lon;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
