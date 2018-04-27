package com.mypolice.poo.bean;

import java.util.List;

/**
 * @Title: PunishBean.java
 * @Package com.mypolice.poo.bean
 * @Description: 违反协议处置 实体类
 * @author wangjl
 * @crdate 2017-10-27
 * @update 2018-4-27    update 结构
 * @version v1.0.1(2)[六安]
 */
public class PunishBean {

    private int total;
    private int per_page;
    private int current_page;
    private int last_page;
    private List<NoticeBean> data;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPer_page() {
        return per_page;
    }

    public void setPer_page(int per_page) {
        this.per_page = per_page;
    }

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public int getLast_page() {
        return last_page;
    }

    public void setLast_page(int last_page) {
        this.last_page = last_page;
    }

    public List<NoticeBean> getData() {
        return data;
    }

    public void setData(List<NoticeBean> data) {
        this.data = data;
    }

    public class NoticeBean {

        private int msg_id;
        private int msg_type;
        private String content;
        private String topic;
        private String create_time;
        private int is_read;

        public int getMsg_id() {
            return msg_id;
        }

        public void setMsg_id(int msg_id) {
            this.msg_id = msg_id;
        }

        public int getMsg_type() {
            return msg_type;
        }

        public void setMsg_type(int msg_type) {
            this.msg_type = msg_type;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public int getIs_read() {
            return is_read;
        }

        public void setIs_read(int is_read) {
            this.is_read = is_read;
        }
    }
}
