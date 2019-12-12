package com.sinosoft.siava.compression;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipCompressing {

    private int k = 1; // 定义递归次数变量

    public void unzip(String zipfilepath, String fileoutputdir) {
        long startTime=System.currentTimeMillis();
        try {
            ZipInputStream Zin=new ZipInputStream(new FileInputStream(zipfilepath));//输入源zip路径
            BufferedInputStream Bin=new BufferedInputStream(Zin);
            File Fout=null;
            ZipEntry entry;
            try {
                while((entry = Zin.getNextEntry())!=null && !entry.isDirectory()){
                    Fout=new File(fileoutputdir,entry.getName());
                    if(!Fout.exists()){
                        (new File(Fout.getParent())).mkdirs();
                    }
                    FileOutputStream out=new FileOutputStream(Fout);
                    BufferedOutputStream Bout=new BufferedOutputStream(out);
                    int b;
                    while((b=Bin.read())!=-1){
                        Bout.write(b);
                    }
                    Bout.close();
                    out.close();
                    System.out.println(Fout+"解压成功");
                }
                Bin.close();
                Zin.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        long endTime=System.currentTimeMillis();
        System.out.println("耗费时间： "+(endTime-startTime)+" ms");
    }

    public void zip(String zipfile, String inputfilepath) throws Exception {
        System.out.println("压缩中...");
        File file = new File(inputfilepath);
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));
        BufferedOutputStream bo = new BufferedOutputStream(out);
        zip(out, file, file.getName(), bo);
        bo.close();
        out.close(); // 输出流关闭
        System.out.println("压缩完成");
    }

    public void zip(ZipOutputStream out, File f, String base, BufferedOutputStream bo) throws IOException {
        if (f.isDirectory()){
            File[] fl = f.listFiles();
            if (fl.length == 0){
                out.putNextEntry(new ZipEntry(base + "/")); // 创建zip压缩进入点base
                System.out.println(base + "/");
            }
            for (int i = 0; i < fl.length; i++) {
                zip(out, fl[i], base + "/" + fl[i].getName(), bo); // 递归遍历子文件夹
            }
            System.out.println("第" + k + "次递归");
            k++;
        } else {
            out.putNextEntry(new ZipEntry(base)); // 创建zip压缩进入点base
            System.out.println(base);
            FileInputStream in = new FileInputStream(f);
            BufferedInputStream bi = new BufferedInputStream(in);
            int b;
            while ((b = bi.read()) != -1) {
                bo.write(b); // 将字节流写入当前zip目录
            }
            bi.close();
            in.close(); // 输入流关闭
        }
    }

    public static void main(String[] args) {
        ZipCompressing zipCompressing = new ZipCompressing();
        try {
//            zipCompressing.zip("/Users/zhangxin/Documents/test.zip", "/Users/zhangxin/Documents/个人文件");
            zipCompressing.unzip("/Users/zhangxin/Documents/test.zip", "/Users/zhangxin/Documents/test/");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
