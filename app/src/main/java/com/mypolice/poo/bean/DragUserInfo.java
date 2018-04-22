package com.mypolice.poo.bean;

/**
 * @Title: DragUserInfo.java
 * @Package com.mypolice.poo.bean
 * @Description: 用户信息 实体类
 * @author wangjl
 * @crdate 2017-8-23
 * @update
 * @version v1.0.0(1)
 */
public class DragUserInfo {

    private int id;
    private String drug_name;
    private String another_name;            // Base64
    private String mobile_phone;
    private int sex;
    private String nation_code;
    private String id_code;
    private String birthday;
    private int stature;
    private int marital_bm;
    private int education_bm;
    private int employment_bm;
//    private String residence_address;       // Base64
//    private String residence_station;       // Base64
//    private String live_address;            // Base64
//    private String live_station;            // Base64
    private String avatar_url;
    private String qq_code;                 // Base64
    private String weixin_code;             // Base64
    private int reg_id;
    private String reg_time;
    private int last_id;
    private String last_time;
    private String community_id_power;
    private String weixin_openid;
    private String weixin_headurl;
    private int weixin_grade;
    private String weixin_tag;
    private String weixin_time;
    private boolean time_array;
    private int age;
    private int periods;
    private String secretary_name;
    private String community_name;
    private String recovery_name;

    @Override
    public String toString() {
        return "DragUserInfo{" +
                "id=" + id +
                ", drug_name='" + drug_name + '\'' +
                ", another_name='" + another_name + '\'' +
                ", mobile_phone='" + mobile_phone + '\'' +
                ", sex=" + sex +
                ", nation_code='" + nation_code + '\'' +
                ", id_code='" + id_code + '\'' +
                ", birthday='" + birthday + '\'' +
                ", stature=" + stature +
                ", marital_bm=" + marital_bm +
                ", education_bm=" + education_bm +
                ", employment_bm=" + employment_bm +
                ", avatar_url='" + avatar_url + '\'' +
                ", qq_code='" + qq_code + '\'' +
                ", weixin_code='" + weixin_code + '\'' +
                ", reg_id=" + reg_id +
                ", reg_time='" + reg_time + '\'' +
                ", last_id=" + last_id +
                ", last_time='" + last_time + '\'' +
                ", community_id_power='" + community_id_power + '\'' +
                ", weixin_openid='" + weixin_openid + '\'' +
                ", weixin_headurl='" + weixin_headurl + '\'' +
                ", weixin_grade=" + weixin_grade +
                ", weixin_tag='" + weixin_tag + '\'' +
                ", weixin_time='" + weixin_time + '\'' +
                ", time_array=" + time_array +
                ", age=" + age +
                ", periods=" + periods +
                ", secretary_name='" + secretary_name + '\'' +
                ", community_name='" + community_name + '\'' +
                ", recovery_name='" + recovery_name + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDrug_name() {
        return drug_name;
    }

    public void setDrug_name(String drug_name) {
        this.drug_name = drug_name;
    }

    public String getAnother_name() {
        return another_name;
    }

    public void setAnother_name(String another_name) {
        this.another_name = another_name;
    }

    public String getMobile_phone() {
        return mobile_phone;
    }

    public void setMobile_phone(String mobile_phone) {
        this.mobile_phone = mobile_phone;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getNation_code() {
        return nation_code;
    }

    public void setNation_code(String nation_code) {
        this.nation_code = nation_code;
    }

    public String getId_code() {
        return id_code;
    }

    public void setId_code(String id_code) {
        this.id_code = id_code;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getStature() {
        return stature;
    }

    public void setStature(int stature) {
        this.stature = stature;
    }

    public int getMarital_bm() {
        return marital_bm;
    }

    public void setMarital_bm(int marital_bm) {
        this.marital_bm = marital_bm;
    }

    public int getEducation_bm() {
        return education_bm;
    }

    public void setEducation_bm(int education_bm) {
        this.education_bm = education_bm;
    }

    public int getEmployment_bm() {
        return employment_bm;
    }

    public void setEmployment_bm(int employment_bm) {
        this.employment_bm = employment_bm;
    }

//    public String getResidence_address() {
//        return residence_address;
//    }
//
//    public void setResidence_address(String residence_address) {
//        this.residence_address = residence_address;
//    }
//
//    public String getResidence_station() {
//        return residence_station;
//    }
//
//    public void setResidence_station(String residence_station) {
//        this.residence_station = residence_station;
//    }
//
//    public String getLive_address() {
//        return live_address;
//    }
//
//    public void setLive_address(String live_address) {
//        this.live_address = live_address;
//    }
//
//    public String getLive_station() {
//        return live_station;
//    }
//
//    public void setLive_station(String live_station) {
//        this.live_station = live_station;
//    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getQq_code() {
        return qq_code;
    }

    public void setQq_code(String qq_code) {
        this.qq_code = qq_code;
    }

    public String getWeixin_code() {
        return weixin_code;
    }

    public void setWeixin_code(String weixin_code) {
        this.weixin_code = weixin_code;
    }

    public int getReg_id() {
        return reg_id;
    }

    public void setReg_id(int reg_id) {
        this.reg_id = reg_id;
    }

    public String getReg_time() {
        return reg_time;
    }

    public void setReg_time(String reg_time) {
        this.reg_time = reg_time;
    }

    public int getLast_id() {
        return last_id;
    }

    public void setLast_id(int last_id) {
        this.last_id = last_id;
    }

    public String getLast_time() {
        return last_time;
    }

    public void setLast_time(String last_time) {
        this.last_time = last_time;
    }

    public String getCommunity_id_power() {
        return community_id_power;
    }

    public void setCommunity_id_power(String community_id_power) {
        this.community_id_power = community_id_power;
    }

    public String getWeixin_openid() {
        return weixin_openid;
    }

    public void setWeixin_openid(String weixin_openid) {
        this.weixin_openid = weixin_openid;
    }

    public String getWeixin_headurl() {
        return weixin_headurl;
    }

    public void setWeixin_headurl(String weixin_headurl) {
        this.weixin_headurl = weixin_headurl;
    }

    public int getWeixin_grade() {
        return weixin_grade;
    }

    public void setWeixin_grade(int weixin_grade) {
        this.weixin_grade = weixin_grade;
    }

    public String getWeixin_tag() {
        return weixin_tag;
    }

    public void setWeixin_tag(String weixin_tag) {
        this.weixin_tag = weixin_tag;
    }

    public String getWeixin_time() {
        return weixin_time;
    }

    public void setWeixin_time(String weixin_time) {
        this.weixin_time = weixin_time;
    }

    public boolean isTime_array() {
        return time_array;
    }

    public void setTime_array(boolean time_array) {
        this.time_array = time_array;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getPeriods() {
        return periods;
    }

    public void setPeriods(int periods) {
        this.periods = periods;
    }

    public String getSecretary_name() {
        return secretary_name;
    }

    public void setSecretary_name(String secretary_name) {
        this.secretary_name = secretary_name;
    }

    public String getCommunity_name() {
        return community_name;
    }

    public void setCommunity_name(String community_name) {
        this.community_name = community_name;
    }

    public String getRecovery_name() {
        return recovery_name;
    }

    public void setRecovery_name(String recovery_name) {
        this.recovery_name = recovery_name;
    }
}
