package org.phial.baas.service.service;


import org.phial.baas.service.domain.entity.ChainNode;

import java.util.List;

public interface ChainNodeService {

    List<ChainNode> listAllByCondition(ChainNode beanCondition);

}
