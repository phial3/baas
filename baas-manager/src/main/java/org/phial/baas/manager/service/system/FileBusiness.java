package org.phial.baas.manager.service.system;

import org.apache.commons.lang3.StringUtils;
import org.mayanjun.core.Assert;
import org.mayanjun.core.ServiceException;
import org.mayanjun.mybatisx.api.query.Query;
import org.mayanjun.mybatisx.api.query.QueryBuilder;
import org.mayanjun.mybatisx.dal.dao.BasicDAO;
import org.mayanjun.util.MimeUtils;
import org.phial.baas.manager.config.app.AppConfig;
import org.phial.baas.manager.util.NetUtils;
import org.phial.baas.service.domain.entity.sys.FileMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;

/**
 * 文件管理
 * @since 2019-07-06
 * @author mayanjun
 */
@Component
public class FileBusiness implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(FileBusiness.class);

    public static final String BUCKET_NAME = "bucket";

    private static final int STORAGE_BUCKETS = 1000;

    private AppConfig config;
    private BasicDAO dao;

    private File dirFile;
    private File clientDirFile;

    public FileBusiness(AppConfig config, BasicDAO dao) {
        this.config = config;
        this.dao = dao;
    }

    private File dir(String fileName) {
        int code = Math.abs(fileName.toLowerCase().hashCode());
        int mod = code % STORAGE_BUCKETS;
        File dir = new File(dirFile, BUCKET_NAME + "_" + mod);
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    /**
     * 上传文件
     * @param fileName 标准的文件名
     * @param inputStream 文件输入流
     * @return
     * @throws Exception
     */
    public String upload(String fileName, InputStream inputStream, String tag) throws Exception {
        File outFile = new File(dir(fileName), fileName);
        int size = FileCopyUtils.copy(inputStream, new FileOutputStream(outFile));
        LOG.info("File upload done: size={}, copied={}", fileName, size);
        saveFileMeta(outFile, tag);
        return toURL(fileName);
    }

    /**
     * 上传来自网页多媒体的文件
     * @param file 文件
     * @param tag 文件标签
     * @param ratio0
     * @param ratio1
     * @return
     * @throws Exception
     */
    public FileMeta upload(MultipartFile file, String tag, Float ratio0, Float ratio1) throws Exception {
        Assert.isTrue(file != null && !file.isEmpty(), "文件不能为空");
        String newFileName = createNewFileName(file.getOriginalFilename());
        File outFile = localFile(newFileName);

        int size = 0;
        if (ratio0 != null && ratio1 != null) {
            // 检测图像比例
            try {
                if (ratio1 < ratio0) {
                    float f0 = ratio0;
                    ratio0 = ratio1;
                    ratio1 = f0;
                }
                BufferedImage image = ImageIO.read(file.getInputStream());
                float ratio = new Double((double) image.getWidth() / (double) image.getHeight()).floatValue();

                Assert.isTrue(
                        ratio >= ratio0 && ratio <= ratio1,
                        "图片宽高比例必须在" + ratio0 + "和" + ratio1 + "之间"
                );
                ImageIO.write(image, extension(file.getOriginalFilename()), outFile);
            } catch (ServiceException e) {
                throw e;
            } catch (Exception e) {
                throw new ServiceException("错误的图片格式");
            }
        } else {
            size = FileCopyUtils.copy(file.getInputStream(), Files.newOutputStream(outFile.toPath()));
        }

        LOG.info("File upload done: file={}, size={}, copied={}", newFileName, file.getSize(), size);
        FileMeta meta = saveFileMeta(outFile, tag);
        meta.setUrl(toURL(newFileName));
        meta.setOriginalFileName(file.getOriginalFilename());
        return meta;
    }

    /**
     * 保存文件元数据
     * @param file
     * @param tag
     * @return
     */
    public FileMeta saveFileMeta(File file, String tag) {
        FileMeta meta = new FileMeta();
        meta.setName(file.getName());
        meta.setDir(file.getParentFile().getName());
        meta.setSize(file.length());
        meta.setMime(MimeUtils.guessMimeFromExtension(extension(file.getName())));
        meta.setTag(tag);
        meta.setHost(NetUtils.guessServerIp());
        meta.setNodeName(config.getNodeName());
        dao.save(meta);
        return meta;
    }

    public File localFile(String filename) {
        return new File(dir(filename), filename);
    }

    /**
     * 通过元数据下载文件，默认是本机下载，分布式则需要改造
     * @param meta
     * @param outputStream
     * @param response
     * @return
     * @throws Exception
     */
    public void download(FileMeta meta, OutputStream outputStream, HttpServletResponse response, boolean forceLocal, HttpHeaders headers) throws Exception {
        String host = meta.getHost();
        if (forceLocal || NetUtils.isLocal(host)) {
            localDownload(meta.getName(), outputStream, headers);
        } else {
            if (config.getNodeName().equalsIgnoreCase(meta.getNodeName())) {
                localDownload(meta.getName(), outputStream, headers);
            } else {
                //remoteDownload(meta, outputStream, response);
            }
        }
    }


    public void localDownload(String filename, OutputStream outputStream, HttpHeaders headers) {

        String extension = extension(filename);
        String mime = MimeUtils.guessMimeFromExtension(extension);

        if (mime == null) mime = "application/octet-stream";

        File file = localFile(filename);
        try {
            FileCopyUtils.copy(Files.newInputStream(file.toPath()), outputStream);
        } catch (IOException e) {
            LOG.error("Can't copy file, file=" + file.getAbsolutePath(), e);
        }

        if (headers != null) {
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");

            String uname = null;
            try {
                uname = URLEncoder.encode(filename, "utf-8");
            } catch (UnsupportedEncodingException e) {
            }

            if (!mime.startsWith("image/")) {
                headers.add("Content-Disposition", "attachment; filename=\"" + uname + "\"; filename*=utf-8''" + uname);
            }
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            headers.add("Content-Type", mime);
        }
    }

    public FileMeta isFileExists(Long id) {
        FileMeta meta = dao.getInclude(new FileMeta(id));
        isExists(meta);
        return meta;
    }

    public FileMeta isFileExists(String name) {
        Assert.notBlank(name, "文件不存在");
        Query<FileMeta> query = QueryBuilder.custom(FileMeta.class).andEquivalent("name", name).build();
        FileMeta meta = dao.queryOne(query);
        isExists(meta);
        return meta;
    }

    private boolean isExists(FileMeta meta) {
        Assert.notNull(meta, "文件不存在");

        if (NetUtils.isLocal(meta.getHost())) {
            Assert.isTrue(fileExists(meta.getName()), "文件不存在");
        }

        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        dirFile = new File(config.getUploadDir());
        if (!dirFile.exists()) {
            boolean ok = dirFile.mkdirs();
            LOG.info("Upload dir created: ret={}, path={}", ok, dirFile.getAbsolutePath());
        }

        clientDirFile = new File(dirFile, "client_upload");
        if (!clientDirFile.exists()) clientDirFile.mkdirs();
    }

    private String toURL(String fileName) {
        return String.format("http://%s/api/file/%s", config.getDomain(), fileName);
    }

    private String createNewFileName(String originalName) {
        String extension = extension(originalName);
        long id = dao.databaseRouter().getDatabaseSession().idGenerator().next();
        if (extension != null) {
            return id + "." + extension;
        } else {
            return String.valueOf(id);
        }
    }

    private String extension(String originalName) {
        int index = -1;
        if (StringUtils.isNotBlank(originalName) && (index = originalName.lastIndexOf(".")) >= 0) {
            if (index == originalName.length() - 1) return null;
            return originalName.substring(index + 1);
        }
        return null;
    }

    public boolean fileExists(String name) {
        File file = new File(dir(name), name);
        return file.exists();
    }
}
