package com.sinosoft.siava.base64andpic;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import java.io.*;

/**
 * 图片转Base64与Base64转图片工具类
 */
public class Base64AndPic {

    /**
     * 图片转化成base64字符串
     * @param picPath 要转化的图片路径
     * @return 转换后的Base64字符串
     */
    public static String getImageStr(String picPath)
    {//将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        String imgFile = picPath;//待处理的图片
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try
        {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        //对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);//返回Base64编码过的字节数组字符串
    }

    /**
     * base64字符串转化成图片
     * @param imgStr 要转换的Base64字符串
     * @param file 转换后输出的文件
     * @return 转换是否成功
     */
    public static boolean GenerateImage(String imgStr,File file)
    {
        boolean flag = false;
        //对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null || imgStr.equals("")) { //图像数据为空
            throw new RuntimeException("图像数据为空，请确认");
        }
        BASE64Decoder decoder = new BASE64Decoder();
        try
        {
            //Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for(int i=0;i<b.length;++i)
            {
                if(b[i]<0)
                {//调整异常数据
                    b[i]+=256;
                }
            }
            //生成jpeg图片
            OutputStream out = new FileOutputStream(file);
            out.write(b);
            out.flush();
            out.close();
            flag = true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return flag;
    }

}
