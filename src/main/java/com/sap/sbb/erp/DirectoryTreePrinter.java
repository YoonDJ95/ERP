package com.sap.sbb.erp;
import java.io.File;

public class DirectoryTreePrinter {

    public static void printDirectoryTree(File folder, String indent) {
        if (!folder.isDirectory()) {
            System.out.println("Not a directory: " + folder.getAbsolutePath());
            return;
        }

        System.out.println(indent + "+-- " + folder.getName());
        indent += "    "; // 하위 항목 들여쓰기

        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    printDirectoryTree(file, indent); // 디렉터리라면 재귀적으로 호출
                } else {
                    System.out.println(indent + "+-- " + file.getName()); // 파일 출력
                }
            }
        }
    }

    public static void main(String[] args) {
        // 현재 작업 중인 폴더의 전체 구조 출력
        File currentDirectory = new File("."); // 현재 디렉터리
        printDirectoryTree(currentDirectory, "");
    }
}
