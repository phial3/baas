package com.phial.baas.manager.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FileUtil {

    public static String execute(String command) {
        InputStream is = null;
        String s;
        StringBuilder sb = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor(10, TimeUnit.SECONDS);
            // 返回 0： 成功 其他：失败
            int exitValue = process.exitValue();
            if (exitValue != 0) {
                StringBuilder errMsg = new StringBuilder();
                is = process.getErrorStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = br.readLine()) != null) {
                    errMsg.append(line).append("\n");
                }
                throw new RuntimeException(errMsg.toString());
            }
            is = process.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
            while ((s = bufferedReader.readLine()) != null) {
                sb.append(s);
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("FileUtil execute err:{}", e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (is != null) is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeFile(String content, String fileName) {
        try {
            File dir = new File(fileName.substring(0, fileName.lastIndexOf("/")));
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fileWriter);
            bw.write(content);
            bw.close();
            System.out.println("writeFile finish! fileName=" + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static boolean mkdirParent(File file) {
        File parent = file.getParentFile();
        if ((!parent.exists()) && (!parent.mkdirs())) {
            return false;
        }
        return true;
    }

    public static boolean save2File(byte[] content, String fileName) {
        OutputStream fos = null;
        try {
            File file = new File(fileName);
            if (!mkdirParent(file)) {
                return false;
            }
            fos = Files.newOutputStream(file.toPath());
            fos.write(content);
            fos.flush();
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static byte[] readFileToByte(String fileName) {
        byte[] fileBytes = null;
        try {
            FileInputStream inputStream = new FileInputStream(fileName);
            fileBytes = IOUtils.toByteArray(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileBytes;
    }

    public static String readFile(String fileName) {
        StringBuilder result = new StringBuilder();
        File file = new File(fileName);
        if (!file.exists()) {
            return null;
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                result.append(tempString).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return result.toString();
    }

}