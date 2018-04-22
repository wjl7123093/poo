package com.mypolice.poo.bean;

/**
 * @Title: FileBean.java
 * @Package com.mypolice.poo.bean
 * @Description: 文件信息 实体类
 * @author wangjl
 * @crdate 2017-9-1
 * @update
 * @version v1.0.0(1)
 */
public class FileBean {

    private String file;
    private String name;
    private String md5;

    @Override
    public String toString() {
        return "FileBean{" +
                "file='" + file + '\'' +
                ", name='" + name + '\'' +
                ", md5='" + md5 + '\'' +
                '}';
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

}
