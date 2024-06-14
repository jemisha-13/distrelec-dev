<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<c:set var="customerType" value="${cartData.b2bCustomerData.customerType}" />
<c:set var="optionsEditable" value="${cartData.openOrderErpCode eq null}" />

<c:set var="addressActionMode">
	<c:choose>
		<c:when test="${optionsEditable}">
			<c:out value="change" />
		</c:when>
		<c:otherwise>
			<c:out value="" />
		</c:otherwise>
	</c:choose>
</c:set>

<c:if test="${customerType eq 'B2B' or customerType eq 'B2B_KEY_ACCOUNT'}">
	<c:if test="${isOrderEditable}">
		<mod:checkout-order-summary-cost-center-box
			skin="new-review" costCenter="${cartData.distCostCenter}"
			projectNumber="${cartData.projectNumber}" />
	</c:if>
</c:if>
