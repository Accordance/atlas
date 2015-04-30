package com.accordance.atlas;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.lang.management.ManagementFactory;
import java.util.List;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@SpringCloudApplication
@EnableScheduling
public class AtlasBootstrap {

    public static void main(String[] args) {

        List<String> inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
        System.out.println("jvm arguments = " + inputArguments);

        System.out.println("java cmd arguments = " + System.getProperty("sun.java.command"));

        SpringApplicationBuilder builder = new SpringApplicationBuilder(AtlasBootstrap.class)
                .showBanner(false);

        builder.run(args);
    }
}
