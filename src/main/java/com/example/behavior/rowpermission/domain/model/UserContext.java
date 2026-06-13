package com.example.behavior.rowpermission.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserContext {
    private final String userId;
    private final String userName;
    private final List<String> departmentNames;
    private final Map<String, String> attributes;

    public UserContext(String userId, String userName, List<String> departmentNames, Map<String, String> attributes) {
        this.userId = requireText(userId, "用户ID不能为空");
        this.userName = userName;
        this.departmentNames = departmentNames == null
                ? Collections.<String>emptyList()
                : Collections.unmodifiableList(new ArrayList<String>(departmentNames));
        this.attributes = attributes == null
                ? Collections.<String, String>emptyMap()
                : Collections.unmodifiableMap(new HashMap<String, String>(attributes));
    }

    public String resolveAttribute(String attributeName, String fallbackValue) {
        if (!attributes.containsKey(attributeName)) {
            return fallbackValue;
        }
        String value = attributes.get(attributeName);
        return value == null || value.trim().isEmpty() ? fallbackValue : value;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public List<String> getDepartmentNames() {
        return departmentNames;
    }

    private static String requireText(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}
