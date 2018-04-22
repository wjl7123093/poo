package com.mypolice.poo.bean;

/**
 * @Title: CategoryBean.java
 * @Package com.mypolice.poo.bean
 * @Description: 宣传教育 分类信息 实体类
 * @author wangjl
 * @crdate 2017-8-30
 * @update
 * @version v1.0.0(1)
 */
public class CategoryBean {

    private int id;
    private String name;
    private int type;
    private int sort;
    private String type_text;

    @Override
    public String toString() {
        return "CategoryBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", sort=" + sort +
                ", type_text='" + type_text + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getType_text() {
        return type_text;
    }

    public void setType_text(String type_text) {
        this.type_text = type_text;
    }
}
