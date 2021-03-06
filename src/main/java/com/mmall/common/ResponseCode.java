package com.mmall.common;

/**
 * 响应编码的枚举类
 * Created by Administrator on 2018/4/29.
 */
public enum ResponseCode {
    SUCCESS(0,"SUCCESS"),
    ERROR(1,"Error"),
    NEED_LOGIN(10,"NEED_LOGIN"),
    ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT");    //非法参数

    private final int code;
    private final String desc;

    ResponseCode(int code,String desc){
        this.code = code;
        this.desc = desc;
    }

    public  int getCode(){
        return code;
    }

    public String getDesc(){
        return desc;
    }

}
