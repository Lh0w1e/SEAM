package com.app.seam.Model;

/**
 * Created by Colinares on 4/1/2018.
 */

public class Event {

    public String e_id, e_name, e_date, e_time, time_created, date_created;

    public Event() {
    }

    public Event(String e_id, String e_name, String e_date, String e_time, String time_created, String date_created) {
        this.e_id = e_id;
        this.e_name = e_name;
        this.e_date = e_date;
        this.e_time = e_time;
        this.time_created = time_created;
        this.date_created = date_created;
    }

    public String getE_id() {
        return e_id;
    }

    public void setE_id(String e_id) {
        this.e_id = e_id;
    }

    public String getE_name() {
        return e_name;
    }

    public void setE_name(String e_name) {
        this.e_name = e_name;
    }

    public String getE_date() {
        return e_date;
    }

    public void setE_date(String e_date) {
        this.e_date = e_date;
    }

    public String getE_time() {
        return e_time;
    }

    public void setE_time(String e_time) {
        this.e_time = e_time;
    }

    public String getTime_created() {
        return time_created;
    }

    public void setTime_created(String time_created) {
        this.time_created = time_created;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }
}
