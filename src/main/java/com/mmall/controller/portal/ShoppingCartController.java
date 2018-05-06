package com.mmall.controller.portal;

import com.google.common.net.HttpHeaders;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import net.sf.jsqlparser.schema.Server;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2018/5/6.
 */
public class ShoppingCartController {

    public ServerResponse getList(HttpSession session,String productName,Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆,请登陆");
        }
        //填充获取列表业务
        return null;
    }
}
