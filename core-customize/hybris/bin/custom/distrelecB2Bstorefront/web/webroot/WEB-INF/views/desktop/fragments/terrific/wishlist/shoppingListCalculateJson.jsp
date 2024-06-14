<%@ page trimDirectiveWhitespaces="true" contentType="application/json" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>

<json:object>
	<c:choose>
		<c:when test="${not empty shoppingList}">
			<json:property name="error" value="false"/>
			<json:property name="list-id" value="${shoppingList.uniqueId}"/>
			<json:property name="price">
				<format:price format="price" priceData="${shoppingList.totalPrice}" />
			</json:property>
			<json:property name="currency">
				<format:price format="currency" priceData="${shoppingList.totalPrice}" />
			</json:property>
			<json:property name="subTotal">
				<format:price format="price" priceData="${shoppingList.subTotal}" />
			</json:property>
			<json:property name="totalTax">
				<format:price format="price" priceData="${shoppingList.totalTax}" />
			</json:property>
		</c:when>
		<c:otherwise>
			<json:property name="error" value="true"/>
			<json:property name="message">
				<spring:message code="${errorMsg}" text="Unknown error occurs during shopping list calculation" />
			</json:property>
		</c:otherwise>
	</c:choose>
</json:object>
