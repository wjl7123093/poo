package com.mypolice.poo.bean;

/**
 * @Title: ArticlesBean.java
 * @Package com.mypolice.poo.bean
 * @Description: 文章信息 实体类
 * @author wangjl
 * @crdate 2017-8-30
 * @update
 * @version v1.0.0(1)
 */
public class ArticlesBean {

    private int id;
    private String name;
//    private String desc;
//    private int portal_category_id;
//    private String keyword;
//    private String tag;
//    private String author;
//    private String source;
//    private String cover;
//    private int sort;
//    private int hide;
//    private int view;
//    private int member_id;
    private String create_time;
    private String update_time;
//    private int status;

    @Override
    public String toString() {
        return "ArticlesBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", create_time='" + create_time + '\'' +
                ", update_time='" + update_time + '\'' +
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

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }
}
