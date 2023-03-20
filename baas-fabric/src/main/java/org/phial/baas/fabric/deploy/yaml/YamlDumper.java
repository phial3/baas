package org.phial.baas.fabric.deploy.yaml;


import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

import java.util.Map;

/**
 * @author yhr
 */
public final class YamlDumper {
    private final Yaml yaml;
    private final Representer representer;
    private final DumperOptions dumperOptions;

    private static YamlDumper yamlDumper;

    private YamlDumper() {
        this.representer = new YamlRepresenter();
        this.configureRepresenter();
        this.dumperOptions = new DumperOptions();
        this.configureDumperOptions();
        this.yaml = new Yaml(this.representer, this.dumperOptions);
    }

    public static YamlDumper getInstance(){
        if (yamlDumper == null){
            yamlDumper = new YamlDumper();
        }
        return yamlDumper;
    }

    public Map<String, Object> load(String yaml) {
        return this.yaml.load(yaml);
    }

    public synchronized String dump(Object data) {
        return this.yaml.dump(data).replace("|-", "");
    }

    private void configureRepresenter() {
//        this.representer.addClassTag(CAConfigYaml.class, Tag.MAP);
//        this.representer.addClassTag(ChainmakerLogYaml.class, Tag.MAP);
    }

    private void configureDumperOptions() {
        this.dumperOptions.setIndent(2);
        this.dumperOptions.setPrettyFlow(false);
        this.dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
    }


    private static class YamlRepresenter extends Representer {

        public YamlRepresenter() {
            this.representers.put(K8sConfigMapYaml.ConfigMapDataType.class, new ConfigMapDataRepresent());
            this.representers.put(K8sDepolymentYaml.NumberType.class, new K8sDeploymentNumberTypeRepresent());
            this.representers.put(K8sDepolymentYaml.StringType.class, new K8sDeploymentStringTypeRepresent());
            this.representers.put(K8sServiceYaml.StringType.class, new K8sServiceStringTypeRepresent());
            this.representers.put(K8sServiceYaml.NumberType.class, new K8sServiceNumberTypeRepresent());
        }

        private class ConfigMapDataRepresent implements Represent {

            @Override
            public Node representData(Object o) {

                K8sConfigMapYaml.ConfigMapDataType str = (K8sConfigMapYaml.ConfigMapDataType) o;

                return representScalar(Tag.STR, str.getValue(), DumperOptions.ScalarStyle.SINGLE_QUOTED);
            }
        }

        private class K8sDeploymentNumberTypeRepresent implements Represent {

            @Override
            public Node representData(Object o) {

                K8sDepolymentYaml.NumberType str = (K8sDepolymentYaml.NumberType) o;

                return representScalar(Tag.INT, str.getValue(), DumperOptions.ScalarStyle.PLAIN);
            }
        }

        private class K8sDeploymentStringTypeRepresent implements Represent {

            @Override
            public Node representData(Object o) {

                K8sDepolymentYaml.StringType str = (K8sDepolymentYaml.StringType) o;

                return representScalar(Tag.STR, str.getValue(), DumperOptions.ScalarStyle.SINGLE_QUOTED);
            }
        }


        private class K8sServiceStringTypeRepresent implements Represent {

            @Override
            public Node representData(Object o) {

                K8sServiceYaml.StringType str = (K8sServiceYaml.StringType) o;

                return representScalar(Tag.STR, str.getValue(), DumperOptions.ScalarStyle.SINGLE_QUOTED);
            }
        }

        private class K8sServiceNumberTypeRepresent implements Represent {

            @Override
            public Node representData(Object o) {

                K8sServiceYaml.NumberType str = (K8sServiceYaml.NumberType) o;

                return representScalar(Tag.INT, str.getValue(), DumperOptions.ScalarStyle.PLAIN);
            }
        }
    }
}
