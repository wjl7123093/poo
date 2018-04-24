package com.mypolice.poo.bean;

/**
 * @Title: UserEntity.java
 * @Package com.mypolice.poo.bean
 * @Description: 用户信息 实体类
 * @author wangjl
 * @crdate 2017-4-24
 * @update
 * @version v1.0.0[六安版]
 */
public class UserEntity {

    private int user_id;
    private String name;
    private String avatar_url;
    private String status;
    private String secretary;
    private String community;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSecretary() {
        return secretary;
    }

    public void setSecretary(String secretary) {
        this.secretary = secretary;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }
}
