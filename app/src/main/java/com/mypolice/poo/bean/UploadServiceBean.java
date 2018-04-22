package com.mypolice.poo.bean;

/**
 * @Title: UploadServiceBean.java
 * @Package com.mypolice.poo.bean
 * @Description: 后台服务定时上传 实体类
 * @author wangjl
 * @crdate 2017-9-5
 * @update
 * @version v1.0.0(1)
 */
public class UploadServiceBean {

    private int drug_user_id;
    private String id_code;
    private String user_tel;
    private String from_id;
    private double longitude;
    private double latitude;
    private String address;
    private long reg_time;

    @Override
    public String toString() {
        return "UploadServiceBean{" +
                "drug_user_id=" + drug_user_id +
                ", id_code='" + id_code + '\'' +
                ", user_tel='" + user_tel + '\'' +
                ", from_id='" + from_id + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", address='" + address + '\'' +
                ", reg_time=" + reg_time +
                '}';
    }

    public int getDrug_user_id() {
        return drug_user_id;
    }

    public void setDrug_user_id(int drug_user_id) {
        this.drug_user_id = drug_user_id;
    }

    public String getId_code() {
        return id_code;
    }

    public void setId_code(String id_code) {
        this.id_code = id_code;
    }

    public String getUser_tel() {
        return user_tel;
    }

    public void setUser_tel(String user_tel) {
        this.user_tel = user_tel;
    }

    public String getFrom_id() {
        return from_id;
    }

    public void setFrom_id(String from_id) {
        this.from_id = from_id;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getReg_time() {
        return reg_time;
    }

    public void setReg_time(long reg_time) {
        this.reg_time = reg_time;
    }
}
