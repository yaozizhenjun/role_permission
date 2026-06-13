package com.example.behavior.rowpermission.domain.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Objects;

@Embeddable
public class AuthorizedSubject {
    @Enumerated(EnumType.STRING)
    @Column(name = "subject_type", nullable = false, length = 32)
    private SubjectType subjectType;

    @Column(name = "subject_value", nullable = false, length = 128)
    private String subjectValue;

    protected AuthorizedSubject() {
    }

    private AuthorizedSubject(SubjectType subjectType, String subjectValue) {
        this.subjectType = requireNonNull(subjectType, "授权对象类型不能为空");
        this.subjectValue = requireText(subjectValue, "授权对象值不能为空");
    }

    public static AuthorizedSubject of(SubjectType subjectType, String subjectValue) {
        return new AuthorizedSubject(subjectType, subjectValue);
    }

    public boolean matches(UserContext userContext) {
        if (subjectType == SubjectType.USER_ACCOUNT) {
            return subjectValue.equals(userContext.getUserId());
        }
        return userContext.getDepartmentNames().contains(subjectValue);
    }

    public SubjectType getSubjectType() {
        return subjectType;
    }

    public String getSubjectValue() {
        return subjectValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuthorizedSubject)) {
            return false;
        }
        AuthorizedSubject that = (AuthorizedSubject) o;
        return subjectType == that.subjectType && Objects.equals(subjectValue, that.subjectValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subjectType, subjectValue);
    }

    private static String requireText(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    private static <T> T requireNonNull(T value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}
