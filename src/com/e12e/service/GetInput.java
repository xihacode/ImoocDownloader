package com.e12e.service;

import java.util.Scanner;

public class GetInput {
	private static Scanner scanner;

	public static int getInputClassNo() {
		int classNo = 0;
		scanner = new Scanner(System.in);
		while (true) {
			System.out.println("请输入需要下载的课程编号（如：http://www.imooc.com/learn/601 或 http://www.imooc.com/view/601，则输入601）：");
			try {
				classNo = scanner.nextInt();
			} catch (Exception e) {
				System.out.println("课程编号填写错误，只能输入整数！\n");
				scanner.nextLine();
				continue;
			}
			return classNo;
		}
	}

	public static int getInputVideoDef() {
		int videoDef = 0;
		scanner = new Scanner(System.in);
		while (true) {
			System.out.println("请输入要下载的清晰度，【0】普清，【1】高清，【2】超清：");
			try {
				videoDef = scanner.nextInt();
				if (videoDef > 2 || videoDef < 0) {
					throw new Exception();
				}
			} catch (Exception e) {
				System.out.println("只能输入【0】【1】【2】中的一个数！\n");
				scanner.nextLine();
				continue;
			}
			return videoDef;
		}
	}
}
