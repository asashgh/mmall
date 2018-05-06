package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2018/5/1.
 */
@Service
public class CategoryServiceImpl implements ICategoryService{

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 添加分类方法
     * @return
     */
    public ServerResponse addCategory(String categoryName,Integer parentId){
        if(parentId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("参数错误");
        }

        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);//这个分类是可用的

        int rowCount = categoryMapper.insert(category);
        if(rowCount > 0){
            return ServerResponse.createBySuccess("添加品类成功");
        }
        return ServerResponse.createByErrorMessage("添加品类失败");
    }


    /**
     * 更新产品名字
     * @param categoryId
     * @param categoryName
     * @return
     */
    public ServerResponse updateCategoryName(Integer categoryId,String categoryName){
        if(categoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("更新产品参数错误");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(rowCount > 0){
            return ServerResponse.createBySuccess("更新品类名字成功");
        }
        return ServerResponse.createByErrorMessage("更新品类名字失败");
    }

    /**
     * 查找平级子节点无递归
     * @param categoryId
     * @return
     */
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
//        logger.info("\n"+categoryId);
        if(categoryList.isEmpty()){
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }


    /**
     * 递归查询所有子节点
     * @param categoryId
     * @return
     */
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
        //guava中提供处理集合的方法，很强大
        Set<Category> categorySet = Sets.newHashSet();
        //调用递归算法
        findChildCategory(categorySet,categoryId);

        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryId != null){
            for(Category categoryItem:categorySet){
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }
    //todo 递归函数,用set排除重复元素
    //重写Category中的hashCode()和equal()方法，保证判断因子一致，来进行特定属性判断两个元素是否相等，因为set判断的依据是调用这两个方法
    private Set<Category> findChildCategory(Set<Category> categorySet,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category != null){
            categorySet.add(category);
        }
        //查找子节点，递归算法一定要有一个退出的条件及子节点是否为空
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        //mybatis处理集合，如果集合没有查到，不会返回null对象，即categoryList不会有null的情况,及不要进行空判断
        for(Category categoryItem:categoryList){
            findChildCategory(categorySet,categoryItem.getId());
        }
        return categorySet;
    }
}
