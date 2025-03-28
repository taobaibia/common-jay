package com.common.bops;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.RestController;


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
@ComponentScan(basePackages = { "com.*", "com.*.*" ,"com.common.bops.*"})
public class CommonBopsWebApplication {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
        SpringApplication.run(CommonBopsWebApplication.class, args);
        System.out.println(
                " 																																                                                       \n"
                        + " ██████╗ ██████╗ ███╗   ███╗███╗   ███╗ ██████╗ ███╗   ██╗      ██████╗  ██████╗ ██████╗ ███████╗\n"
                        + "██╔════╝██╔═══██╗████╗ ████║████╗ ████║██╔═══██╗████╗  ██║      ██╔══██╗██╔═══██╗██╔══██╗██╔════╝\n"
                        + "██║     ██║   ██║██╔████╔██║██╔████╔██║██║   ██║██╔██╗ ██║█████╗██████╔╝██║   ██║██████╔╝███████╗\n"
                        + "██║     ██║   ██║██║╚██╔╝██║██║╚██╔╝██║██║   ██║██║╚██╗██║╚════╝██╔══██╗██║   ██║██╔═══╝ ╚════██║\n"
                        + "╚██████╗╚██████╔╝██║ ╚═╝ ██║██║ ╚═╝ ██║╚██████╔╝██║ ╚████║      ██████╔╝╚██████╔╝██║     ███████║\n"
                        + " ╚═════╝ ╚═════╝ ╚═╝     ╚═╝╚═╝     ╚═╝ ╚═════╝ ╚═╝  ╚═══╝      ╚═════╝  ╚═════╝ ╚═╝     ╚══════╝\n");
    }


//    @Bean
//    public ServletWebServerFactory webServerFactory() {
//        TomcatServletWebServerFactory fa = new TomcatServletWebServerFactory();
//        // 解决url中含特殊字符报错问题
//        fa.addConnectorCustomizers(connector -> {
//            connector.setProperty("relaxedPathChars", "\"<>[\\]^`{|}");
//            connector.setProperty("relaxedQueryChars", "\"<>[\\]^`{|}");
//        });
//        return fa;
//    }


}