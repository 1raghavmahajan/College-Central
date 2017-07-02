package com.blackboxindia.TakeIT.dataModels;

import java.sql.Time;
import java.util.Date;

public class Event extends AdData {

    private Date date;
    private Time time;
    private String venue;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

}
