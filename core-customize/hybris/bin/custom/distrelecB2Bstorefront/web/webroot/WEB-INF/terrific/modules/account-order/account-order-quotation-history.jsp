<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="currentOrderCode" value="byRequestDate"/>
<c:if test="${not empty sortCode}">
	<c:set var="currentOrderCode" value="${sortCode}"/>
</c:if>

<c:if test="${not empty sortTypeList}">
	<select id="select-account-order" name="account-order" class="selectpicker" data-pretext='<spring:message code="accountorder.history.order.by" />: '>
		<c:forEach items="${sortTypeList}" var="sortItem">
			<option value="${sortItem}" ${sortItem == currentOrderCode ? 'selected' : ''}><spring:message code="accountorder.history.sort.${fn:replace(sortItem, ':', '')}" /></option>
		</c:forEach>
	</select>
</c:if>