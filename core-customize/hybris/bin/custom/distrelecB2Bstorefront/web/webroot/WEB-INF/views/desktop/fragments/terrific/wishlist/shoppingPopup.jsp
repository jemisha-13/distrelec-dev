<%@ page trimDirectiveWhitespaces="true" contentType="application/json" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>

<json:array name="lists" items="${shoppingLists}" var="list">
    <json:object>
        <json:property name="id" value="${list.uniqueId}"/>
        <json:property name="value" value="${list.name}"/>
        <json:property name="count" value="${list.totalUnitCount}"/>
    </json:object>
</json:array>
