package com.accordance.atlas.repository;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.catalog.model.CatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Random;

@Repository
public class ConsulService {

    private static final Logger logger = LoggerFactory.getLogger(ConsulService.class);

    private static ConsulClient client;

    private ConsulClient getClient() {
        if (client == null)
//            client = new ConsulClient("http://dockerhost:8500");
            client = new ConsulClient("dockerhost", 8500);

        return client;
    }

    public Response<List<CatalogService>> getService(String serviceId) {
        return getClient().getCatalogService("orientdb", new QueryParams("dev"));
    }

    public String getSingleServiceAddress(String serviceId) {

        List<CatalogService> responce = getClient().getCatalogService(serviceId, new QueryParams("dev")).getValue();

        CatalogService service = responce.get(new Random().nextInt(responce.size()));

        return service.getAddress() + ":" + service.getServicePort();
    }
}
