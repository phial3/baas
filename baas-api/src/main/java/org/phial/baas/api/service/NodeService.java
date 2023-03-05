package org.phial.baas.api.service;

import org.phial.baas.api.domain.Node;

import java.util.List;

public interface NodeService {
    void save(Node node);
    void update(Node node);
    void delete(Long node);
    Node get(Long node);

    List<Node> getByIds(List<Long> nodeIds);
}
