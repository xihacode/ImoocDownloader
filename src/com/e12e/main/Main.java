package com.e12e.main;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.e12e.service.DownloadFile;
import com.e12e.service.GetAttachFile;
import com.e12e.service.GetInfo;
import com.e12e.service.GetInput;
import com.e12e.service.GetList;

/**
 * 主类
 *
 * @author Coande
 */
public class Main {


//    public static void main(String[] args) {
//
//        String savePath = "./download/";
//        int classNo = GetInput.getInputClassNo();
//        int videoDef = GetInput.getInputVideoDef();
//        Document doc = getParseHtml(classNo);
//        String title = getCourseTitle(doc);
//        getVideoFile(title, savePath, classNo, videoDef, doc);
//
//        System.out.println("\n【"
//                + title
//                + "】课程的下载任务已完成！！！\n已下载到该程序所在目"
//                + "录download文件夹下。\n慕课网视频批量下载工具 v1.5  By Coande"
//                + "\n----------------------------------------------------------\n");
//    }


//       static void start(String savePath, int classNo, int videoDef) {
//        // String savePath = "./download/";
//        // int classNo = GetInput.getInputClassNo();
//        // int videoDef = GetInput.getInputVideoDef();
//        Document doc = getParseHtml(classNo);
//        String title = getCourseTitle(doc);
//        getVideoFile(title, savePath, classNo, videoDef, doc);
//
//        System.out.println("\n【"
//                + title
//                + "】课程的下载任务已完成！！！\n已下载到该程序所在目"
//                + "录download文件夹下。\n慕课网视频批量下载工具 v1.5  By Coande"
//                + "\n----------------------------------------------------------\n");
//    }

    /**
     * 获得课程的标题
     *
     * @param doc
     * @return
     */
    static String getCourseTitle(Document doc) {
        String title;// 获得课程标题：
        title = doc.getElementsByTag("h2").html();
        // 过滤文件夹非法字符
        title = title.replaceAll("[\\\\/:\\*\\?\"<>\\|]", "#");
        return title;
    }

    /**
     * 获得视频文件
     *
     * @param title
     * @param savePath
     * @param classNo
     * @param videoDef
     * @param doc
     */
    static void getVideoFile(String title, String savePath, int classNo, int videoDef, Document doc) {

        String path = savePath + title + "/";
        Elements videos = doc.select(".video a");
        if (title.equals("") && videos.size() == 0) {
            System.out.println("抱歉，没有该课程！\n");
            return;
        }
        try {
            GetAttachFile.doGetFile(videos, title, savePath);
        } catch (IOException e) {
            System.out.println("下载课程资料附件时出现异常！\n");
        }
        System.out.println("\n正在下载，请耐心等待…\n");
        String[] videoNos;
        String videoName;
        String videoNo;
        int currentCount = 0;
        getCourseInfoFile(title, savePath, classNo, videos);
        for (Element video : videos) {
            currentCount++;
            videoNos = video.attr("href").split("/");
            // 如果该课程不是视频则不用下载
            if (videoNos.length < 2) {
                continue;
            }
            if (!videoNos[1].equals("video")) {
                continue;
            }
            videoName = getVideoName(video);

            videoNo = videoNos[2];
            String downloadPath = getVideoPath(currentCount, videoDef, videoName, videoNo);
            if (downloadPath == null) continue;

            // 进行下载
            try {
                DownloadFile.downLoadFromUrl(downloadPath, videoName + ".mp4", path);
                System.out.println("【" + currentCount + "】" + videoName
                        + " \t下载成功！");
            } catch (IOException e) {
                System.out.println("【" + currentCount + "】：\t" + videoName
                        + " \t网络异常，下载失败！");
            }

        }

    }

    /**
     * 获得视频下载地址
     *
     * @param currentCount
     * @param videoDef
     * @param videoName
     * @param videoNo
     * @return
     */
    private static String getVideoPath(int currentCount, int videoDef, String videoName, String videoNo) {
        Document jsonDoc;
        String jsonData;
        JSONObject jsonObject;
        JSONArray mpath;// 获取视频下载地址
        try {
            jsonDoc = Jsoup.connect("http://www.imooc.com/course/ajaxmediainfo/?mid=" + videoNo + "&mode=flash").timeout(10 * 1000).get();
        } catch (IOException e) {
            System.out.println("【" + currentCount + "】" + videoName
                    + "\t网络异常，地址获取失败！");
            return null;
        }

        jsonData = jsonDoc.text();
        jsonObject = new JSONObject(jsonData);
        mpath = jsonObject.optJSONObject("data")
                .optJSONObject("result").optJSONArray("mpath");
        String downloadPath = mpath.getString(videoDef).trim();
        return downloadPath;
    }

    /**
     * 获得视频名称
     *
     * @param video
     * @return
     */
    private static String getVideoName(Element video) {
        String videoName = video.text();
        if (videoName.contains("开始学习")) {
            videoName = videoName.substring(0, videoName.length() - 5);
        }
        return videoName;
    }

    /**
     * 得到课程信息 平生成文件
     *
     * @param title
     * @param savePath
     * @param classNo
     * @param videos
     */
    private static void getCourseInfoFile(String title, String savePath, int classNo, Elements videos) {
        File file = new File(savePath);
        file.mkdirs();
        // 获得课程信息进行保存
        try {
            GetInfo.doGetInfo(classNo, title, savePath);

            System.out.println("course_info.txt\t生成成功！");
        } catch (Exception e2) {
            System.out.println("生成course_info.txt时出现异常！");
            try {
                GetInfo.doGetInfo(classNo, title, savePath);
            } catch (IOException e) {
                System.out.println("生成course_info.txt时出现异常！");
            }
        }

        // 生成course_list.html
        try {
            GetList.doGetList(videos, title, savePath);
            System.out.println("course_list.html\t生成成功！");
        } catch (Exception e1) {
            try {
                GetList.doGetList(videos, title, savePath);
            } catch (Exception e) {
                System.out.println("生成course_list.html时出现异常！");
            }
        }
    }

    /**
     * 获得要解析的网页文档
     *
     * @param classNo
     * @return
     */
    static Document getParseHtml(int classNo) {
        try {
            return Jsoup.connect("http://www.imooc.com/learn/" + classNo).get();
        } catch (IOException e) {
            System.out.println("获取课程信息时网络异常！可以稍后重试~\n");
            return null;
        }
    }

}
