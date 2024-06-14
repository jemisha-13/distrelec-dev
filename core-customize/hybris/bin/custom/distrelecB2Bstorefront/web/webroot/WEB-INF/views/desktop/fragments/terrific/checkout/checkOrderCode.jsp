<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<spring:message code="text.store.dateformat" var="datePattern" />

<json:object>
	<json:property name="status" value="${status}" />
	<json:property name="erpCode" value="${erpCode}" />
	
	<c:if test="${not empty erpVoucher}">
		<json:object name="voucher">
			<json:property name="code" value="${erpVoucher.code}" />
			<json:property name="fromDate">
				<fmt:formatDate value="${erpVoucher.validFrom}" dateStyle="short" timeStyle="short" type="date" pattern="${datePattern}" />
			</json:property>
			<json:property name="toDate">
				<fmt:formatDate value="${erpVoucher.validUntil}" dateStyle="short" timeStyle="short" type="date" pattern="${datePattern}" />
			</json:property>
			<json:property name="value">
				<format:price format="simple" priceData="${erpVoucher.value}" />
			</json:property>
		</json:object>
	</c:if>
</json:object>
