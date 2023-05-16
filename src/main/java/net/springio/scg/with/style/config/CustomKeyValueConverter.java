package net.springio.scg.with.style.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CustomKeyValueConverter implements Converter<String, KeyValue> {

	private static final String INVALID_CONFIGURATION_MESSAGE = "Invalid configuration, expected format: 'key:value'";

	@Override
	public KeyValue convert(String source) throws IllegalArgumentException {
		try {
			String[] split = source.split(":");
			if (source.contains(":") && StringUtils.hasText(split[0])) {
				return new KeyValue(split[0], split.length == 1 ? "" : split[1]);
			}
			throw new IllegalArgumentException(INVALID_CONFIGURATION_MESSAGE);
		}
		catch (ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException(INVALID_CONFIGURATION_MESSAGE);
		}
	}
}



