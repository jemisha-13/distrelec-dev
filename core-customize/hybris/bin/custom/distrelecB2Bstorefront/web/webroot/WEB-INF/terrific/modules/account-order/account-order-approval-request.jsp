<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="currentOrderCode" value="byDate"/>
<c:if test="${not empty currentOrder}">
	<c:set var="currentOrderCode" value="${currentOrder}"/>
</c:if>

<select id="select-account-order" name="account-order" class="selectpicker" data-pretext='<spring:message code="accountorder.approvals.order.by" />: '>
	<c:forEach items="${sortTypeList}" var="sortItem">
		<option value="${sortItem}" ${sortItem == currentOrderCode ? 'selected' : ''}><spring:message code="accountorder.approvals.sort.${fn:replace(sortItem, ':', '')}" /></option>
	</c:forEach>
</select>