package com.e12e.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetAttachFile {
    /**
     * 下载课程资料附件
     *
     * @param videos
     * @param className 课程名称，用于获得路径，存放附件
     * @throws IOException
     */
    public static void doGetFile(Elements videos, String className, String savePath)
            throws IOException {
        // 获得要解析的网页文档
        Document doc = null;
        String filePath;
        String[] s;
        String lastName;
        String fileName;

        String[] videoNos;
        String videoNo;
        List<String> pathList = new ArrayList<>();
        for (Element video : videos) {
            videoNos = video.attr("href").split("/");
            // 如果该课程不是视频则不用下载
            if (videoNos.length < 2) {
                continue;
            }
            if (!videoNos[1].equals("video")) {
                continue;
            }
            videoNo = videoNos[2];
            doc = Jsoup.connect("http://www.imooc.com/video/" + videoNo).timeout(10 * 1000).get();
            Elements efilePaths = doc.select(".coursedownload a");
            // 遍历下载所有附件
            for (Element efilePath : efilePaths) {
                filePath = efilePath.attr("href");
                if (!pathList.contains(filePath)) {
                    pathList.add(filePath);
                    s = filePath.split("\\.");
                    lastName = s[s.length - 1];
                    fileName = efilePath.attr("title");
                    DownloadFile.downLoadFromUrl(filePath, fileName + "." + lastName,
                            savePath + className + "/课程资料/");
                    System.out.println("下载课程资料" + fileName + "." + lastName + "成功!");
                }

            }
        }

    }
}
