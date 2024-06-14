<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="currentOrderCode" value="byName:asc"/>
<c:if test="${not empty sortCode}">
	<c:set var="currentOrderCode" value="${sortCode}"/>
</c:if>

<c:choose>
	<c:when test="${siteUid eq 'distrelec_FR'}">
		<select id="select-account-order" name="account-order" class="selectpicker selectboxit-meta select-user-management-fr" data-pretext='<spring:message code="accountorder.userManagement.order.by" />: '>
			<c:forEach items="${sortTypeList}" var="sortItem">
				<c:set var="selectFlag" value="${sortItem == 'byStatus:desc' or sortItem == 'byYearlyBudget:asc' or sortItem == 'byYearlyBudget:desc'}" />
				<c:if test="${selectFlag eq false}">
					<c:out value="${sortItem}" />
					<option value="${sortItem}" ${sortItem == currentOrderCode ? 'selected' : ''}><spring:message code="text.account.company.userManagement.sort.${sortItem}" /></option>
				</c:if>
			</c:forEach>
		</select>
	</c:when>
	<c:otherwise>
		<select id="select-account-order" name="account-order" class="selectpicker selectboxit-meta" data-pretext='<spring:message code="accountorder.userManagement.order.by" />: '>
			<c:forEach items="${sortTypeList}" var="sortItem">
				<c:out value="${sortItem}" />
				<option value="${sortItem}" ${sortItem == currentOrderCode ? 'selected' : ''}><spring:message code="text.account.company.userManagement.sort.${sortItem}" /></option>
			</c:forEach>
		</select>
	</c:otherwise>
</c:choose>