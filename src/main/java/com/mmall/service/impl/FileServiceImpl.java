package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

/**
 * Created by Administrator on 2018/5/3.
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService{

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    /**
     * 把上传时候的文件名返回
     * @return
     */
    public String upload(MultipartFile file, String path){
        String fileName = file.getOriginalFilename();//拿到文件名
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);//获取扩展名,不要点
        String updateFileName = UUID.randomUUID().toString()+"."+fileExtensionName;//上传文件名字,为了保证每个人发送同名文件不被覆盖
        logger.info("开始上传文件，上传文件的文件名:{},上传的路径:{},新文件名:{}",fileName,path,updateFileName);

        File fileDir = new File(path);//创建目录file
        if(!fileDir.exists()){
            fileDir.setWritable(true);//赋予文件权限,可写
            fileDir.mkdirs();//获取能在webapp文件下创建文件加的权限,因为发布完后可以改，mkdir是当前级别的，mkdirs是/a/b这样的文件夹创建
        }
        File targetFile = new File(path,updateFileName);//完整的file，包括路径文件名和扩展名

        try {
            file.transferTo(targetFile);
            //文件上传成功
            // TODO: 2018/5/3 将targetFile上传到FTP服务器上
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));//已上传到ftp服务器上
            // TODO: 2018/5/3 上传完后删除upload下面的文件
            targetFile.delete();
        }catch (Exception e){
            logger.error("上传文件异常",e);
            return null;
        }
        System.out.println(targetFile.getName());
        return targetFile.getName();
    }
}
