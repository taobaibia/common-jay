package com.common.jay.startBusiness;

import com.common.jay.startBusiness.api.StreamiMessageApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * @author Jay
 * @version V1.0
 * @Package org.common.jay
 * @date ${DATE} ${TIME}
 */
@SpringBootApplication
@ImportResource({ "classpath*:/**/jay/*.xml" })
@EnableFeignClients(basePackages = { "com.*" })
@EnableAsync
@RestController
@ComponentScan(basePackages = { "com.*", "com.*.*" ,"com.common.jay.startBusiness.*"})

public class ImessageApplication {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
        SpringApplication.run(ImessageApplication.class, args);
        System.out.println(" ██ ████     ████                                                  \n" +
                "░░ ░██░██   ██░██                                    █████         \n" +
                " ██░██░░██ ██ ░██  █████   ██████  ██████  ██████   ██░░░██  █████ \n" +
                "░██░██ ░░███  ░██ ██░░░██ ██░░░░  ██░░░░  ░░░░░░██ ░██  ░██ ██░░░██\n" +
                "░██░██  ░░█   ░██░███████░░█████ ░░█████   ███████ ░░██████░███████\n" +
                "░██░██   ░    ░██░██░░░░  ░░░░░██ ░░░░░██ ██░░░░██  ░░░░░██░██░░░░ \n" +
                "░██░██        ░██░░██████ ██████  ██████ ░░████████  █████ ░░██████\n" +
                "░░ ░░         ░░  ░░░░░░ ░░░░░░  ░░░░░░   ░░░░░░░░  ░░░░░   ░░░░░░ \n");


    }
    @Value("${imessage.appKey}")
    private String appKey;
    @Value("${imessage.token}")
    private String token;
    @Value("${imessage.url}")
    private String url;

    @Autowired
    private StreamiMessageApi streamiMessageApi;

    @PostConstruct
    public void init() {
        String[] textArray = {"你好", "我是Jay"};
        try {
            streamiMessageApi.process(textArray, appKey, token, url);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        streamiMessageApi.shutdown();
    }
}