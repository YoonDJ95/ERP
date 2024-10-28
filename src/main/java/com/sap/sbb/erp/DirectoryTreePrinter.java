package com.sap.sbb.erp;
import java.io.File;

public class DirectoryTreePrinter {

    // 제외할 디렉터리 및 파일 이름 목록
    private static final String[] EXCLUDED_ITEMS = {".git", ".idea", "node_modules", "target","build",".gradle","gradle",".gitattributes",".gitignore","notice.txt"};

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
                if (shouldExclude(file.getName())) { // 제외 항목 검사
                    continue; // 제외할 항목이면 건너뜀
                }

                if (file.isDirectory()) {
                    printDirectoryTree(file, indent); // 디렉터리라면 재귀적으로 호출
                } else {
                    System.out.println(indent + "+-- " + file.getName()); // 파일 출력
                }
            }
        }
    }

    // 제외할 파일 및 디렉터리 이름인지 확인하는 메서드
    private static boolean shouldExclude(String name) {
        for (String excluded : EXCLUDED_ITEMS) {
            if (name.equalsIgnoreCase(excluded)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        // 현재 작업 중인 폴더의 전체 구조 출력
        File currentDirectory = new File("."); // 현재 디렉터리
        printDirectoryTree(currentDirectory, "");
    }
}
