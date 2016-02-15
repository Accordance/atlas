package com.accordance.atlas.repository;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.model.GetValue;
import java.io.IOException;
import java.nio.charset.Charset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Repository;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Repository
public class ConsulRepository {

    private static final Logger logger = LoggerFactory.getLogger(ConsulRepository.class);

    @Autowired
    DiscoveryClient discoveryClient;

    @Autowired
    ConsulClient consulClient;

    private static final AtomicReference<String> SECURITY_TOKEN_REF = new AtomicReference<>();

    private ConsulClient getClient() {
        return consulClient;
    }

    private String getSecurityToken() {
        String result = SECURITY_TOKEN_REF.get();
        if (result == null) {
            result = readSecurityToken();
            if (!SECURITY_TOKEN_REF.compareAndSet(null, result)) {
                result = SECURITY_TOKEN_REF.get();
            }
        }
        return result;
    }

    private String readSecurityToken() {
        // FIXME: Don't read from the current working directory.
        Path tokenPath = Paths.get("consul_token.development");
        try {
            if (Files.isRegularFile(tokenPath)) {
                return Files.readAllLines(Paths.get("consul_token.development"), Charset.defaultCharset()).get(0);
            }
        } catch (IOException ex) {
            logger.warn("Could not read security token file: {}: {}", tokenPath, ex);
        }

        return System.getenv("CONSUL_TOKEN");
    }

    private String decodeValue(GetValue value) {
        byte[] decoded = Base64.getDecoder().decode(value.getValue());
        return new String(decoded, StandardCharsets.UTF_8);
    }

    public String getPropertyValue(String path, String defaultValue) {
        QueryParams queryParams = new QueryParams("dev");
        Response<GetValue> kvValue = getClient().getKVValue(path, getSecurityToken(), queryParams);
        if (kvValue.getValue() == null)
            return defaultValue;

        return decodeValue(kvValue.getValue());
    }

    public Map<String, String> getPropertyValuesRecursive(String prefix, Map<String, String> defaultValue) {

        Map<String, String> result = new HashMap<String, String>();

        QueryParams queryParams = new QueryParams("dev");
        Response<List<GetValue>> kvValues = getClient().getKVValues(prefix, getSecurityToken(), queryParams);
        if (kvValues.getValue() == null)
            return defaultValue;

        kvValues.getValue().forEach(value -> result.put(value.getKey().replace(prefix + "/", ""), decodeValue(value)));

        return result;
    }
}

