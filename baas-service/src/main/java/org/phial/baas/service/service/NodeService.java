package org.phial.baas.service.service;

import org.phial.baas.service.domain.entity.bc.P2pNode;

import java.util.List;

public interface NodeService {
    void save(P2pNode p2pNode);
    void update(P2pNode p2pNode);
    void delete(Long node);
    P2pNode get(Long node);

    List<P2pNode> getByIds(List<Long> nodeIds);
}
