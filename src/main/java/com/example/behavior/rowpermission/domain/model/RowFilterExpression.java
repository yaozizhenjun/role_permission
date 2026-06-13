package com.example.behavior.rowpermission.domain.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Objects;

@Embeddable
public class RowFilterExpression {
    @Column(name = "field_name", nullable = false, length = 128)
    private String fieldName;

    @Column(name = "field_label", length = 128)
    private String fieldLabel;

    @Enumerated(EnumType.STRING)
    @Column(name = "operator", nullable = false, length = 32)
    private FilterOperator operator;

    @Enumerated(EnumType.STRING)
    @Column(name = "right_type", nullable = false, length = 32)
    private FilterValueType rightType;

    @Column(name = "right_value", nullable = false, length = 256)
    private String rightValue;

    @Column(name = "right_label", length = 128)
    private String rightLabel;

    protected RowFilterExpression() {
    }

    private RowFilterExpression(String fieldName,
                                String fieldLabel,
                                FilterOperator operator,
                                FilterValueType rightType,
                                String rightValue,
                                String rightLabel) {
        this.fieldName = requireText(fieldName, "过滤字段不能为空");
        this.fieldLabel = fieldLabel;
        this.operator = requireNonNull(operator, "过滤操作符不能为空");
        this.rightType = requireNonNull(rightType, "过滤值类型不能为空");
        this.rightValue = requireText(rightValue, "过滤值不能为空");
        this.rightLabel = rightLabel;
    }

    public static RowFilterExpression of(String fieldName,
                                         String fieldLabel,
                                         FilterOperator operator,
                                         FilterValueType rightType,
                                         String rightValue,
                                         String rightLabel) {
        return new RowFilterExpression(fieldName, fieldLabel, operator, rightType, rightValue, rightLabel);
    }

    public RowFilterExpression resolveWithLiteralValue(String literalValue) {
        return of(fieldName, fieldLabel, operator, FilterValueType.LITERAL, literalValue, rightLabel);
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldLabel() {
        return fieldLabel;
    }

    public FilterOperator getOperator() {
        return operator;
    }

    public FilterValueType getRightType() {
        return rightType;
    }

    public String getRightValue() {
        return rightValue;
    }

    public String getRightLabel() {
        return rightLabel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RowFilterExpression)) {
            return false;
        }
        RowFilterExpression that = (RowFilterExpression) o;
        return Objects.equals(fieldName, that.fieldName)
                && Objects.equals(fieldLabel, that.fieldLabel)
                && operator == that.operator
                && rightType == that.rightType
                && Objects.equals(rightValue, that.rightValue)
                && Objects.equals(rightLabel, that.rightLabel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldName, fieldLabel, operator, rightType, rightValue, rightLabel);
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
