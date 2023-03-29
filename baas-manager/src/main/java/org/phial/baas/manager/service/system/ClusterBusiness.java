package org.phial.baas.manager.service.system;

import org.apache.commons.lang3.StringUtils;
import org.mayanjun.core.Assert;
import org.mayanjun.mybatisx.dal.dao.BasicDAO;
import org.mayanjun.myrest.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 处理集群接口的业务逻辑
 * @since 2021/1/19
 * @author mayanjun
 */
@Component
public class ClusterBusiness {
    private static final Logger LOG = LoggerFactory.getLogger(ClusterBusiness.class);

    private BasicDAO dao;

    @Autowired
    private AttributeBusiness attributeBusiness;

}
