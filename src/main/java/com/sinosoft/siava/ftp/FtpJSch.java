package com.sinosoft.siava.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.UUID;
import java.util.Vector;

import com.jcraft.jsch.*;

public class FtpJSch {

    private static ChannelSftp sftp = null;

    //账号
    private static String user = "ftpuser";
    //主机ip
    private static String host =  "192.168.78.128";
    //密码
    private static String password = "ftpuser";
    //端口
    private static int port = 22;
    //上传地址
    private static String directory = "/home/www/imgs";
    //下载目录
    private static String saveFile = "D:\\VMware\\XuNiJi\\imgs";

    public static FtpJSch getConnect(String ftphost, String ftpusername, String ftppassword, int ftpport){
        FtpJSch ftp = new FtpJSch();
        try {
            JSch jsch = new JSch();

            //获取sshSession  账号-ip-端口
            Session sshSession =jsch.getSession(ftpusername, ftphost, ftpport);
            //添加密码
            sshSession.setPassword(ftppassword);
            Properties sshConfig = new Properties();
            //严格主机密钥检查
            sshConfig.put("StrictHostKeyChecking", "no");

            sshSession.setConfig(sshConfig);
            //开启sshSession链接
            sshSession.connect();
            //获取sftp通道
            Channel channel = sshSession.openChannel("sftp");
            //开启
            channel.connect();
            sftp = (ChannelSftp) channel;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ftp;
    }

    /**
     *
     * @param uploadFile 上传文件的路径
     * @return 服务器上文件名
     */
    public String upload(String uploadFile) {
        File file = null;
        String fileName = null;
        try {
            sftp.cd(directory);
            file = new File(uploadFile);
            //获取随机文件名
            fileName  = UUID.randomUUID().toString() + file.getName().substring(file.getName().length()-5);
            //文件名是 随机数加文件名的后5位
            sftp.put(new FileInputStream(file), fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file == null ? null : fileName;
    }


    public void download(String ftpdir, String downloadFileName, String localfilepath) {
        try {
            sftp.cd(ftpdir);

            File file = new File(localfilepath);

            sftp.get(downloadFileName, new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void delete(String deleteFile) {
        try {
            sftp.cd(directory);
            sftp.rm(deleteFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Vector listFiles(String directory)
            throws SftpException {
        return sftp.ls(directory);
    }

}