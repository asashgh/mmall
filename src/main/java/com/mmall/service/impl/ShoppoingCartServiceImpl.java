package com.mmall.service.impl;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.service.IShoppingService;
import com.mmall.vo.ProductListVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2018/5/6.
 */
@Service("iShopingService")
public class ShoppoingCartServiceImpl implements IShoppingService{

    public ServerResponse<List<ProductListVo>> getList(String productName,Integer productId){
        return null
                ;
    }
}
