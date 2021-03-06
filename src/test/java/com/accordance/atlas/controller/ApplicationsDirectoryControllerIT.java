package com.accordance.atlas.controller;

import com.accordance.atlas.AtlasBootstrap;
import com.ecwid.consul.v1.ConsulClient;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.parsing.Parser;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

// TODO: replace ConsulClient with a Fake
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { ApplicationsDirectoryControllerIT.AtlasTestApplication.class,  })
@WebAppConfiguration
@IntegrationTest("server.port:0")
//@ActiveProfiles("test")
public class ApplicationsDirectoryControllerIT {
    private static final String ITEMS_RESOURCE = "/apps";
    private static final String ITEM_RESOURCE = "/apps/{id}";

    @SpringBootApplication
    @EnableCircuitBreaker
    @EnableDiscoveryClient
    public static class AtlasTestApplication {

        @Bean
        public ConsulClient consulClient() {
            return null;
        }

        public static void main(String[] args) {

            SpringApplication.run(AtlasBootstrap.class, args);
        }

    }

    @Value("${local.server.port}")
    private int serverPort;

    @Before
    public void setUp() {
        RestAssured.port = serverPort;
        RestAssured.defaultParser = Parser.JSON;
    }

    @Test
    public void getApplicationsShouldReturnFullCollection() {
        given()
                .log()
                .all()
                .when()
                .get(ITEMS_RESOURCE)
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void getItemsShouldReturnBothItems() {
        given()
                .log()
                .all()
                .when()
                .get(ITEMS_RESOURCE)
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_OK)
                .body("data", equalTo("something here"));
//            .body(DESCRIPTION_FIELD, hasItems(FIRST_ITEM_DESCRIPTION, SECOND_ITEM_DESCRIPTION))
//            .body(CHECKED_FIELD, hasItems(true, false));
    }

    @Test
    public void getItemShouldReturnItem() {
        given()
                .log().all()
                .when()
                .get(ITEM_RESOURCE, "api")
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_OK);
    }
}