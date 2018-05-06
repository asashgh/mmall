package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2018/5/1.
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;


    /**
     * 添加产品类别，ResponseBody注解将jackson自动序列化成json
     *
     * @param session
     * @param categoryName
     * @param parentId
     * @return
     */
    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse addCategory(HttpSession session, String categoryName, @RequestParam(value = "parentId", defaultValue = "0") Integer parentId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登陆，请登陆");
        }
        //校验是否为管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //管理员增加处理分类逻辑
            return iCategoryService.addCategory(categoryName, parentId);
        } else {
            return ServerResponse.createByErrorMessage("您不是管理员，无权限操作");
        }
    }


    /**
     * 更新产品名字
     *
     * @param session
     * @param categoryId
     * @param categoryName
     * @return
     */
    @RequestMapping("set_category_name.do")
    @ResponseBody
    public ServerResponse setCategoryName(HttpSession session, Integer categoryId, String categoryName) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登陆，请登陆");
        }
        //校验是否为管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //更新categoryName
            return iCategoryService.updateCategoryName(categoryId,categoryName);
        } else {
            return ServerResponse.createByErrorMessage("您不是管理员，无权限操作");
        }
    }

    /**
     * 根据categoryId，获取当前category下边子节点的categoryId的信息，并且是平级无递归的
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping("get_category.do")
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登陆，请登陆");
        }
        //校验是否为管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //查询子节点的category信息，并且无递归，保持平级

            return iCategoryService.getChildrenParallelCategory(categoryId);
        } else {
            return ServerResponse.createByErrorMessage("您不是管理员，无权限操作");
        }
    }


    /**
     * 获取当前categoryId并且递归获取子节点的categoryId
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session,@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登陆，请登陆");
        }
        //校验是否为管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //查询当前节点的id和递归子节点的id
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
        } else {
            return ServerResponse.createByErrorMessage("您不是管理员，无权限操作");
        }
    }

}