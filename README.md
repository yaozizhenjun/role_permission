# 行权限模块说明

这是一个基于 JDK8 + Spring Boot 2.7 + Maven 3 的行权限后端模块骨架，可迁移到现有用户行为分析系统。

## 覆盖功能

- 资源列表：运营用户选择资源，当前按事件/汇总类资源建模。
- 资源下规则列表：查看、新增、编辑、删除、启停某个事件上的行权限规则。
- 规则配置：支持权限名称、描述、生效系统、授权对象、过滤规则详情。
- 分析接入：分析执行前根据依赖资源、分析用户、所属系统判断是否追加过滤条件。
- 安全兜底：规则命中但用户动态属性缺失时，追加一个永不匹配条件，避免绕过行权限。
- DDD 分层：`domain` 放聚合和值对象，`application` 放用例服务和 DTO，`infrastructure` 放 JPA 实现，`interfaces` 放 REST 接口。
- 非 JSON 存储：生效系统、授权对象、过滤表达式分别保存到 `row_permission_rule_system`、`row_permission_rule_subject`、`row_permission_rule_filter`。

## 关键接口

- `GET /api/row-permission/resources`：资源列表，对应“选择资源”页面。
- `POST /api/row-permission/resources`：新增行权限资源。
- `GET /api/row-permission/resources/{resourceId}/rules`：资源下规则列表。
- `POST /api/row-permission/resources/{resourceId}/rules`：新增规则。
- `PUT /api/row-permission/rules/{ruleId}`：编辑规则。
- `PATCH /api/row-permission/rules/{ruleId}/status?status=ENABLED`：启停规则。
- `DELETE /api/row-permission/rules/{ruleId}`：删除规则。
- `POST /api/analysis/preview-with-row-permission`：模拟分析前追加行权限过滤。

## 分析侧接入方式

在原分析查询构造 SQL/ES/ClickHouse 查询条件前调用：

```java
AnalysisPreviewResponse result = rowPermissionDecisionService.apply(request);
List<FilterConditionDto> finalFilters = result.finalFilters;
```

真实项目中通常只需要迁移 `RowPermissionDecisionApplicationService`，并把 `FilterConditionDto` 转换成现有查询引擎的过滤表达式。

## 示例规则

```json
{
  "ruleName": "分中心用户背对背访问",
  "ruleDescription": "分中心用户只能访问自己所在城市的数据",
  "enabledSystems": ["微光分析中心"],
  "subjects": [
    {
      "subjectType": "DEPARTMENT",
      "values": ["北京分中心", "杭州分中心"]
    }
  ],
  "filterConditions": [
    {
      "fieldName": "city",
      "fieldLabel": "城市",
      "operator": "EQ",
      "rightType": "USER_ATTRIBUTE",
      "rightValue": "city",
      "rightLabel": "当前用户属性：所在城市"
    }
  ],
  "status": "ENABLED"
}
```

## 本地启动

```bash
mvn spring-boot:run
```

默认使用 H2 内存库，启动后会加载 `schema.sql` 和 `data.sql`。

## Maven 3 编译

```bash
mvn test
```

`pom.xml` 使用 `maven-enforcer-plugin` 约束 Maven 版本为 `3.6.3 <= Maven < 4.0.0`，Java 版本为 JDK8。
