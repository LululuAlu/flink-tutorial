package flink.totorial;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: wuxuyang
 * @Date: 2019-03-07
 */
public class FileUtil {

    public static String readFileFromClassPath(String filePath) throws IOException {
        //xxx.class.getClassLoader().getResourceAsStream("文件")，返回一个InputStream类型的供读取文件
        InputStream inputStream = FileUtil.class.getClassLoader().getResourceAsStream(filePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf8"));
        StringBuilder bf = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            bf.append(line);
        }
        return bf.toString();
    }

    public static List<String> readFileFromClassPathMultiLine(String filePath) throws IOException {
        //xxx.class.getClassLoader().getResourceAsStream("文件")，返回一个InputStream类型的供读取文件
        List<String> strings=new ArrayList<>();
        InputStream inputStream = FileUtil.class.getClassLoader().getResourceAsStream(filePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf8"));
        StringBuilder bf = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            strings.add(line);
        }
        return strings;
    }

    /**
     * 获取目录下的所有文件名称 没有递归子目录
     * @param dirPath  文件目录
     * @return 文件名称集合
     */
    public static List<String> readFileNameFromDir(String dirPath) {
        URL url = FileUtil.class.getClassLoader().getResource(dirPath);
        if (null == url) {
            return null;
        }
        String[] list = new File(url.getPath()).list();
        if (null == list) {
            return null;
        }
        return Arrays.asList(list);
    }

    public static String readFile(String filePath) throws IOException {
        //xxx.class.getClassLoader().getResourceAsStream("文件")，返回一个InputStream类型的供读取文件
        InputStream inputStream = new FileInputStream(new File(filePath));
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf8"));
        StringBuilder bf = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            bf.append(line);
        }
        return bf.toString();
    }
}
