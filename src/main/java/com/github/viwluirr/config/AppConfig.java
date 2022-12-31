package com.github.viwluirr.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Base64;
@Component
public class AppConfig {

    @Value("${management.private.key}")
    private String key;

    @Value("${management.server.port}")
    private String port;

    public byte[] getKey(){
        return Base64.getDecoder().decode(key);
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
