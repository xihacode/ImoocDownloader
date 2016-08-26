package com.e12e.main;

import org.jsoup.nodes.Document;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by liukun on 16/8/25.
 * GUI启动
 */
public class GUIStart {
    private JTextField courseID;
    private JTextField videoType;
    private JTextField savePath;
    private JButton downloadBtn;
    private javax.swing.JPanel JPanel;
    private JScrollPane scrollPane;
    private JTextArea textArea;

    private GUIStart() {

        ScheduledExecutorService exec =
                Executors.newScheduledThreadPool(1);
        exec.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                OutputStream textAreaStream = new OutputStream() {
                    public void write(int b) throws IOException {
                        textArea.append(String.valueOf((char) b));
                    }

                    public void write(byte b[]) throws IOException {
                        textArea.append(new String(b));
                    }

                    public void write(byte b[], int off, int len) throws IOException {
                        textArea.append(new String(b, off, len));
                    }
                };
                PrintStream myOut = new PrintStream(textAreaStream);
                System.setOut(myOut);
                System.setErr(myOut);

            }
        }, 0, 1, TimeUnit.SECONDS);

        downloadBtn.addActionListener(e -> {
            String path = savePath.getText();
            String id = courseID.getText();
            String type = videoType.getText();

            int classNo = 0;
            int typeNo = 0;
            try {
                classNo = Integer.parseInt(id);

            } catch (Exception ignored) {
                System.out.println("请输入正确的ID:如：http://www.imooc.com/learn/601 或 http://www.imooc.com/view/601，则输入601）");

            }

            try {
                typeNo = Integer.parseInt(type);
            } catch (Exception ignored) {
                System.out.println("请输入正确的要下载的清晰度，【0】普清，【1】高清，【2】超清");
            }
            start(path, classNo, typeNo);
        });


    }

    private static void start(String savePath, int classNo, int videoDef) {
        // String savePath = "./download/";
        // int classNo = GetInput.getInputClassNo();
        // int videoDef = GetInput.getInputVideoDef();
        Document doc = Main.getParseHtml(classNo);
        String title = Main.getCourseTitle(doc);
        Main.getVideoFile(title, savePath, classNo, videoDef, doc);

        System.out.println("\n【"
                + title
                + "】课程的下载任务已完成！！！\n已下载到该程序所在目"
                + "录download文件夹下。\n慕课网视频批量下载工具 v1.5  By Coande"
                + "\n----------------------------------------------------------\n");
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("GUIStart");
        frame.setContentPane(new GUIStart().JPanel);
        frame.setLocationRelativeTo(null);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width / 2;         // 获取屏幕的宽
        int screenHeight = screenSize.height / 2;       // 获取屏幕的高
        int height = frame.getHeight();
        int width = frame.getWidth();
        frame.setLocation(screenWidth - width / 2, screenHeight - height / 2);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

}


