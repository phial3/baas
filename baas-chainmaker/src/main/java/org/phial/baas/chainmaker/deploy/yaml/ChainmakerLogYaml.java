package org.phial.baas.chainmaker.deploy.yaml;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class ChainmakerLogYaml {

    private Log log;

    public ChainmakerLogYaml() {
        this.log = new Log();
    }

    public void setSystem(String log_level_default, String file_path, Integer max_age, Integer rotation_time,
                          String core, String vm, String net, String storage, String consensus){

        this.log.setSystem(log_level_default, file_path, max_age, rotation_time, core, vm, net, storage, consensus, false);
    }

    public void setEvent(String log_level_default, String file_path, Integer max_age, Integer rotation_time,
                         boolean log_in_console, boolean show_color){
        this.log.setEvent(log_level_default, file_path, max_age, rotation_time, log_in_console, show_color);
    }

    public void setBrief(String log_level_default, String file_path, Integer max_age, Integer rotation_time,
                         boolean log_in_console, boolean show_color) {

        this.log.setBrief(log_level_default, file_path, max_age, rotation_time, log_in_console, show_color);
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class Log {

        private System system;

        private Brief brief;

        private Event event;

        @Data
        @NoArgsConstructor
        public class System {

            private String log_level_default;

            private LogLevels log_levels;

            private String file_path;

            private Integer max_age;

            private Integer rotation_time;

            private boolean log_in_console;

            private boolean show_color;

            @Data
            @AllArgsConstructor
            @NoArgsConstructor
            public class LogLevels {

                private String core;

                private String net;

                private String vm;

                private String storage;

                private String consensus;

            }

            public System(String log_level_default, String file_path, Integer max_age, Integer rotation_time,
                          String core, String vm, String net, String storage, String consensus, boolean showColor) {
                this.log_level_default = log_level_default;
                this.file_path = file_path;
                this.max_age = max_age;
                this.rotation_time = rotation_time;
                this.log_in_console = false;
                this.show_color = showColor;

                this.log_levels = new LogLevels(core, net, vm, storage, consensus);

            }
        }

        public void setSystem(String log_level_default, String file_path, Integer max_age, Integer rotation_time,
                             String core, String vm, String net, String storage, String consensus, boolean showColor) {

            this.system = new System(log_level_default, file_path, max_age, rotation_time, core, vm, net, storage, consensus, showColor);
        }

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public class Brief {

            private String log_level_default;

            private String file_path;

            private Integer max_age;

            private Integer rotation_time;

            private boolean log_in_console;

            private boolean show_color;

        }



        public void setBrief(String log_level_default, String file_path, Integer max_age, Integer rotation_time,
                              boolean log_in_console, boolean show_color) {

            this.brief = new Brief(log_level_default, file_path, max_age, rotation_time, log_in_console, show_color);
        }


        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public class Event {

            private String log_level_default;

            private String file_path;

            private Integer max_age;

            private Integer rotation_time;

            private boolean log_in_console;

            private boolean show_color;

        }


        public void setEvent(String log_level_default, String file_path, Integer max_age, Integer rotation_time,
                             boolean log_in_console, boolean show_color) {

            this.event = new Event(log_level_default, file_path, max_age, rotation_time, log_in_console, show_color);
        }

    }

}
