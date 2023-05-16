package net.springio.scg.with.style.filters;

import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static net.springio.scg.with.style.filters.Headers.addValueToList;
import static net.springio.scg.with.style.filters.Headers.buildHeaderValue;

@Component
public class ExtractClaimGatewayFilterFactory
    implements GatewayFilterFactory<ExtractClaimGatewayFilterFactory.Config> {

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> ReactiveSecurityContextHolder
            .getContext()
            .map(context -> {
                ServerWebExchange mutatedExchange = addClaim(config, exchange,
                    context.getAuthentication());
                return mutatedExchange != null ? mutatedExchange : exchange;
            })
            .switchIfEmpty(Mono.fromSupplier(() -> markAsUnauthenticated(exchange)))
            .flatMap(chain::filter);
    }


    private static final Logger logger = LoggerFactory.getLogger(ExtractClaimGatewayFilterFactory.class);

    /**
     * Notify upstream the request is not authenticated
     */
    private ServerWebExchange markAsUnauthenticated(ServerWebExchange exchange) {
        logger.warn("No Session found!");
        return exchange.mutate()
            .request(req -> req.header("X-Authentication", "anonymous"))
            .build();
    }

    private ServerWebExchange addClaim(Config config, ServerWebExchange exchange,
                                       Authentication authentication) {

        if (authentication instanceof JwtAuthenticationToken) {
            logger.debug("Found JwtAuthenticationToken authentication object");

            return copyClaimToHeader(exchange,
                config,
                () -> {
                    Map<String, Object> claims = ((JwtAuthenticationToken) authentication).getTokenAttributes();
                    return claims.get(config.claim);
                });
        }
        return null;
    }

    private ServerWebExchange copyClaimToHeader(ServerWebExchange exchange, Config config,
                                                Supplier<Object> valueSupplier) {
        return exchange.mutate()
            .request(request -> request
                .headers(headers -> {
                    Object value = valueSupplier.get();

                    if (value != null) {
                        List<String> previousValues = headers.get(config.headerName);
                        logger.debug(
                            "Found value for claim '{}', including into request headers",
                            config.claim);

                        headers.put(
                            config.headerName,
                            buildHeaderValue(addValueToList(previousValues, value)));
                    }
                }))
            .build();
    }


    @Validated
    static class Config {

        @NotBlank
        private String claim;

        @NotBlank
        private String headerName;

        public String getClaim() {
            return claim;
        }

        public void setClaim(String claim) {
            this.claim = claim;
        }

        public String getHeaderName() {
            return headerName;
        }

        public void setHeaderName(String headerName) {
            this.headerName = headerName;
        }
    }

    @Override
    public Config newConfig() {
        return new Config();
    }

    @Override
    public Class<Config> getConfigClass() {
        return Config.class;
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return List.of("claim", "headerName");
    }
}
