package com.mmall.common;

/**
 * 常量类
 * Created by Administrator on 2018/4/29.
 */
public class Const {
    //定义当前用户
    public  static  final  String CURRENT_USER = "currentUser";

    public static final  String EMAIL = "email";
    public static  final String USERNAME = "username";

    //内部接口类进行常量分组，没有枚举那么繁重，又起到了分组的作用
    public interface Role{
        int ROLE_COSTOMER = 0;//普通用户
        int ROLE_ADMIN = 1;//管
    }
}
