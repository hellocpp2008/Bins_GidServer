package com.bins.gid.global.id;

import com.bins.gid.global.id.utils.IpUtils;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

/**
 * Desc:
 *
 * @author zhouxingbin
 * @date 2021/2/4
 */
@EnableDubbo
@SpringBootApplication
@ComponentScan(basePackages = {"com.bins.gid.global.id.*"})
public class GlobalIdProviderApplication {

    private final static Logger logger = LoggerFactory.getLogger(GlobalIdProviderApplication.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(GlobalIdProviderApplication.class);
        Environment env = app.run(args).getEnvironment();
        logger.info(GlobalIdProviderApplication.class.getSimpleName() + " is success!");
        logger.info("\n----------------------------------------------------------\n\t" +
                "Application '{}' is running! Access URLs:\n\t" +
                "Local: \t\thttp://localhost:{}\n\t" +
                "External: \t\thttp://{}:{}\n\t" +
                "API documentation: \thttp://localhost:{}/doc.html\n",
            env.getProperty("spring.application.name"),
            env.getProperty("server.port"),
            IpUtils.getIp(),
            env.getProperty("server.port"),
            env.getProperty("server.port"));
    }

}
