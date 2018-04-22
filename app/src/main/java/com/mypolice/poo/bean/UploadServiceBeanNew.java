package com.mypolice.poo.bean;

/**
 * Created by wangjl on 2017/9/7.
 */

public class UploadServiceBeanNew {

    private int drug_user_id;
    private long end_time;
    private double longitude;
    private double latitude;

    @Override
    public String toString() {
        return "UpdateServiceBeanNew{" +
                "drug_user_id=" + drug_user_id +
                ", end_time=" + end_time +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }

    public int getDrug_user_id() {
        return drug_user_id;
    }

    public void setDrug_user_id(int drug_user_id) {
        this.drug_user_id = drug_user_id;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
