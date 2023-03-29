package org.phial.baas.manager.controller.system;

import org.mayanjun.myrest.BaseController;
import org.mayanjun.myrest.RestResponse;
import org.phial.baas.manager.config.interceptor.ClusterVerifySignature;
import org.phial.baas.manager.service.system.ClusterBusiness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ClusterVerifySignature
@RequestMapping("api/cluster")
public class ClusterController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(ClusterController.class);

    public ClusterBusiness clusterBusiness;

    public ClusterController(ClusterBusiness clusterBusiness) {
        this.clusterBusiness = clusterBusiness;
    }

}
