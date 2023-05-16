package net.springio.scg.with.style.filters;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.WebFilterChainProxy;
import org.springframework.stereotype.Component;

@Component
public class AuthorizeGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    private final ReactiveJwtDecoder reactiveJwtDecoder;

    AuthorizeGatewayFilterFactory(ReactiveJwtDecoder gatewayReactiveJwtDecoder) {
        this.reactiveJwtDecoder = gatewayReactiveJwtDecoder;
    }

    @Override
    public GatewayFilter apply(Object config) {
        SecurityWebFilterChain securityChain =
            baseConfiguration(ServerHttpSecurity.http())

                .oauth2ResourceServer()
                .jwt().jwtDecoder(reactiveJwtDecoder).and()
                .and()

                .authorizeExchange()
                .anyExchange()
                .authenticated()
                .and()
                .build();

        return (exchange, chain) -> {
            return new WebFilterChainProxy(securityChain)
                .filter(exchange, chain::filter);
        };
    }

    private static ServerHttpSecurity baseConfiguration(ServerHttpSecurity httpSecurity) {
        return httpSecurity
            .headers().disable()
            .httpBasic().disable()
            .csrf().disable()
            .logout().disable();
    }

}
