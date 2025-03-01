package com.example.ewaste.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static Map<String, Object> parseJson(String json) throws IOException {
        return mapper.readValue(json, Map.class);
    }

    public static String extractString(String json, String key) throws IOException {
        return parseJson(json).get(key).toString();
    }
}