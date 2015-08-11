package com.iwaykids.models;

/**
 * Created by SCORP on 19/05/15.
 */
public class Response {

    public String onTime;
    public String lat;
    public String lng;
    public String speed;
    public String date;
    public String alt;
    public String address;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSignal() {
        return signal;
    }

    public void setSignal(String signal) {
        this.signal = signal;
    }

    public String signal;

    public String getBetteryStatus() {
        return betteryStatus;
    }

    public void setBetteryStatus(String betteryStatus) {
        this.betteryStatus = betteryStatus;
    }

    public String betteryStatus;

    public String getOnTime() {
        return onTime;
    }

    public void setOnTime(String onTime) {
        this.onTime = onTime;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public void setSpeed(String speed){
        this.speed = speed;
    }

    public String getSpeed(){
        return this.speed;
    }
}
