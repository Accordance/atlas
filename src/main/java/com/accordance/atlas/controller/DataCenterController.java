package com.accordance.atlas.controller;

import com.accordance.atlas.model.DataCenter;
import com.accordance.atlas.repository.DataCenterRepository;
import java.io.IOException;
import java.util.Objects;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataCenterController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataCenterController.class);

    private final DataCenterRepository dataCenters;

    @Autowired
    public DataCenterController(DataCenterRepository dataCenters) {
        this.dataCenters = Objects.requireNonNull(dataCenters, "dataCenters");
    }

    @RequestMapping(value = "/data_centers", method = RequestMethod.GET, produces = "application/json")
    public JSONArray getDataCenters() throws IOException {
        LOGGER.debug("Retrieving all DataCenter vertexes from OrientDb.");

        JSONArray result = new JSONArray();
        dataCenters.getAllDataCenters((dataCenter) -> {
            JSONObject dataCenterObj = new JSONObject();
            dataCenterObj.put("id", dataCenter.getUserId());
            dataCenterObj.put("descr", dataCenter.getDescription());
            result.add(dataCenterObj);
        });
        return result;
    }

    @RequestMapping(value = "/data_centers", method = RequestMethod.POST)
    public void addDataCenter(
            @RequestParam(value = "id", required = true) String id,
            @RequestParam(value = "descr", required = false, defaultValue = "") String descr) throws IOException {

        DataCenter.Builder dataCenterBuilder = new DataCenter.Builder(id);
        dataCenterBuilder.setDescription(descr);

        DataCenter dataCenter = dataCenterBuilder.create();
        dataCenters.addDataCenter(dataCenter);

        LOGGER.info("Successfully added new DataCenter: {}", dataCenter);
    }
}
