package com.mypolice.poo.application;

/**
 * @Title: ApiCode.java
 * @Package com.mypolice.poo.application
 * @Description: 服务端接口 返回码
 * @author wangjl
 * @crdate 2018-4-24
 * @update
 * @version v1.0.0[六安]
 */
public interface ApiCode {

    int CODE_SUCCESS = 1;
    int CODE_FAILURE = 0;
    int CODE_TOKEN_EXPIRED = 1005;  // token 过期
    int CODE_EMPTY_DATA = 1013;     // 空数据

}
