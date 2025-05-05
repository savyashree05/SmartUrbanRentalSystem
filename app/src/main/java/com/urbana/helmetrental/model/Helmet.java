package com.urbana.helmetrental.model;

public class Helmet {
    private long id;
    private String code;
    private boolean available;
    private long locationId;

    public Helmet(long id, String code, boolean available, long locationId) {
        this.id = id;
        this.code = code;
        this.available = available;
        this.locationId = locationId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }
}
