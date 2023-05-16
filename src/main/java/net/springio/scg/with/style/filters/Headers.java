package net.springio.scg.with.style.filters;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Headers {

    static List<String> buildHeaderValue(Object value) {
        if (value instanceof Collection) {
            return ((Collection<?>) value).stream()
                .map(Headers::mapSimpleValue)
                .collect(Collectors.toList());
        }
        return List.of(mapSimpleValue(value));
    }

    static Collection<Object> addValueToList(List<String> previousValues, Object value) {
        if (previousValues == null) {
            return (value instanceof Collection) ? (Collection<Object>) value : List.of(value);
        }

        final List<Object> mergedValues = new ArrayList<>();
        if (value instanceof Collection) {
            mergedValues.addAll(previousValues);
            mergedValues.addAll((Collection<?>) value);
        } else {
            mergedValues.add(value);

        }
        return mergedValues;
    }

    static String mapSimpleValue(Object value) {
        if (value instanceof String) {
            return (String) value;
        }
        if (value instanceof Instant) {
            return String.valueOf(((Instant) value).getEpochSecond());
        }
        return value.toString();
    }
}
