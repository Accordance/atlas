package com.accordance.atlas.controller;

import com.accordance.atlas.model.DataCenter;
import com.accordance.atlas.repository.OrientDataCenterRepository;
import com.accordance.atlas.repository.OrientDbFactory;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import javax.inject.Inject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { TestApp.class })
@WebAppConfiguration
public class DataCenterControllerTest {
    private static final String DATA_CENTER_CLASS = "DataCenter";

    @Inject
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        OCommandRequest command = new OCommandSQL("DELETE VERTEX " + DATA_CENTER_CLASS);
        withGraphNoTx((OrientGraphNoTx db) -> {
            db.command(command).execute();
            return null;
        });
    }

    public <R> R withGraphNoTx(Function<? super OrientGraphNoTx, ? extends R> action) {
        OrientDbFactory dbFactory = webApplicationContext.getBean(OrientDbFactory.class);
        return dbFactory.withGraphNoTx(action);
    }

    private static DataCenter newTestDataCenter(int id) {
        DataCenter.Builder result = new DataCenter.Builder("my-test-id-" + id);
        result.setDescription("my-description-" + id);
        return result.create();
    }

    private static MockHttpServletRequestBuilder newAddDataCenterRequest(DataCenter dataCenter) {
        MockHttpServletRequestBuilder request = post("/data_centers");
        request.param("id", dataCenter.getUserId());
        request.param("descr", dataCenter.getDescription());
        return request;
    }

    private static MockHttpServletRequestBuilder newGetDataCentersRequest() {
        return get("/data_centers");
    }

    @Test
    public void testAddSingleDataCenter() throws Exception {
        DataCenter testData = newTestDataCenter(1);

        mockMvc.perform(newAddDataCenterRequest(testData))
                .andExpect(status().isOk());

        withGraphNoTx((OrientGraphNoTx db) -> {
            Iterable<Vertex> vertices = db.getVerticesOfClass(DATA_CENTER_CLASS);

            Iterable<DataCenter> dataCenters = Iterables.transform(vertices, OrientDataCenterRepository::fromVertex);

            assertEquals("DataCenter", Sets.newHashSet(testData), Sets.newHashSet(dataCenters));
            return null;
        });
    }

    @Test
    public void testAddMultipleDataCenter() throws Exception {
        DataCenter[] testDatas = new DataCenter[] {
            newTestDataCenter(1),
            newTestDataCenter(2),
            newTestDataCenter(3)
        };

        for (DataCenter testData: testDatas) {
            mockMvc.perform(newAddDataCenterRequest(testData))
                    .andExpect(status().isOk());
        }

        withGraphNoTx((OrientGraphNoTx db) -> {
            Iterable<Vertex> vertices = db.getVerticesOfClass(DATA_CENTER_CLASS);

            Iterable<DataCenter> dataCenters = Iterables.transform(vertices, OrientDataCenterRepository::fromVertex);

            assertEquals("DataCenter", Sets.newHashSet(testDatas), Sets.newHashSet(dataCenters));
            return null;
        });
    }

    private static List<DataCenter> parseJsonDataCenters(String json) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONArray jsonResult = (JSONArray)parser.parse(json);

        List<DataCenter> result = new ArrayList<>(jsonResult.size());
        for (Object jsonObj: jsonResult) {
            result.add(DataCenterController.fromJson((JSONObject)jsonObj));
        }
        return result;
    }

    @Test
    public void testGetDataCenters() throws Exception {
        Set<DataCenter> testDatas = Sets.newHashSet(
                newTestDataCenter(1),
                newTestDataCenter(2),
                newTestDataCenter(3));

        for (DataCenter testData: testDatas) {
            mockMvc.perform(newAddDataCenterRequest(testData))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(newGetDataCentersRequest())
                .andExpect(status().isOk())
                .andExpect((MvcResult result) -> {
                    String content = result.getResponse().getContentAsString();
                    List<DataCenter> resultDatas = parseJsonDataCenters(content);
                    assertEquals("DataCenters", testDatas, new HashSet<>(resultDatas));
                });
    }
}
