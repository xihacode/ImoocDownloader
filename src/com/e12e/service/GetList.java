package com.e12e.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetList {

    /**
     * 生成course_list.html文件
     *
     * @throws Exception
     */
    public static void doGetList(Elements videos, String className, String savePath) {

        int curruntGlobalCount = 0;
        String[] videoNos;
        String url;
        String codeName;
        String videoName;
        // 文件保存位置
        File saveDir = new File(savePath + className);
        if (!saveDir.exists()) {
            saveDir.mkdir();
        }
        FileWriter htmlWriter = null;
        try {
            htmlWriter = new FileWriter(saveDir.getPath() + "/course_list.html");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        String htmlText = "<!DOCTYPE html><html><head>"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"
                + "<meta charset=\"utf-8\">"
                + "<title>课程列表</title><link type=\"text/css\" rel=\"stylesheet\" href=\""
                + "http://apps.bdimg.com/libs/jquerymobile/1.4.5/jquery.mobile-1.4.5.min.css\" />"
                + "</head><body><table  data-role=table data-mode=reflow class=\"ui-responsive table-stroke\">"
                + "<thead><th>No.</th><th>课程名称</th><th>在线地址</th></thead><tbody>";

        // 拼凑HTML
        for (Element video : videos) {
            curruntGlobalCount++;

            videoNos = video.attr("href").split("/");
            url = "http://www.imooc.com" + video.attr("href");

            if (videoNos.length < 2) {
                continue;
            }
            // 记录在html中：
            if (!videoNos[1].equals("video")) {
                codeName = getVideoName(video);
//                System.out.println("code:" + video.text());
                htmlText += "<tr><td>" + curruntGlobalCount + "</td><td>"
                        + codeName + "</td><td><a href='" + url
                        + "'>去慕课网练习*</a></td></tr>\n";
            } else {
                // 获得视频课程名称
                videoName = getVideoName(video);
                htmlText += "<tr><td>" + curruntGlobalCount + "</td><td>"
                        + videoName + "</td><td><a href='" + url
                        + "'>去慕课网观看</a></td></tr>\n";
            }

        }

        htmlText += "</tbody></table><script src=\"http://apps.bdimg.com/libs/jquery/2.1.4/jquery.min.js\">"
                + "</script><script src=\"http://apps.bdimg.com/libs/jquerymobile/1.4.5/jquery.mobile-1.4.5.min.js\"></script></body></html>";
        try {
            htmlWriter.write(htmlText);
        } catch (IOException e) {
            System.out.println("2:" + e.getMessage());
            e.printStackTrace();
        }
        try {
            htmlWriter.close();
        } catch (IOException e) {
            System.out.println("3:" + e.getMessage());
            e.printStackTrace();
        }

    }

    private static String getVideoName(Element video) {
        String videoName;
        videoName = video.text();
        if (videoName.contains("开始学习")) {
            videoName = videoName.substring(0, videoName.length() - 5);
        }
        return videoName;
    }
}
