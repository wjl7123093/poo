package com.mypolice.poo.bean;

/**
 * @Title: PunishBean.java
 * @Package com.mypolice.poo.bean
 * @Description: 违反协议处置 实体类
 * @author wangjl
 * @crdate 2017-10-27
 * @update
 * @version v2.1.0(12)
 */
public class PunishBean {

    private int id;
    private int drug_user_id;
    private String reg_time;
    private String punish_type_text;
    private int is_read;

    @Override
    public String toString() {
        return "PunishBean{" +
                "id=" + id +
                ", drug_user_id=" + drug_user_id +
                ", reg_time='" + reg_time + '\'' +
                ", punish_type_text='" + punish_type_text + '\'' +
                ", is_read=" + is_read +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDrug_user_id() {
        return drug_user_id;
    }

    public void setDrug_user_id(int drug_user_id) {
        this.drug_user_id = drug_user_id;
    }

    public String getReg_time() {
        return reg_time;
    }

    public void setReg_time(String reg_time) {
        this.reg_time = reg_time;
    }

    public String getPunish_type_text() {
        return punish_type_text;
    }

    public void setPunish_type_text(String punish_type_text) {
        this.punish_type_text = punish_type_text;
    }

    public int getIs_read() {
        return is_read;
    }

    public void setIs_read(int is_read) {
        this.is_read = is_read;
    }
}
