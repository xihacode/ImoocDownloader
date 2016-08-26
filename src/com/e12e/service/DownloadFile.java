package com.e12e.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 下载视频类
 *
 * @author Coande
 */
public class DownloadFile {
    public static void downLoadFromUrl(String urlStr, String fileName,
                                       String savePath) throws IOException {
        File saveDir = new File(savePath);

        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        File file = new File(saveDir + File.separator + fileName);
       //new MultiThread().download(urlStr, file, 5);

////         文件不存在才进行下载
        SingleThread.singleThread(urlStr, file);
    }



}
