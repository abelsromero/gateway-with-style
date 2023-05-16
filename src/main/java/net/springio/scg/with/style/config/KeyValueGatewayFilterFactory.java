package net.springio.scg.with.style.config;

import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;

import java.util.Collections;
import java.util.List;

public abstract class KeyValueGatewayFilterFactory extends AbstractGatewayFilterFactory<KeyValueConfig> {

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
}
