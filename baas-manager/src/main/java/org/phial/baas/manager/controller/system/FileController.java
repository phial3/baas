package org.phial.baas.manager.controller.system;

import org.mayanjun.core.Assert;
import org.mayanjun.myrest.BaseController;
import org.mayanjun.myrest.RestResponse;
import org.phial.baas.manager.config.app.AppConfig;
import org.phial.baas.manager.config.init.MetaProperty;
import org.phial.baas.manager.config.init.Privileged;
import org.phial.baas.manager.config.init.PrivilegedMeta;
import org.phial.baas.manager.config.init.Profiler;
import org.phial.baas.manager.config.interceptor.Login;
import org.phial.baas.manager.service.system.FileBusiness;
import org.phial.baas.service.domain.entity.system.FileMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;

/**
 * 文件上传下载
 * @since 2019-07-10
 * @author mayanjun
 */
@RestController
@RequestMapping("api/file")
@PrivilegedMeta({
        @MetaProperty(name = "module", value = "文件")
})
public class FileController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(FileController.class);

    private FileBusiness business;

    @Value("${server.port}")
    private int serverPort;

    @Autowired
    private AppConfig config;

    public FileController(FileBusiness business) {
        this.business = business;
    }

    @Profiler
    @Login
    @Privileged("文件上传")
    @PostMapping
    public Object upload(MultipartFile file,
                         String tag,
                         @RequestParam(required = false) Float ratio0,
                         @RequestParam(required = false) Float ratio1) throws Exception {
        return RestResponse.ok().add("metadata", business.upload(file, tag, ratio0, ratio1));
    }

    @Profiler
    @GetMapping(value = "/cluster/")
    public ResponseEntity<byte[]> downloadCluster(Long id, HttpServletRequest request, HttpServletResponse response) throws Exception {

        int remotePort = request.getServerPort();
        Assert.isTrue(remotePort == serverPort, "非法请求");

        String addr = request.getRemoteAddr();
        boolean isClusterHost = config.getClusterNodesList().contains(addr);
        if (!isClusterHost) {
            isClusterHost = config.getClusterNodesList().contains(request.getRemoteHost());
        }
        Assert.isTrue(isClusterHost, "非法请求");

        final FileMeta meta = business.isFileExists(id);
        return doDownloadById(meta, response, true);
    }

    @Profiler
    @Login
    @GetMapping(value = "id/{id}")
    public ResponseEntity<byte[]> downloadById(@PathVariable Long id, HttpServletResponse response) throws Exception {
        final FileMeta meta = business.isFileExists(id);
        return doDownloadById(meta, response, false);
    }

    private ResponseEntity<byte[]> doDownloadById(final FileMeta meta, HttpServletResponse response, boolean forceLocal) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HttpHeaders headers = new HttpHeaders();
        business.download(meta, baos, response, forceLocal, headers);
        ResponseEntity<byte[]> entity = ResponseEntity.ok().headers(headers).body(baos.toByteArray());
        return entity;
    }

    /**
     * 后台访问文件资源
     * @param name
     * @param response
     * @return
     */
    @Profiler
    @Login
    @GetMapping(value = "{name}")
    public ResponseEntity<byte[]> download(@PathVariable String name, HttpServletResponse response) throws Exception {
        return doDownload(name, response, false);
    }

    private ResponseEntity<byte[]> doDownload(String name, HttpServletResponse response, boolean forceLocal) throws Exception {
        final FileMeta meta = business.isFileExists(name);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HttpHeaders headers = new HttpHeaders();
        business.download(meta, baos, response, forceLocal, headers);
        ResponseEntity<byte[]> entity = ResponseEntity.ok().headers(headers).body(baos.toByteArray());
        return entity;
    }
}
