package com.accordance.atlas.repository;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.model.GetValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ConsulRepository {

    private static final Logger logger = LoggerFactory.getLogger(ConsulRepository.class);

    @Autowired
    DiscoveryClient discoveryClient;

    private static ConsulClient client;
    private static String securityToken;

    public static final String CONSUL_NAME = "consul";

    private ConsulClient getClient() {
        if (client == null) {

            ServiceInstance instance = null;
            List<ServiceInstance> instances = discoveryClient.getInstances(CONSUL_NAME);
            if (instances.size() > 0)
            {
                instance = instances.get(0);
            }

            String host = "localhost";
            int port = 8500;
            if (instance == null) {
                host = instance.getHost();
                port = instance.getPort();
            }

            client = new ConsulClient(host, port);
        }

        if (securityToken == null) {
            try {
                securityToken = "";
                securityToken = Files.readAllLines(Paths.get("consul_token.development"), Charset.defaultCharset()).get(0);
            } catch (IOException e) {
                securityToken = "";
            }
//            FileUtils.readFileToString(Paths.get("aa"));
        }

        return client;
    }

//    public Response<List<CatalogService>> getService(String serviceId) {
//        return getClient().getCatalogService("orientdb", new QueryParams("dev"));
//    }
//
//    public String getSingleServiceAddress(String serviceId) {
//
//        List<CatalogService> responce = getClient().getCatalogService(serviceId, new QueryParams("dev")).getValue();
//
//        CatalogService service = responce.get(new Random().nextInt(responce.size()));
//
//        return service.getAddress() + ":" + service.getServicePort();
//    }

    private String decodeValue(GetValue value) {
        byte[] decoded = Base64.getDecoder().decode(value.getValue());
        return new String(decoded, StandardCharsets.UTF_8);
    }

    public String getPropertyValue(String path, String defaultValue) {
        QueryParams queryParams = new QueryParams("dev");
        Response<GetValue> kvValue = getClient().getKVValue(path, securityToken, queryParams);
        return decodeValue(kvValue.getValue());
    }

    public Map<String, String> getPropertyValuesRecursive(String prefix, Map<String, String> defaultValue) {

        Map<String, String> result = new HashMap<String, String>();

        QueryParams queryParams = new QueryParams("dev");
        Response<List<GetValue>> kvValues = getClient().getKVValues(prefix, securityToken, queryParams);
        kvValues.getValue().forEach(value -> result.put(value.getKey().replace(prefix + "/", ""), decodeValue(value)));

        return result;
    }
}

