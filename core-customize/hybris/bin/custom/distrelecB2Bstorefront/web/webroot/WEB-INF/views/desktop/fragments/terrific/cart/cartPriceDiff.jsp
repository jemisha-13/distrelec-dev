<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>


<json:object>
	<json:property name="source" value="${priceDiffForm.source}" />
	<json:property name="target" value="${priceDiffForm.target}" />
	
	<c:if test="${not empty priceData}">
		<json:object name="priceDiff">
			<json:property name="amount">
				<format:price format="price" priceData="${priceData}" />
			</json:property>
			<json:property name="currency">
				<format:price format="currency" fallBackCurrency="" priceData="${priceData}" />
			</json:property>
		</json:object>
	</c:if>

	<c:if test="${not empty error}">
		<json:property name="error" value="${error}" />
	</c:if>
	<json:property name="message">
		<c:if test="${not empty errorMsg}">
			<spring:message code="${errorMsg}" text="Unknown Error!" />
		</c:if>
	</json:property>
</json:object>

