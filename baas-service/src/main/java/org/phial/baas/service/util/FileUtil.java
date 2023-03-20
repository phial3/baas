package org.phial.baas.service.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

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
        return (parent.exists()) || (parent.mkdirs());
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

    public static void deleteFolders(String filePath) {

        Path path = Paths.get(filePath);
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir,
                                                          IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 复制文件夹
     *
     * @param oldDir 原来的目录
     * @param newDir 复制到哪个目录
     */
    public static void copyDir(String oldDir, String newDir) {
        File srcDir = new File(oldDir);
        // 判断文件是否不存在或是否不是文件夹
        if (!srcDir.exists() || !srcDir.isDirectory()) {
            throw new IllegalArgumentException("参数错误");
        }
        File destDir = new File(newDir);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        // 列出目录中的文件
        File[] files = srcDir.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            // 是文件就调用复制文件方法 是目录就继续调用复制目录方法
            if (f.isFile()) {
                copyFile(f, new File(newDir, f.getName()));
            } else if (f.isDirectory()) {
                copyDir(oldDir + File.separator + f.getName(),
                        newDir + File.separator + f.getName());
            }
        }
    }

    /**
     * 复制文件
     *
     * @param oldDir 原来的文件
     * @param newDir 复制到的文件
     */
    public static void copyFile(File oldDir, File newDir) {
        BufferedInputStream bufferedInputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        byte[] b = new byte[1024];
        try {
            // 将要复制文件输入到缓冲输入流
            bufferedInputStream = new BufferedInputStream(new FileInputStream(oldDir));
            // 将复制的文件定义为缓冲输出流
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(newDir));
            // 定义字节数
            int len;
            while ((len = bufferedInputStream.read(b)) > -1) {
                // 写入文件
                bufferedOutputStream.write(b, 0, len);
            }
            //刷新此缓冲输出流
            bufferedOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedInputStream != null) {
                try {
                    // 关闭流
                    bufferedInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void moveFile(String srcPath, String newPath) {
        File srcFile = new File(srcPath);
        File newFile = new File(newPath);
        //需要移动文件的后缀
        String suffix = ".xml";
        if (!srcFile.exists()) {
            System.out.println("文件移动错误！源文件不存在srcPath=" + srcPath);
        }
        //如果目标路径不存在，就创建一个
        if (!newFile.exists()) {
            newFile.mkdirs();
        }
        //oldFiles数组存放的是源文件夹中所有的文件名称
        //newFiles数组放的是目标文件所有文件的名称
        String[] oldFiles = srcFile.list();
        String[] newFiles = newFile.list();
        if (ArrayUtils.isEmpty(oldFiles)) {
            System.out.println("源路径下没有文件srcPath=" + srcPath);
            return;
        }
        //用于记录移动了多少个文件
        int count = 0;
        for (int i = 0; i < oldFiles.length; i++) {
            int foot = oldFiles[i].length();
            int front = foot - suffix.length();
            if (front > 0) {
                //筛选满足文件后缀名的文件
                if (suffix.equals(oldFiles[i].substring(front, foot))) {

                    for (int j = 0; j < newFiles.length; j++) {
                        if (oldFiles[i].equals(newFiles[j])) {
                            System.out.println(newPath + newFiles[j] + "已更新：" + new File(newPath + File.separator + newFiles[j]).delete());
                        }
                    }
                    new File(srcFile + File.separator + oldFiles[i]).renameTo(new File(newPath + File.separator + oldFiles[i]));
                    count++;
                }
            }
        }
        System.out.println("文件移动成功!\n" + srcPath + "目录下的" + suffix + "文件已经移动到了" + newPath + "\n一共转移文件" + count + "个");
    }


    /**
     * 解压zip压缩文件到指定目录
     *
     * @param zipPath zip压缩文件绝对路径
     * @param descDir 指定的解压目录
     */
    public static void unzipFile(String zipPath, String descDir) throws IOException {
        try {
            File zipFile = new File(zipPath);
            if (!zipFile.exists()) {
                throw new IOException("要解压的压缩文件不存在");
            }

            String zipFileName = zipFile.getName().split("\\.zip")[0];
            descDir = descDir  + File.separator + zipFileName;
            File pathFile = new File(descDir);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            InputStream input = Files.newInputStream(Paths.get(zipPath));
            unzipWithStream(input, descDir);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    /**
     * 解压
     *
     * @param inputStream
     * @param descDir
     */
    public static void unzipWithStream(InputStream inputStream, String descDir) {
        if (!descDir.endsWith(File.separator)) {
            descDir = descDir + File.separator;
        }
        File pathFile = new File(descDir);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream, StandardCharsets.UTF_8)) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String zipEntryNameStr = zipEntry.getName();
                String zipEntryName = zipEntryNameStr;
                if (zipEntryNameStr.contains("/")) {
                    String str1 = zipEntryNameStr.substring(0, zipEntryNameStr.indexOf("/"));
                    zipEntryName = zipEntryNameStr.substring(str1.length() + 1);
                }
                String outPath = (descDir + zipEntryName).replace("\\\\", "/");
                File outFile = new File(outPath.substring(0, outPath.lastIndexOf('/')));
                if (!outFile.exists()) {
                    outFile.mkdirs();
                }
                if (new File(outPath).isDirectory()) {
                    continue;
                }
                writeFile(zipInputStream, outPath);
                zipInputStream.closeEntry();
            }
            System.out.println("======解压完成=======");
        } catch (IOException e) {
            System.out.println("压缩包处理异常，异常信息{}" + e);
        }
    }

    //将流写到文件中
    public static void writeFile(ZipInputStream zipInputStream, String filePath) {
        try (OutputStream outputStream = Files.newOutputStream(Paths.get(filePath))) {
            byte[] bytes = new byte[4096];
            int len = -1;
            while ((len = zipInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }
        } catch (IOException ex) {
            System.out.println("writeFile写文件出错");
        }
    }


    //处理文件解压;
    public static void unzip(String zipSourcePath, String targetZipPath) {
        //判断目标地址是否存在，如果没有就创建
        File pathFile = new File(targetZipPath);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        ZipFile zipFile = null;
        try {
            // 若zip中含有中文名文件,换GBK
            // zip = new ZipFile(zipPath, Charset.forName("GBK"));
            zipFile = new ZipFile(zipSourcePath, StandardCharsets.UTF_8);
            //遍历里面的文件及文件夹
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String zipEntryName = entry.getName();
                InputStream in = zipFile.getInputStream(entry);
                //也就是把这个文件加入到目标文件夹路径;
                String outpath = (targetZipPath + zipEntryName).replace("/", File.separator);
                //不存在则创建文件路径
                File file = new File(outpath.substring(0, outpath.lastIndexOf(File.separator)));
                if (!file.exists()) {
                    file.mkdirs();
                }
                File outPathFile = new File(outpath);
                /* String outPathFileName = outPathFile.getName();
                if(outPathFileName.endsWith(".zip")){
                    dealzip(outpath,targetZipPath);
                }*/

                // 文件夹就不解压
                if (outPathFile.isDirectory()) {
                    continue;
                }
                OutputStream out = Files.newOutputStream(Paths.get(outpath));
                byte[] bf = new byte[2048];
                int len;
                while ((len = in.read(bf)) > 0) {
                    out.write(bf, 0, len);
                }
                in.close();
                out.close();
            }
            zipFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}