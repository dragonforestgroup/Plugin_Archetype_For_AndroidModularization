package com.dragonforest.plugin.archetype.utils;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.Collection;
import java.util.List;

public class FileUtil {
    public static boolean copyFile(String srcFile, String dstFile) {
        try {
            FileUtils.copyFile(new File(srcFile), new File(dstFile));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean copyDir(String srcDir, String dstDir) {
        try {
            FileUtils.copyDirectory(new File(srcDir), new File(dstDir));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean copyDirToDir(String srcDir, String dstDir) {
        try {
            FileUtils.copyDirectoryToDirectory(new File(srcDir), new File(dstDir));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 剪切文件
     *
     * @param srcDir
     * @param dstDir
     * @return
     */
    public static boolean moveDirToDir(String srcDir, String dstDir) {
        try {
            FileUtils.moveDirectoryToDirectory(new File(srcDir), new File(dstDir), true);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static Collection<File> listFiles(String dir, String[] extentions, boolean recusive) {
        return FileUtils.listFiles(new File(dir), extentions, recusive);
    }

    /**
     * 替换文件中的字符串
     * 指定文件格式为UTF-8
     *
     * @param filePath
     * @param oldText
     * @param newText
     * @return
     */
    public static boolean readAndReplace(String filePath, String oldText, String newText) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }
        BufferedReader bufReader = null;
        BufferedWriter bufWriter = null;
        try {
            // 读取内容
            StringBuilder stringBuilder = new StringBuilder();
            bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath)), "UTF-8"));
            String line = null;
            while ((line = bufReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            bufReader.close();
            String content = stringBuilder.toString();

            // 替换
            String newContent = content.replace(oldText, newText);

            // 写入新内容
            bufWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filePath), false), "UTF-8"));
            bufWriter.write(newContent);
            bufWriter.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 清除文件夹下的所有文件和文件夹
     *
     * @param dir
     * @return
     */
    public static boolean cleanDir(String dir) {
        try {
            FileUtils.cleanDirectory(new File(dir));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除文件夹
     *
     * @param dir
     * @return
     */
    public static boolean deleteDir(String dir) {
        try {
            FileUtils.deleteDirectory(new File(dir));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 创建目录
     *
     * @param dir
     * @return
     */
    public static boolean mkDir(String dir) {
        try {
            FileUtils.forceMkdir(new File(dir));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 写入文件
     *
     * @param content
     * @param path
     * @param append true时追加 false时覆盖     但是追加时会另起一行进行追加
     * @return
     */
    public static boolean writeFile(String content, String path, boolean append) {
        try {
            FileUtils.write(new File(path), content, "UTF-8", append);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 追加进文件 不会另起一行
     * @param append
     * @param path
     * @return
     */
    public static boolean appendFile(String append,String path){
        try {
            List<String> lines = FileUtils.readLines(new File(path), "UTF-8");
            StringBuilder sb=new StringBuilder();
            for (String line : lines) {
                sb.append(line);
            }
            sb.append(append);
            return writeFile(sb.toString(),path,false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
