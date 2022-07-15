package com.akumine.smartclass.model;

public class Notification {

    public static final String TABLE_NOTIFICATION = "Notifications";
    public static final String TYPE = "type";
    public static final String FROM = "from";
    public static final String LOCATION_ID = "locationId";

    private String type;
    private String from;
    private String locationId;

    public Notification() {
    }

    public Notification(String type, String from, String locationId) {
        this.type = type;
        this.from = from;
        this.locationId = locationId;
    }

    public String getType() {
        return type;
    }

    public String getFrom() {
        return from;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }
}

