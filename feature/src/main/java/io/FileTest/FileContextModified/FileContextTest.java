package io.FileTest.FileContextModified;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FileContextTest {

    public static void main(String[] args) {

        LinkedHashMap<String, String> properties = new LinkedHashMap<>();
        properties.put("name1", "value1");
        properties.put("name2", "value2");
        properties.put("", "value2");
        properties.put(null, "value2");
        System.out.println(properties);

        RandomAccessFile javaConfigFile = null;
        try {

            String javaConfigStr = "RandomAccessFile.log";
            javaConfigFile = new RandomAccessFile(javaConfigStr, "rw");
            insertMapForFile(javaConfigFile, properties);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != javaConfigFile) {
                try {
                    javaConfigFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    private static void insertMapForFile(RandomAccessFile javaConfigFile, LinkedHashMap<String, String> properties) throws IOException {
        LinkedHashMap<String, String> newProperties = filterMapNullKey(properties);
        String                        context       = convertMapToContext(newProperties);
        System.out.println(context);
        long index = findEndOfJavaConfig(javaConfigFile);

        File temp = null;
        try {

            temp = File.createTempFile("temp", ".bak");
            System.out.println(temp.getCanonicalPath());

            FileInputStream  fis    = new FileInputStream(temp);
            FileOutputStream fos    = new FileOutputStream(temp);
            byte[]           buffer = new byte[1024];
            int              length = 0;

            javaConfigFile.seek(index);
            //读写前最好指定下文件指针的位置，以免出现杜写的内容不正确的奇怪错误
            while (-1 != (length = javaConfigFile.read(buffer, 0, buffer.length))) {
                fos.write(buffer, 0, length);
            }

            javaConfigFile.seek(index);
            javaConfigFile.write(context.getBytes());


            while (-1 != (length = fis.read(buffer, 0, buffer.length))) {
                javaConfigFile.write(buffer, 0, length);
            }

        } finally {
            if (null != temp) {
                temp.delete();
            }
        }

    }

    private static LinkedHashMap<String, String> filterMapNullKey(LinkedHashMap<String, String> properties) {
        List<String> toDeleteKey = new ArrayList<>();
        for (String key : properties.keySet()) {
            if (null == key || key.isEmpty()) {
                toDeleteKey.add(key);
                System.out.println("Invalid Property:【" + key + "】, Ignored!");
                continue;
            }
        }
        for (String key : toDeleteKey) {
            properties.remove(key);
        }
        return properties;
    }

    private static String convertMapToContext(LinkedHashMap<String, String> properties) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            sb.append(System.lineSeparator());
            sb.append(entry.toString());
        }
        return sb.toString();
    }

    private static long findEndOfJavaConfig(RandomAccessFile javaConfigFile) throws IOException {
        long index = javaConfigFile.length();
        return index;
    }
}
