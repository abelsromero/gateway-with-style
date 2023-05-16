package net.springio.scg.with.style.filters;


import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import net.springio.scg.with.style.config.KeyValue;
import net.springio.scg.with.style.config.KeyValueConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

/**
 * Replaces a JSON attribute value, it can receive a list of pairs
 * separated by commas where first parameter is a JSON attribute selector and
 * second parameter is the desired value for the selection.
 * <p>
 * Example:
 * -  RewriteJsonAttributesResponseBody=slides[0].title:My Title,date:11-11-2011
 */
@Component
public class RewriteJsonResponseBodyGatewayFilterFactory extends AbstractGatewayFilterFactory<KeyValueConfig> {

    @Override
    public ShortcutType shortcutType() {
        return ShortcutType.GATHER_LIST;
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList("keyValues");
    }

    @Override
    public KeyValueConfig newConfig() {
        return new KeyValueConfig();
    }

    @Override
    public Class<KeyValueConfig> getConfigClass() {
        return KeyValueConfig.class;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RewriteJsonResponseBodyGatewayFilterFactory.class);

    private final ModifyResponseBodyGatewayFilterFactory modifyResponseBodyGatewayFilterFactory;

    public RewriteJsonResponseBodyGatewayFilterFactory(
        ModifyResponseBodyGatewayFilterFactory modifyResponseBodyGatewayFilterFactory) {
        this.modifyResponseBodyGatewayFilterFactory = modifyResponseBodyGatewayFilterFactory;
    }

    @Override
    public GatewayFilter apply(KeyValueConfig config) {
        ModifyResponseBodyGatewayFilterFactory.Config modifyResponseBodyConfig = new ModifyResponseBodyGatewayFilterFactory.Config();
        modifyResponseBodyConfig.setInClass(String.class);
        modifyResponseBodyConfig.setOutClass(String.class);

        RewriteFunction<String, String> rewriteFunction = (exchange, body) -> {
            if (exchange.getResponse().getHeaders().getContentType().isCompatibleWith(MediaType.APPLICATION_JSON)) {
                DocumentContext jsonBody = JsonPath.parse(body);

                for (KeyValue kv : config.getKeyValues()) {
                    try {
                        jsonBody.set("$." + kv.getKey(), kv.getValue());
                    } catch (PathNotFoundException e) {
                        LOGGER.debug("Could not find JSON path: " + kv.getKey(), e.getMessage());
                    }
                }
                return Mono.just(jsonBody.jsonString());
            }
            return Mono.just(body);
        };
        modifyResponseBodyConfig.setRewriteFunction(rewriteFunction);

        return modifyResponseBodyGatewayFilterFactory.apply(modifyResponseBodyConfig);
    }

}
