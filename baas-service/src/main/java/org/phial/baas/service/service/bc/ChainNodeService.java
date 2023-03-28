package org.phial.baas.service.service.bc;


import org.phial.baas.service.domain.entity.bc.ChainNode;

import java.util.List;

public interface ChainNodeService {

    List<ChainNode> listAllByCondition(ChainNode beanCondition);

}
