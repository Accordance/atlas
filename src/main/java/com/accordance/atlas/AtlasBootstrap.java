package com.accordance.atlas;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@SpringBootApplication
public class AtlasBootstrap {

    public static void main(String[] args) {

        SpringApplicationBuilder builder = new SpringApplicationBuilder(AtlasBootstrap.class)
                .showBanner(false);

        builder.run(args);
    }
}
