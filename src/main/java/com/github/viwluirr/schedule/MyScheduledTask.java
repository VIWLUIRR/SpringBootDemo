package com.github.viwluirr.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.github.viwluirr.config.AppConfig;

@Component
public class MyScheduledTask {

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private AppConfig appConfig;

    @Scheduled(fixedRate = 1000)
    public void refreshConfig() {
        System.out.println("调起配置刷新。");
        restTemplate.postForObject("http://localhost:"+appConfig.getPort()+"/actuator/refresh", null, Void.class);
    }
}