package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * 常量类
 * Created by Administrator on 2018/4/29.
 */
public class Const {
    //定义当前用户
    public  static  final  String CURRENT_USER = "currentUser";

    public static final  String EMAIL = "email";
    public static  final String USERNAME = "username";

    public interface productListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc","price_asc");
    }

    //内部接口类进行常量分组，没有枚举那么繁重，又起到了分组的作用
    public interface Role{
        int ROLE_COSTOMER = 0;//普通用户
        int ROLE_ADMIN = 1;//管
    }

    public enum ProductStatusEnum{
        ON_SALE(1,"在线");

        private String value;
        private int code;


        ProductStatusEnum(int code,String value){
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }
}
