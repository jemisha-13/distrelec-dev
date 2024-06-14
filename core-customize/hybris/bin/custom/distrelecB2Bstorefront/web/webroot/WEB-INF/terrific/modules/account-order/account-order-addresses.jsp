<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="currentOrderCode" value="byCity"/>
<c:if test="${not empty sortBy}">
	<c:set var="currentOrderCode" value="${sortBy}"/>
	<c:set var="desc" value="desc"/>
	<c:set var="asc" value="asc"/>
</c:if>

<select id="select-account-order" name="account-order" class="selectpicker" data-pretext='<spring:message code="accountorder.addresses.order.by" />: '>
	<c:forEach items="${sortByList}" var="sortItem">
		<c:set var="currentSortItemAsc" value="${sortItem}${asc}"/>
		<c:set var="currentSortItemDesc" value="${sortItem}${desc}"/>
		<c:set var="currentSortCode" value="${currentOrderCode}${sortType}"/>
		<option value="${sortItem}:asc" ${currentSortItemAsc == currentSortCode  ? 'selected' : ''}><spring:message code="accountorder.addresses.sort.${sortItem}asc" /></option>
		<option value="${sortItem}:desc" ${currentSortItemDesc == currentSortCode  ? 'selected' : ''}><spring:message code="accountorder.addresses.sort.${sortItem}desc" /></option>
	</c:forEach>
</select>