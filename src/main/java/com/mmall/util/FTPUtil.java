package com.mmall.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2018/5/3.
 */
public class FTPUtil {

    private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static String ftpPass = PropertiesUtil.getProperty("ftp.pass");

    public FTPUtil(String ip,int port,String user,String pwd){
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
    }


    /**
     * 对外开放的方法
     * @param fileList
     * @return
     * @throws Exception
     */
    public static boolean uploadFile(List<File> fileList) throws Exception{
        FTPUtil ftpUtil = new FTPUtil(ftpIp,21,ftpUser,ftpPass);
        logger.info("开始连接ftp服务器");

        boolean result = ftpUtil.uploadFile("img",fileList);
        logger.info("结束上传，上传结束：{}");
        return result;
    }

    private boolean uploadFile(String remotePath,List<File> fileList) throws Exception{
        boolean uploaded = true;
        FileInputStream fis = null;
        //连接ftp服务器
        if(connectionServer(this.ip,this.port,this.user,this.pwd)){
            try {
                ftpClient.changeWorkingDirectory(remotePath);//修改工作目录,需不需要切换文件夹
                ftpClient.setBufferSize(1024);//设置服务器缓冲区
                ftpClient.setControlEncoding("UTF-8");//设置字符集
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);//设置成ftpclient里提供的一个常量，把文件类型设置成二进制文件类型，或防止乱码
                ftpClient.enterLocalPassiveMode();;//打开本地被动模式，因为之前ftp配置的是被动模式，开放了一个服务被动端口范围
                for(File fileItem:fileList){
                    fis = new FileInputStream(fileItem);
                    //通过这个input流调用ftpClient的storeFile方法，存储文件
                    ftpClient.storeFile(fileItem.getName(),fis);
                }
            }catch (Exception e){
                logger.error("上传文件异常：",e);
                uploaded = false;
            }finally {
                fis.close();
                ftpClient.disconnect();
            }
        }
        return uploaded;
    }

    //封装连接ftp服务器
    private boolean connectionServer(String ip,int port,String user,String pwd){
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user,pwd);

        }catch (Exception e){
            logger.error("连接ftp服务器异常:",e);
        }
        return isSuccess;
    }

    private String ip;
    private int port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
