package com.accordance.atlas.controller;

import com.accordance.atlas.SwaggerConfiguration;
import java.util.function.Consumer;
import javax.inject.Inject;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { TestApp.class, SwaggerConfiguration.class, SwaggerTest.ExtraControllers.class })
@WebAppConfiguration
public class SwaggerTest {
    @Inject
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    private static MockHttpServletRequestBuilder newGetSwaggerApi() {
        return get("/api");
    }

    @Test
    public void testGetSwaggerApi() throws Exception {
        mockMvc.perform(newGetSwaggerApi())
                .andExpect(status().isOk())
                .andExpect(validSwaggerApiResponse());
    }

    @Test
    public void testControllerDefinitionExists() throws Exception {
        mockMvc.perform(newGetSwaggerApi())
                .andExpect(status().isOk())
                .andExpect(testApiCallExists("/apps_graph", "get"));

    }

    private static ResultMatcher jsonObjValidator(Consumer<? super JSONObject> matcher) {
        return (MvcResult result) -> {
            String strResponse = result.getResponse().getContentAsString();
            JSONParser parser = new JSONParser();
            matcher.accept((JSONObject)parser.parse(strResponse));
        };
    }

    private static ResultMatcher validSwaggerApiResponse() {
        return jsonObjValidator((JSONObject jsonObj) -> {
            Object swaggerField = jsonObj.get("swagger");
            assertEquals("2.0", swaggerField.toString());
        });
    }

    private static ResultMatcher testApiCallExists(String urlPart, String methodType) {
        return jsonObjValidator((JSONObject jsonObj) -> {
            JSONObject paths = (JSONObject)jsonObj.get("paths");
            assertNotNull("paths", paths);

            JSONObject dataCenters = (JSONObject)paths.get(urlPart);
            assertNotNull(urlPart, dataCenters);

            JSONObject dataCentersGet = (JSONObject)dataCenters.get(methodType);
            assertNotNull(methodType, dataCentersGet);
        });
    }

    @ComponentScan({"springfox"})
    public static class ExtraControllers {
    }
}
