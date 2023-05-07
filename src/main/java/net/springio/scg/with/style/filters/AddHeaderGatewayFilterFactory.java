package net.springio.scg.with.style.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Component
class AddHeaderGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddHeaderGatewayFilterFactory.class);
    private static final String MY_HEADER_KEY = "X-My-Header";

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerWebExchange updatedExchange = exchange.mutate()
                .request(request -> request.headers(headers -> {

                    headers.put(MY_HEADER_KEY, List.of("gen-" + LocalDateTime.now()));

                    LOGGER.info("Processed request, added " + MY_HEADER_KEY + " header");
                }))
                .build();
            return chain.filter(updatedExchange)
                .then(Mono.fromRunnable(() -> {
                        HttpHeaders headers = exchange.getResponse().getHeaders();
                        headers.add("Another", "a-Value");
                    }
                ));
        };
    }
}

