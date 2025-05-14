package com.example.ewaste.Utils;

import java.util.*;

/**
 * A simple JSON parser implementation to replace org.json library
 */
public class SimpleJsonParser {
    
    /**
     * Simple JSON Object implementation
     */
    public static class JSONObject {
        private final Map<String, Object> map = new HashMap<>();
        
        public JSONObject() {
        }
        
        public JSONObject(String jsonString) {
            parse(jsonString);
        }
        
        private void parse(String jsonString) {
            if (jsonString == null || jsonString.trim().isEmpty()) {
                return;
            }
            
            // Remove the outer braces
            String content = jsonString.trim();
            if (content.startsWith("{") && content.endsWith("}")) {
                content = content.substring(1, content.length() - 1).trim();
            } else {
                throw new IllegalArgumentException("Invalid JSON object: " + jsonString);
            }
            
            // Parse the key-value pairs
            int pos = 0;
            while (pos < content.length()) {
                // Find the key
                int keyStart = content.indexOf("\"", pos);
                if (keyStart == -1) break;
                
                int keyEnd = content.indexOf("\"", keyStart + 1);
                if (keyEnd == -1) break;
                
                String key = content.substring(keyStart + 1, keyEnd);
                
                // Find the colon
                int colon = content.indexOf(":", keyEnd);
                if (colon == -1) break;
                
                // Find the value
                int valueStart = colon + 1;
                while (valueStart < content.length() && Character.isWhitespace(content.charAt(valueStart))) {
                    valueStart++;
                }
                
                if (valueStart >= content.length()) break;
                
                // Determine the type of value
                char firstChar = content.charAt(valueStart);
                Object value;
                int valueEnd;
                
                if (firstChar == '{') {
                    // Object value
                    valueEnd = findMatchingBrace(content, valueStart);
                    if (valueEnd == -1) break;
                    
                    String objectStr = content.substring(valueStart, valueEnd + 1);
                    value = new JSONObject(objectStr);
                } else if (firstChar == '[') {
                    // Array value
                    valueEnd = findMatchingBracket(content, valueStart);
                    if (valueEnd == -1) break;
                    
                    String arrayStr = content.substring(valueStart, valueEnd + 1);
                    value = new JSONArray(arrayStr);
                } else if (firstChar == '"') {
                    // String value
                    valueEnd = content.indexOf("\"", valueStart + 1);
                    while (valueEnd > 0 && content.charAt(valueEnd - 1) == '\\') {
                        valueEnd = content.indexOf("\"", valueEnd + 1);
                    }
                    if (valueEnd == -1) break;
                    
                    value = content.substring(valueStart + 1, valueEnd);
                    // Unescape the string
                    value = ((String) value).replace("\\\"", "\"")
                                           .replace("\\\\", "\\")
                                           .replace("\\n", "\n")
                                           .replace("\\r", "\r")
                                           .replace("\\t", "\t");
                } else if (firstChar == 't' && content.substring(valueStart).startsWith("true")) {
                    // Boolean true
                    value = true;
                    valueEnd = valueStart + 3;
                } else if (firstChar == 'f' && content.substring(valueStart).startsWith("false")) {
                    // Boolean false
                    value = false;
                    valueEnd = valueStart + 4;
                } else if (firstChar == 'n' && content.substring(valueStart).startsWith("null")) {
                    // Null value
                    value = null;
                    valueEnd = valueStart + 3;
                } else {
                    // Number value
                    valueEnd = content.indexOf(",", valueStart);
                    if (valueEnd == -1) {
                        valueEnd = content.indexOf("}", valueStart);
                        if (valueEnd == -1) valueEnd = content.length();
                    }
                    
                    String numStr = content.substring(valueStart, valueEnd).trim();
                    try {
                        if (numStr.contains(".")) {
                            value = Double.parseDouble(numStr);
                        } else {
                            value = Integer.parseInt(numStr);
                        }
                    } catch (NumberFormatException e) {
                        value = numStr;
                    }
                }
                
                // Add the key-value pair to the map
                map.put(key, value);
                
                // Move to the next key-value pair
                pos = valueEnd + 1;
                
                // Skip the comma
                while (pos < content.length() && content.charAt(pos) != ',') {
                    pos++;
                }
                pos++;
            }
        }
        
        private int findMatchingBrace(String content, int start) {
            int count = 1;
            for (int i = start + 1; i < content.length(); i++) {
                if (content.charAt(i) == '{') {
                    count++;
                } else if (content.charAt(i) == '}') {
                    count--;
                    if (count == 0) {
                        return i;
                    }
                }
            }
            return -1;
        }
        
        private int findMatchingBracket(String content, int start) {
            int count = 1;
            for (int i = start + 1; i < content.length(); i++) {
                if (content.charAt(i) == '[') {
                    count++;
                } else if (content.charAt(i) == ']') {
                    count--;
                    if (count == 0) {
                        return i;
                    }
                }
            }
            return -1;
        }
        
        public void put(String key, Object value) {
            map.put(key, value);
        }
        
        public Object get(String key) {
            return map.get(key);
        }
        
        public boolean has(String key) {
            return map.containsKey(key);
        }
        
        public String getString(String key) {
            Object value = map.get(key);
            return value != null ? value.toString() : null;
        }
        
        public int getInt(String key) {
            Object value = map.get(key);
            if (value instanceof Integer) {
                return (Integer) value;
            } else if (value instanceof String) {
                return Integer.parseInt((String) value);
            }
            return 0;
        }
        
        public boolean getBoolean(String key) {
            Object value = map.get(key);
            if (value instanceof Boolean) {
                return (Boolean) value;
            } else if (value instanceof String) {
                return Boolean.parseBoolean((String) value);
            }
            return false;
        }
        
        public JSONObject getJSONObject(String key) {
            Object value = map.get(key);
            if (value instanceof JSONObject) {
                return (JSONObject) value;
            } else if (value instanceof String) {
                return new JSONObject((String) value);
            }
            return new JSONObject();
        }
        
        public JSONArray getJSONArray(String key) {
            Object value = map.get(key);
            if (value instanceof JSONArray) {
                return (JSONArray) value;
            } else if (value instanceof String) {
                return new JSONArray((String) value);
            }
            return new JSONArray();
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            
            boolean first = true;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (!first) {
                    sb.append(",");
                }
                first = false;
                
                sb.append("\"").append(entry.getKey()).append("\":");
                
                Object value = entry.getValue();
                if (value == null) {
                    sb.append("null");
                } else if (value instanceof String) {
                    sb.append("\"").append(escapeJson((String) value)).append("\"");
                } else {
                    sb.append(value);
                }
            }
            
            sb.append("}");
            return sb.toString();
        }
    }
    
    /**
     * Simple JSON Array implementation
     */
    public static class JSONArray {
        private final List<Object> list = new ArrayList<>();
        
        public JSONArray() {
        }
        
        public JSONArray(String jsonString) {
            parse(jsonString);
        }
        
        private void parse(String jsonString) {
            if (jsonString == null || jsonString.trim().isEmpty()) {
                return;
            }
            
            // Remove the outer brackets
            String content = jsonString.trim();
            if (content.startsWith("[") && content.endsWith("]")) {
                content = content.substring(1, content.length() - 1).trim();
            } else {
                throw new IllegalArgumentException("Invalid JSON array: " + jsonString);
            }
            
            // Parse the elements
            int pos = 0;
            while (pos < content.length()) {
                // Skip whitespace
                while (pos < content.length() && Character.isWhitespace(content.charAt(pos))) {
                    pos++;
                }
                
                if (pos >= content.length()) break;
                
                // Determine the type of value
                char firstChar = content.charAt(pos);
                Object value;
                int valueEnd;
                
                if (firstChar == '{') {
                    // Object value
                    valueEnd = findMatchingBrace(content, pos);
                    if (valueEnd == -1) break;
                    
                    String objectStr = content.substring(pos, valueEnd + 1);
                    value = new JSONObject(objectStr);
                } else if (firstChar == '[') {
                    // Array value
                    valueEnd = findMatchingBracket(content, pos);
                    if (valueEnd == -1) break;
                    
                    String arrayStr = content.substring(pos, valueEnd + 1);
                    value = new JSONArray(arrayStr);
                } else if (firstChar == '"') {
                    // String value
                    valueEnd = content.indexOf("\"", pos + 1);
                    while (valueEnd > 0 && content.charAt(valueEnd - 1) == '\\') {
                        valueEnd = content.indexOf("\"", valueEnd + 1);
                    }
                    if (valueEnd == -1) break;
                    
                    value = content.substring(pos + 1, valueEnd);
                    // Unescape the string
                    value = ((String) value).replace("\\\"", "\"")
                                           .replace("\\\\", "\\")
                                           .replace("\\n", "\n")
                                           .replace("\\r", "\r")
                                           .replace("\\t", "\t");
                } else if (firstChar == 't' && content.substring(pos).startsWith("true")) {
                    // Boolean true
                    value = true;
                    valueEnd = pos + 3;
                } else if (firstChar == 'f' && content.substring(pos).startsWith("false")) {
                    // Boolean false
                    value = false;
                    valueEnd = pos + 4;
                } else if (firstChar == 'n' && content.substring(pos).startsWith("null")) {
                    // Null value
                    value = null;
                    valueEnd = pos + 3;
                } else {
                    // Number value
                    valueEnd = content.indexOf(",", pos);
                    if (valueEnd == -1) {
                        valueEnd = content.indexOf("]", pos);
                        if (valueEnd == -1) valueEnd = content.length();
                    }
                    
                    String numStr = content.substring(pos, valueEnd).trim();
                    try {
                        if (numStr.contains(".")) {
                            value = Double.parseDouble(numStr);
                        } else {
                            value = Integer.parseInt(numStr);
                        }
                    } catch (NumberFormatException e) {
                        value = numStr;
                    }
                }
                
                // Add the value to the list
                list.add(value);
                
                // Move to the next element
                pos = valueEnd + 1;
                
                // Skip the comma
                while (pos < content.length() && content.charAt(pos) != ',') {
                    pos++;
                }
                pos++;
            }
        }
        
        private int findMatchingBrace(String content, int start) {
            int count = 1;
            for (int i = start + 1; i < content.length(); i++) {
                if (content.charAt(i) == '{') {
                    count++;
                } else if (content.charAt(i) == '}') {
                    count--;
                    if (count == 0) {
                        return i;
                    }
                }
            }
            return -1;
        }
        
        private int findMatchingBracket(String content, int start) {
            int count = 1;
            for (int i = start + 1; i < content.length(); i++) {
                if (content.charAt(i) == '[') {
                    count++;
                } else if (content.charAt(i) == ']') {
                    count--;
                    if (count == 0) {
                        return i;
                    }
                }
            }
            return -1;
        }
        
        public void put(Object value) {
            list.add(value);
        }
        
        public Object get(int index) {
            return list.get(index);
        }
        
        public int length() {
            return list.size();
        }
        
        public String getString(int index) {
            Object value = list.get(index);
            return value != null ? value.toString() : null;
        }
        
        public int getInt(int index) {
            Object value = list.get(index);
            if (value instanceof Integer) {
                return (Integer) value;
            } else if (value instanceof String) {
                return Integer.parseInt((String) value);
            }
            return 0;
        }
        
        public boolean getBoolean(int index) {
            Object value = list.get(index);
            if (value instanceof Boolean) {
                return (Boolean) value;
            } else if (value instanceof String) {
                return Boolean.parseBoolean((String) value);
            }
            return false;
        }
        
        public JSONObject getJSONObject(int index) {
            Object value = list.get(index);
            if (value instanceof JSONObject) {
                return (JSONObject) value;
            } else if (value instanceof String) {
                return new JSONObject((String) value);
            }
            return new JSONObject();
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            
            boolean first = true;
            for (Object value : list) {
                if (!first) {
                    sb.append(",");
                }
                first = false;
                
                if (value == null) {
                    sb.append("null");
                } else if (value instanceof String) {
                    sb.append("\"").append(escapeJson((String) value)).append("\"");
                } else {
                    sb.append(value);
                }
            }
            
            sb.append("]");
            return sb.toString();
        }
    }
    
    /**
     * Escapes special characters in a JSON string
     */
    public static String escapeJson(String input) {
        if (input == null) {
            return "null";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '\\': sb.append("\\\\"); break;
                case '\"': sb.append("\\\""); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default: sb.append(c);
            }
        }
        return sb.toString();
    }
}
