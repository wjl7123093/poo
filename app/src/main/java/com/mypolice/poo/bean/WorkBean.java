package com.mypolice.poo.bean;

/**
 * @Title: WorkBean.java
 * @Package com.mypolice.poo.bean
 * @Description: 任务信息 实体类
 * @author wangjl
 * @crdate 2017-8-23
 * @update 2017-10-26
 * @version v2.1.0(12)
 */
public class WorkBean {

    private int id;
    private int work_type;
    private String work_time;
    private String remark;
    private int work_tag;
    private String work_tag_text;
    private String work_type_text;

    @Override
    public String toString() {
        return "WorkBean{" +
                "id=" + id +
                ", work_type=" + work_type +
                ", work_time='" + work_time + '\'' +
                ", remark='" + remark + '\'' +
                ", work_tag=" + work_tag +
                ", work_tag_text=" + work_tag_text +
                ", work_type_text=" + work_type_text +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWork_type() {
        return work_type;
    }

    public void setWork_type(int work_type) {
        this.work_type = work_type;
    }

    public String getWork_time() {
        return work_time;
    }

    public void setWork_time(String work_time) {
        this.work_time = work_time;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getWork_tag() {
        return work_tag;
    }

    public void setWork_tag(int work_tag) {
        this.work_tag = work_tag;
    }

    public String getWork_tag_text() {
        return work_tag_text;
    }

    public void setWork_tag_text(String work_tag_text) {
        this.work_tag_text = work_tag_text;
    }

    public String getWork_type_text() {
        return work_type_text;
    }

    public void setWork_type_text(String work_type_text) {
        this.work_type_text = work_type_text;
    }
}
