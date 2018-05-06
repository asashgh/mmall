package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by Administrator on 2018/5/2.
 */
@Controller
@RequestMapping("/manage/product")
public class ProduncManageController {
    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;

    /**
     * 产品保存controller
     * @param session
     * @param product
     * @return
     */
    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆,请登陆管理员");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充增加产品的业务逻辑
            return iProductService.saveOrUpdateProduct(product);
        }
        return ServerResponse.createByErrorMessage("您不是管理员，无权限操作");
    }

    /**
     * 更新商品销售状态
     * @param session
     * @param
    * @return
     */
    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId,Integer status){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆,请登陆管理员");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.setSaleStatus(productId,status);
        }
        return ServerResponse.createByErrorMessage("您不是管理员，无权限操作");
    }

    /**
     * 获取商品详情
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpSession session, Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆,请登陆管理员");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充业务
            return iProductService.manageProductDetail(productId);
        }
        return ServerResponse.createByErrorMessage("您不是管理员，无权限操作");
    }

    /**
     * 获取商品列表
     * @param session
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("get_list.do")
    @ResponseBody
    public ServerResponse getList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,@RequestParam(value = "pageSize",defaultValue = "2") int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆,请登陆管理员");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充业务
            return iProductService.getProductList(pageNum,pageSize);
        }
        return ServerResponse.createByErrorMessage("您不是管理员，无权限操作");
    }

    /**
     * 模糊查询查找商品
     * @param session
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse searchProduct(HttpSession session,String productName,Integer productId, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,@RequestParam(value = "pageSize",defaultValue = "2") int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆,请登陆管理员");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充业务
            return iProductService.searchProduct(productName,productId,pageNum,pageSize);
        }
        return ServerResponse.createByErrorMessage("您不是管理员，无权限操作");
    }


    /**
     * SpringMVC上传文件，根据servlet动态生成一个相对路径出来
     * @param file
     * @return
     */
    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session, @RequestParam(value = "upload_file",required = false)MultipartFile file, HttpServletRequest request){

        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆,请登陆管理员");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //这个路径上传后会创建在发布之后的webapp下
            String path = request.getSession().getServletContext().getRealPath("upload");
            System.out.println(file);
            String targetFileName = iFileService.upload(file,path);
            //拼url
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            Map fileMap = Maps.newHashMap();
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);

            return ServerResponse.createBySuccess(fileMap);
        }
        return ServerResponse.createByErrorMessage("您不是管理员，无权限操作");
    }


    /**
     * 富文本文件上传
     * @param session
     * @param file
     * @param request
     * @return
     */
    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map richtextImgUpload(HttpSession session, @RequestParam(value = "upload_file",required = false)MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        Map resultMap = Maps.newHashMap();
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            //simditor返回要求
            //{
            //    "success": true/false
            //      "msg": "error massage"
            //    "file_path": "[real file path]"
            // }
            resultMap.put("success",false);
            resultMap.put("msg","请登陆管理员");
            return resultMap;
        }
        //富文本中对于自己的返回值有自己的要求，我们使用的是simditor，所以按照simditor的要求进行返回
        if(iUserService.checkAdminRole(user).isSuccess()){
            //这个路径上传后会创建在发布之后的webapp下
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            //拼url
            if(StringUtils.isBlank(targetFileName)){
                resultMap.put("success",false);
                resultMap.put("msg","上传失败");
                return resultMap;
            }

            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
            resultMap.put("file_path",url);

            response.addHeader("Access-Control-Allow-Headers","X-File_Name");//和前端的约定，一些前段的插件对后端有要求的，所以要学会修改response的header
            //这个后端配置只针对simditor插件，因为是按照simditor的规范来返回数据的

            return resultMap;
        }
        resultMap.put("success",false);
        resultMap.put("msg","无权限操作");
        return resultMap;
    }
}
