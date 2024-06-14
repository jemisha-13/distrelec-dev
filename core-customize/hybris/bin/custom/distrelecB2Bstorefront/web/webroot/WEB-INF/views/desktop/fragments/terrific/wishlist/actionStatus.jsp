<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>

<json:object>
	<json:object name="action">
		<c:choose>
			<c:when test="${actionStatus}">
				<json:property name="status" value="true" />
			</c:when>
			<c:otherwise>
				<json:property name="status" value="false" />
			</c:otherwise>
		</c:choose>
	</json:object>
	<c:if test="${not empty favoriteListCount}">
		<json:property name="favoriteListCount" value="${favoriteListCount}" />
	</c:if>
	<c:if test="${not empty shoppingListCount}">
		<json:array name="listData">
			<c:forEach var="list" items="${shoppingListCount}">
				<json:object>
					<json:property name="listId" value="${list.key}" />
					<json:property name="count" value="${list.value}" />
				</json:object>
			</c:forEach>
		</json:array>
	</c:if>
	<c:if test="${not empty newListId}">
		<json:property name="newListId" value="${newListId}" />
	</c:if>
</json:object>
