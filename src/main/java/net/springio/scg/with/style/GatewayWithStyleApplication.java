package net.springio.scg.with.style;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class GatewayWithStyleApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
            .sources(GatewayWithStyleApplication.class)
            .profiles("local")
            .run(args);
    }

}
