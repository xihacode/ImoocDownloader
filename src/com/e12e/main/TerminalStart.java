package com.e12e.main;

import com.e12e.service.GetInput;
import org.jsoup.nodes.Document;

/**
 * Created by liukun on 16/8/25.
 */
public class TerminalStart {

    //命令行下载
    public static void main(String[] args) {
        String savePath = "./download/";
        int classNo = GetInput.getInputClassNo();
        int videoDef = GetInput.getInputVideoDef();
        Document doc = Main.getParseHtml(classNo);
        String title = Main.getCourseTitle(doc);
        Main.getVideoFile(title, savePath, classNo, videoDef, doc);

        System.out.println("\n【"
                + title
                + "】课程的下载任务已完成！！！\n已下载到该程序所在目"
                + "录download文件夹下。\n慕课网视频批量下载工具 v1.5  By Coande"
                + "\n----------------------------------------------------------\n");
    }

}
