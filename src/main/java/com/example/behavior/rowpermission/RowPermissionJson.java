package com.example.behavior.rowpermission;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
class RowPermissionJson {
    private final ObjectMapper objectMapper;

    RowPermissionJson(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    String write(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (IOException ex) {
            throw new IllegalArgumentException("行权限配置序列化失败", ex);
        }
    }

    List<String> readStringList(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new ArrayList<String>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {
            });
        } catch (IOException ex) {
            throw new IllegalArgumentException("生效系统配置解析失败", ex);
        }
    }

    List<RowPermissionDtos.SubjectConditionDto> readSubjects(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<RowPermissionDtos.SubjectConditionDto>>() {
            });
        } catch (IOException ex) {
            throw new IllegalArgumentException("授权对象配置解析失败", ex);
        }
    }

    List<RowPermissionDtos.FilterConditionDto> readFilters(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<RowPermissionDtos.FilterConditionDto>>() {
            });
        } catch (IOException ex) {
            throw new IllegalArgumentException("过滤规则配置解析失败", ex);
        }
    }
}
