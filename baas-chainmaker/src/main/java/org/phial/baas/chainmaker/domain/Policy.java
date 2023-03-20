package org.phial.baas.chainmaker.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Policy {

    private String rule;

    private List<String> orgList;

    private List<String> roleList;

    private List<String> availableOrgList;

}
