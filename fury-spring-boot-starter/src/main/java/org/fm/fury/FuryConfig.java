package org.fm.fury;

import java.util.HashMap;
import java.util.Map;

public class FuryConfig {

    Map<String, Object> configMap = new HashMap<>();

    public void put(String key, Object value) {
        configMap.put(key, value);
    }

    public Object get(String key) {
        return configMap.get(key);
    }
}
