package com.e12e.service;

import org.omg.CORBA.INTERNAL;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by liukun on 16/8/25.
 */

class SingleThread {

    static void singleThread(String urlStr, File file) throws IOException {
        if (!file.exists()) {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置超时为10秒
            conn.setConnectTimeout(10 * 1000);
            // 防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36");

            // 得到输入流
            InputStream inputStream = conn.getInputStream();
            int length = (int) conn.getContentLength();//这个就是下载的文件（不单指文件）大小
            ConsoleProgressBar cpb = new ConsoleProgressBar(0, length, 20, '=');
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int len;
            int count =0;
            while ((len = inputStream.read(temp)) != -1) {
                fos.write(temp, 0, len);
                count = count+len;
                cpb.show(count);
            }

            fos.close();
            inputStream.close();
        }
    }
}
