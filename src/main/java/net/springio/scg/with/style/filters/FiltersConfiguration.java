package net.springio.scg.with.style.filters;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.stereotype.Component;

@Component
class FiltersConfiguration {

    @Bean
    ReactiveJwtDecoder gatewayReactiveJwtDecoder(
        @Value("${spring.security.oauth2.client.provider.sso.issuer-uri}") String ssoIssuerUri) {
        return ReactiveJwtDecoders.fromOidcIssuerLocation(ssoIssuerUri);
    }
}
