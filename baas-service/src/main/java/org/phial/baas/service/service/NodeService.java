package org.phial.baas.service.service;

import org.phial.baas.service.domain.entity.Node;

import java.util.List;

public interface NodeService {
    void save(Node node);
    void update(Node node);
    void delete(Long node);
    Node get(Long node);

    List<Node> getByIds(List<Long> nodeIds);
}
