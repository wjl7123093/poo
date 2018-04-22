package com.mypolice.poo.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @Title: LeaveBean.java
 * @Package com.mypolice.poo.bean
 * @Description: 请假信息 实体类
 * @author wangjl
 * @crdate 2017-8-30
 * @update 2017-9-1
 * @version v1.0.0(1)
 */
public class LeaveBean implements Parcelable {

    private int drug_user_id;
    private int community_drug_reg_id;
    private String destination;
    private String reason;
    private long start_time;
    private long end_time;
    private int leave_num;
    private String visitor_name;
    private String visitor_relation;
    private String visitor_profession;
    private String visitor_company;
    private String visitor_address;
    private String visitor_tel;
    private String remark;
    private int leave_type;

    public LeaveBean() {  }

    protected LeaveBean(Parcel in) {
        drug_user_id = in.readInt();
        community_drug_reg_id = in.readInt();
        destination = in.readString();
        reason = in.readString();
        start_time = in.readLong();
        end_time = in.readLong();
        leave_num = in.readInt();
        visitor_name = in.readString();
        visitor_relation = in.readString();
        visitor_profession = in.readString();
        visitor_company = in.readString();
        visitor_address = in.readString();
        visitor_tel = in.readString();
        remark = in.readString();
        leave_type = in.readInt();
    }

    public static final Creator<LeaveBean> CREATOR = new Creator<LeaveBean>() {
        @Override
        public LeaveBean createFromParcel(Parcel in) {
            return new LeaveBean(in);
        }

        @Override
        public LeaveBean[] newArray(int size) {
            return new LeaveBean[size];
        }
    };

    @Override
    public String toString() {
        return "LeaveBean{" +
                "drug_user_id=" + drug_user_id +
                ", community_drug_reg_id=" + community_drug_reg_id +
                ", destination='" + destination + '\'' +
                ", reason='" + reason + '\'' +
                ", start_time='" + start_time + '\'' +
                ", end_time='" + end_time + '\'' +
                ", leave_num=" + leave_num +
                ", visitor_name='" + visitor_name + '\'' +
                ", visitor_relation='" + visitor_relation + '\'' +
                ", visitor_profession='" + visitor_profession + '\'' +
                ", visitor_company='" + visitor_company + '\'' +
                ", visitor_address='" + visitor_address + '\'' +
                ", visitor_tel='" + visitor_tel + '\'' +
                ", remark='" + remark + '\'' +
                ", leave_type=" + leave_type +
                '}';
    }

    public int getDrug_user_id() {
        return drug_user_id;
    }

    public void setDrug_user_id(int drug_user_id) {
        this.drug_user_id = drug_user_id;
    }

    public int getCommunity_drug_reg_id() {
        return community_drug_reg_id;
    }

    public void setCommunity_drug_reg_id(int community_drug_reg_id) {
        this.community_drug_reg_id = community_drug_reg_id;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

    public int getLeave_num() {
        return leave_num;
    }

    public void setLeave_num(int leave_num) {
        this.leave_num = leave_num;
    }

    public String getVisitor_name() {
        return visitor_name;
    }

    public void setVisitor_name(String visitor_name) {
        this.visitor_name = visitor_name;
    }

    public String getVisitor_relation() {
        return visitor_relation;
    }

    public void setVisitor_relation(String visitor_relation) {
        this.visitor_relation = visitor_relation;
    }

    public String getVisitor_profession() {
        return visitor_profession;
    }

    public void setVisitor_profession(String visitor_profession) {
        this.visitor_profession = visitor_profession;
    }

    public String getVisitor_company() {
        return visitor_company;
    }

    public void setVisitor_company(String visitor_company) {
        this.visitor_company = visitor_company;
    }

    public String getVisitor_address() {
        return visitor_address;
    }

    public void setVisitor_address(String visitor_address) {
        this.visitor_address = visitor_address;
    }

    public String getVisitor_tel() {
        return visitor_tel;
    }

    public void setVisitor_tel(String visitor_tel) {
        this.visitor_tel = visitor_tel;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getLeave_type() {
        return leave_type;
    }

    public void setLeave_type(int leave_type) {
        this.leave_type = leave_type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(drug_user_id);
        dest.writeInt(community_drug_reg_id);
        dest.writeString(destination);
        dest.writeString(reason);
        dest.writeLong(start_time);
        dest.writeLong(end_time);
        dest.writeInt(leave_num);
        dest.writeString(visitor_name);
        dest.writeString(visitor_relation);
        dest.writeString(visitor_profession);
        dest.writeString(visitor_company);
        dest.writeString(visitor_address);
        dest.writeString(visitor_tel);
        dest.writeString(remark);
        dest.writeInt(leave_type);
    }
}
