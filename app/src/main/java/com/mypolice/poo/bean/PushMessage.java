package com.mypolice.poo.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/5/15.
 */

public class PushMessage implements Serializable {
    private int CODE;
    private int ID;

    @Override
    public String toString() {
        return "PushMessage{" +
                "CODE=" + CODE +
                ", ID=" + ID +
                '}';
    }

    public int getCODE() {
        return CODE;
    }

    public void setCODE(int CODE) {
        this.CODE = CODE;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
