package com.e12e.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * http://blog.csdn.net/java4found/article/details/8656695
 * http://blog.csdn.net/java4found/article/details/8661134
 * 具体思路：
 * 1、文件分块。 文件分块大小block = 文件大小 % 线程数 == 0 ? 文件大小 / 线程数 ： 文件大小 / 线程数 + 1 ;
 * 2、确定每一个线程下载对应用文件的位置指针。
 * 现假设为每个线程分别编号threadid     0 1 2 3 4
 * 则第一个线程负责的下载位置是： 0*分块大小  到  (0+1)*分块大小-1
 * 第二个线程负责的下载位置是： 1*分块大小   到  (1+1)*分块大小-1
 * 即有开始下载位置 start = threadid*block;
 * 即有结束下载位置 end = (threadid+1)*block-1;
 * 3、最后通过设置连接的属性， conn.setRequestProperty("Range", "bytes=" + start + "-" + end);
 * Created by liukun on 16/8/25.
 */
class MultiThread {

    /**
     * 下载文件
     */
    void download(String filePath, File file, int threadNum) {

        HttpURLConnection conn = null;
        RandomAccessFile raf = null;
        try {
            if (!file.exists()){
                file.createNewFile();
            }
            //通过下载路径获取连接
            URL url = new URL(filePath);
            conn = (HttpURLConnection) url.openConnection();
            //设置连接的相关属性
            conn.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36");
            conn.setRequestMethod("GET");
            conn.setReadTimeout(5000);
            //判断连接是否正确。
            if (conn.getResponseCode() == 200) {
                // 获取文件大小。
                int fileSize = conn.getContentLength();
//                //得到文件名
//                String fileName = getFileName(filePath);
//                //根据文件大小及文件名，创建一个同样大小，同样文件名的文件
//                File file = new File(destination + File.separator + fileName);
                if (file.exists()) {
                    raf = new RandomAccessFile(file, "rw");
                    raf.setLength(fileSize);
                    raf.close();
                    // 将文件分成threadNum = 5份。
                    int block = fileSize % threadNum == 0 ? fileSize / threadNum
                            : fileSize / threadNum + 1;
                    for (int threadId = 0; threadId < threadNum; threadId++) {
                        //传入线程编号，并开始下载。
                        new DownloadThread(threadId, block, file, url, threadNum).start();
                    }

                }

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }


}

//文件下载线程
class DownloadThread extends Thread {
    private int start, end, threadId;
    private File file = null;
    private URL url = null;
    private int count;
    HashMap<String,Integer> map = new HashMap<>();


    public DownloadThread(int threadId, int block, File file, URL url, int count) {
        this.count = count;
        this.threadId = threadId;
        start = block * threadId;
        end = block * (threadId + 1) - 1;
        this.file = file;
        this.url = url;
        map.put(file.getName(),0);
    }

    public void run() {
        HttpURLConnection conn = null;
        RandomAccessFile raf = null;
        InputStream inStream = null;
        try {
            //获取连接并设置相关属性。
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(5000);
            //此步骤是关键。
            conn.setRequestProperty("Range", "bytes=" + start + "-" + end);
            if (conn.getResponseCode() == 206) {
                raf = new RandomAccessFile(file, "rw");
                //移动指针至该线程负责写入数据的位置。
                raf.seek(start);
                //读取数据并写入
                inStream = conn.getInputStream();
                byte[] b = new byte[1024];
                int len = 0;
                while ((len = inStream.read(b)) != -1) {
                    raf.write(b, 0, len);
                }
                System.out.println("线程" + threadId + "下载完毕");

//                //通过文件名和对应的文件大小来检测进度
//                map.entrySet().stream().filter(entry
//                        -> file.getName().equals(entry.getKey())).filter(entry
//                        -> count == entry.getValue()).forEach(entry
//                        -> System.out.println(file.getName() + "下载完毕"));

                //  System.out.println("threadId:"+threadId+" count:"+count);
//                if (threadId >= count - 1) {
//                    System.out.println("ddddd");
//                    System.out.println(file.getName());
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
