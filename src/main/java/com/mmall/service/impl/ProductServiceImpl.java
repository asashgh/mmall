package com.mmall.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.omg.CORBA.Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2018/5/2.
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService{

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 保存或者新增商品service
     * @param product
     * @return
     */
    public ServerResponse saveOrUpdateProduct(Product product){
        if(product != null){
            //判断子图是否存在，如果存在，把第一张图作为主图
            if(StringUtils.isNotBlank(product.getSubImages())){
                //用String进行分割
                String[] subImageArray = product.getSubImages().split(",");
                if(subImageArray.length>0){
                    product.setMainImage(subImageArray[0]);
                }
            }
            //id存在更新操作,不存在新增操作
            if(product.getId() != null){
                int rowCount = productMapper.updateByPrimaryKey(product);
                if(rowCount > 0) {
                    return ServerResponse.createBySuccess("更新产品成功");
                }
                return ServerResponse.createByErrorMessage("更新产品失败");
            }else{
                int rowCount = productMapper.insert(product);
                if(rowCount > 0) {
                    return ServerResponse.createBySuccess("新增产品成功");
                }
                return ServerResponse.createByErrorMessage("新增产品失败");
            }
        }
        return ServerResponse.createByErrorMessage("新增或更新参数不正确");
    }

    /**
     * 设置产品销售状态
     * @param productId
     * @param status
     * @return
     */
    public ServerResponse<String> setSaleStatus(Integer productId,Integer status){
        if(productId == null || status == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);

        int rowCount = productMapper.updateByPrimaryKeySelective(product);

        if(rowCount > 0){
            return ServerResponse.createBySuccessMessage("设置产品销售状态成功");
        }
        return ServerResponse.createByErrorMessage("设置产品销售状态失败");
    }


    /**
     * 后台得到产品信息
     * @param productId
     * @return
     */
    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId){
        if(productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        //VO对象--value object 业务逻辑相对简单,根据系统架构的演变过程使用此对象
        //pojo -> bo(business object) ->vo(view object) 稍微复杂一点的
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }
    //装载Vo对象
    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImage(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        /**
         * imageHost,从一个配置文件当中，如果图片服务器修改，只需要修改properties配置，因为这个imageHost在代码中会多次引用，如果硬编码，很麻烦，因为配置文件之后会优化成一个热部署配置，另外也会去建立一个配置中心，专门管理这些配置
         * 热部署：也就是说重启，把我们的配置初始化，之后的演进当中逐渐抽离配置，形成一个配置中心，专门管理这些配置
         * 写一个properyiesUtil专门读取配置文件,然后通过读取配置文件将imageHost读取出来
         */
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        //parentCategoryId
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null){
            productDetailVo.setParentCategoryId(0);//默认根节点
        }else {
            productDetailVo.setParentCategoryId(category.getParentId());
        }
        //createTime
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        //updateTime
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        return productDetailVo;
    }



    /**
     * 获取产品清单
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse getProductList(int pageNum,int pageSize){
        //startPage--start
        //填充自己的sql查询逻辑
        //配置pageHelper收尾
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList = productMapper.selectList();

        List<ProductListVo> productListVoList = Lists.newArrayList();

        for(Product productItem : productList){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        if(pageResult == null){
            return ServerResponse.createByErrorMessage("添加分页失败");
        }
        return ServerResponse.createBySuccess("添加分页成功",pageResult);
    }

    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        return productListVo;
    }

    public ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        if(StringUtils.isNotBlank(productName)){
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.selectByNameAndProductId(productName,productId);

        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product:productList){
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        if(pageResult == null){
            return ServerResponse.createByErrorMessage("查询失败");
        }
        return ServerResponse.createBySuccess("查询成功",pageResult);
    }


    //todo 前台部分


    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        if(productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        //VO对象--value object 业务逻辑相对简单,根据系统架构的演变过程使用此对象
        //pojo -> bo(business object) ->vo(view object) 稍微复杂一点的
        if(product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }


    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy){
        if (StringUtils.isBlank(keyword) && categoryId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryIdList = new ArrayList<Integer>();//当传分类的时候，假设传一个高级的分类，那就要使用递归算法，把所有属于这个分类的子分类遍历出来，并且加上本身，查询sql的时候直接用in去命中产品所在的德和的条件

        if (categoryId != null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if(category == null&&keyword == null){
                //没有该分类，并且没有关键字，返回一个空的结果集，不报错
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryIdList = iCategoryService.selectCategoryAndChildrenById(category.getId()).getData();
        }
        if (StringUtils.isNotBlank(keyword)){
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }
        PageHelper.startPage(pageNum,pageSize);
        //排序处理
        if(StringUtils.isNotBlank(orderBy)){
            if (Const.productListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
            }
        }
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword,categoryIdList.size()==0?null:categoryIdList);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product:productList){
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);

        return ServerResponse.createBySuccess(pageInfo);
    }
}
