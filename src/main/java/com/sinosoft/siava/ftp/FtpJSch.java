package com.sinosoft.siava.ftp;

import java.io.*;
import java.util.Properties;
import java.util.UUID;
import java.util.Vector;

import com.jcraft.jsch.*;

public class FtpJSch {

    private static ChannelSftp sftp = null;

    /**
     * 获取FTP连接实力方法
     * @param ftphost FTP服务器地址
     * @param ftpusername FTP服务器用户名
     * @param ftppassword FTP服务器密码
     * @param ftpport FTP服务器端口
     * @return FtpJSch对象
     */
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
     * 像FTP服务器上传文件
     * @param uploadFile 本地文件全路径
     * @param directory 要上传到FTP服务器的路径文件夹路径
     * @param filename 要上传到FTP服务器的文件名
     * @return 上传成功的文件名
     */
    public String upload(String uploadFile, String directory, String filename) {
        File file = null;
        String fileName = null;
        InputStream in = null;
        try {
            sftp.cd(directory);
            file = new File(uploadFile);
            //文件名是 随机数加文件名的后5位
            in = new FileInputStream(file);
            sftp.put(in, filename);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException io) {
                    io.printStackTrace();
                } finally {
                    in = null;
                }
            }
        }
        return file == null ? null : fileName;
    }


    /**
     * 从FTP服务器下载文件
     * @param ftpdir 要从FTP下载的文件夹路径
     * @param downloadFileName 要从FTP下载的文件的文件名
     * @param localfilepath 要保存到本地的全路径
     */
    public void download(String ftpdir, String downloadFileName, String localfilepath) {
        FileOutputStream out = null;
        try {
            sftp.cd(ftpdir);

            File file = new File(localfilepath);
            out = new FileOutputStream(file);

            sftp.get(downloadFileName, out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException io) {
                    io.printStackTrace();
                } finally {
                    out = null;
                }
            }
        }
    }

    public void delete(String directory, String deleteFile) {
        try {
            sftp.cd(directory);
            sftp.rm(deleteFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 列出FTP服务器指定目录下所有文件
     * @param directory 需要列出的文件夹路径
     * @return 对象集合
     * @throws SftpException FTP异常
     */
    public Vector listFiles(String directory)
            throws SftpException {
        return sftp.ls(directory);
    }

    /**
     * 断开FTP连接
     */
    public void disconnect() {
        sftp.quit();
        sftp.exit();
        sftp.disconnect();
    }
}