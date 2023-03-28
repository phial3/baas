package org.phial.baas.manager.service;


import org.apache.commons.collections4.map.HashedMap;
import org.mayanjun.mybatisx.api.query.SortDirection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author mayanjun
 * @vendor mayanjun.org
 * @since 2019-10-10
 */
public class ParametersBuilder {

    private List<KVEntity> parameters;
    private Map<String, Object> extra;

    private String orderField = "id";
    private SortDirection orderDirection = SortDirection.DESC;
    /**
     * 多个字段排序
     */
    private boolean multipleFieldSort = false;

    private String excludeFields[];

    /**
     * 是否开启链式依赖：如果开启链式依赖的话，查询条件遵守最左依赖原则，目的是为了优化联合索引的性能
     */
    private boolean chainedDepend;

    private ParametersBuilder() {
        this.parameters = new ArrayList<>();
        this.extra = new HashedMap();
        this.chainedDepend = false;
    }

    private ParametersBuilder(String orderField, SortDirection orderDirection) {
        this();
        this.orderField = orderField;
        this.orderDirection = orderDirection;
    }

    public static ParametersBuilder custom(String orderField, SortDirection orderDirection) {
        return custom(orderField, false, orderDirection);
    }

    public static ParametersBuilder custom(String orderField, boolean multipleFieldSort, SortDirection orderDirection) {
        ParametersBuilder parametersBuilder = new ParametersBuilder(orderField, orderDirection);
        parametersBuilder.setMultipleFieldSort(multipleFieldSort);
        return parametersBuilder;
    }

    public static ParametersBuilder custom() {
        return new ParametersBuilder();
    }

    public ParametersBuilder add(String name, Object value) {
        this.parameters.add(new KVEntity(name, value));
        return this;
    }

    public ParametersBuilder reset(String name, Object value) {
        this.remove(name);
        this.add(name, value);
        return this;
    }

    public ParametersBuilder remove(String name) {
        this.parameters.removeIf(kvEntity -> kvEntity.key.equals(name));
        return this;
    }

    public ParametersBuilder enabledChainedDepend() {
        this.chainedDepend = true;
        return this;
    }

    public ParametersBuilder disabledChainedDepend() {
        this.chainedDepend = false;
        return this;
    }

    public ParametersBuilder extra(String name, Object data) {
        this.extra.put(name, data);
        return this;
    }

    public Map<String, Object> extras() {
        return this.extra;
    }

    public Object extra(String name) {
        return this.extra.get(name);
    }

    public boolean isChainedDepend() {
        return chainedDepend;
    }

    public List<KVEntity> build() {
        return parameters;
    }

    public String getOrderField() {
        return orderField;
    }

    public void setOrderField(String orderField) {
        this.orderField = orderField;
    }

    public SortDirection getOrderDirection() {
        return orderDirection;
    }

    public void setOrderDirection(SortDirection orderDirection) {
        this.orderDirection = orderDirection;
    }

    public ParametersBuilder excludes(String... excludeFields) {
        this.excludeFields = excludeFields;
        return this;
    }

    public String[] getExcludeFields() {
        return this.excludeFields;
    }

    public boolean isMultipleFieldSort() {
        return multipleFieldSort;
    }

    public void setMultipleFieldSort(boolean multipleFieldSort) {
        this.multipleFieldSort = multipleFieldSort;
    }

    public static class KVEntity {
        public final String key;
        public final Object value;

        public KVEntity(String key, Object value) {
            this.key = key;
            this.value = value;
        }
    }
}
