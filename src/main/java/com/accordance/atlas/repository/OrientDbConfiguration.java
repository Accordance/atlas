package com.accordance.atlas.repository;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
@ConfigurationProperties("orient_db")
public class OrientDbConfiguration {

    @NotBlank
    private String host;
    private int port = 2424;
    private String username = "username";
    private String password = "password";

    @NotNull
    private String dbName;

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) { this.username = username; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String db_name) {
        this.dbName = db_name;
    }

    public String getConnectionString() {
        return String.format("remote:%s:%d/%s", getHost(), getPort(), getDbName());
    }
}