package org.phial.baas.api.service;

import org.phial.baas.api.domain.entity.ChainNode;

import java.util.List;

public interface ChainNodeService {

    List<ChainNode> listAllByCondition(ChainNode beanCondition);

}
