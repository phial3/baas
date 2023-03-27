package org.phial.baas.manager;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.phial.baas.manager.monitor.SystemMonitor;
import org.phial.baas.service.listener.system.ApplicationReadyListener;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author admin
 */
@Slf4j
@EnableAsync
@EnableScheduling
@ComponentScan(basePackages={"org.phial.baas"})
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ImportResource({"classpath:config/spring.xml"})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class BaasManagerApplication {

    public static void main(String[] args) throws Exception {
        String myConfig = System.getProperty("my.config");
        final Properties properties = new Properties();
        final AtomicBoolean customPropertiesLoaded = new AtomicBoolean(false);

        if (StringUtils.isNotBlank(myConfig)) {
            File configFile = new File(myConfig);
            if (configFile.exists()) {
                properties.load(new FileReader(configFile));
                customPropertiesLoaded.set(true);
            }
        }

        SpringApplicationBuilder builder = new SpringApplicationBuilder()
                .sources(BaasManagerApplication.class)
                .registerShutdownHook(true);

        builder.listeners(event -> {

            if (event instanceof ApplicationEnvironmentPreparedEvent) {
                log.info("Application Environment Prepared");
                ConfigurableEnvironment environment = ((ApplicationEnvironmentPreparedEvent) event).getEnvironment();
                String mavenProfile = environment.getProperty("application.mavenProfile");
                String[] activeProfiles = environment.getActiveProfiles();

                if (activeProfiles.length == 0) {
                    environment.addActiveProfile(mavenProfile);
                }

                if (customPropertiesLoaded.get()) {
                    environment.getPropertySources().addFirst(new PropertiesPropertySource("my-config", properties));
                }
            } else if (event instanceof ApplicationReadyEvent) {

                ConfigurableApplicationContext context = ((ApplicationReadyEvent) event).getApplicationContext();
                Map<String, ApplicationReadyListener> lmap = context.getBeansOfType(ApplicationReadyListener.class);
                if (!lmap.isEmpty()) {
                    lmap.values().forEach(l -> l.applicationReady(context));
                }

            }
        });
        ConfigurableApplicationContext context = builder.run(args);
        ConfigurableEnvironment env = context.getEnvironment();

        if (customPropertiesLoaded.get()) {
            log.info("Customized config specified: {}", myConfig);
        } else {
            log.info("No Custom config specified");
        }

        int monitorPort = env.getProperty("monitor.server.port", int.class, 6899);
        SystemMonitor monitor = new SystemMonitor(monitorPort, context);
        monitor.start();

        log.info("\n" +
                        "============================= APPLICATION INFORMATION =============================\n" +
                        ":: Application Name:       {}\n" +
                        ":: Build Version:          {}\n" +
                        ":: Application Version:    {}\n" +
                        ":: Maven Package Profile:  {}\n" +
                        ":: Spring Active Profiles: {}\n" +
                        ":: Logging Config:         {}\n" +
                        ":: Logging Path:           {}\n" +
                        ":: Logging File:           {}\n" +
                        ":: Server Port:            {}\n" +
                        ":: System Monitor Port:    {}\n" +
                        ":: Application Domain:     {}\n" +
                        "============================== APPLICATION STARTED!! ==============================\n",
                env.getProperty("application.name"),
                env.getProperty("app-config.build-version"),
                env.getProperty("application.version"),
                env.getProperty("application.mavenProfile"),
                StringUtils.join(env.getActiveProfiles(), ','),
                env.getProperty("logging.config"),
                env.getProperty("logging.file.path"),
                env.getProperty("logging.file.name"),
                env.getProperty("server.port"),
                monitorPort,
                env.getProperty("app-config.domain")
        );
    }
}
