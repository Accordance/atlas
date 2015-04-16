package com.accordance.atlas;

import groovy.util.logging.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@SpringCloudApplication
@Slf4j
public class AtlasBootstrap {

    public static void main(String[] args) {

        SpringApplicationBuilder builder = new SpringApplicationBuilder(AtlasBootstrap.class)
                .showBanner(false);

        builder.run(args);
    }
}
