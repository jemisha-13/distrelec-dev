<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>

<c:set var="articleNumber">
	<format:articleNumber articleNumber="${product.codeErpRelevant}" />
</c:set>
<c:set var="min" value="1" />
<c:set var="step" value="1" />
<c:if test="${product.orderQuantityMinimum > 1}">
	<c:set var="min" value="${product.orderQuantityMinimum}" />
</c:if>
<c:if test="${product.orderQuantityStep > 1}">
	<c:set var="step" value="${product.orderQuantityStep}" />
</c:if>

<div class="cell cell-numeric-tabular ${not product.buyable ? 'js-disable-qty' : ''}">
	<div class="numeric numeric-small"
	<c:if test="${not isSubItem}">
		data-min="${min}" 
		data-step="${step}"
		data-min-error="<spring:message code='validation.error.min.order.quantity' arguments='${articleNumber},${min}' htmlEscape='true' />"
		data-step-error='<spring:message code="buyingSection.error.step.order.quantity" arguments="${step}" htmlEscape="true"/>'
	</c:if>
	>
		<c:if test="${bomQty > 0}">
			<c:set var="min" value="${bomQty}" />
		</c:if>

		<div class="btn-wrapper">
			<button class="btn numeric-btn numeric-btn-down disabled">&ndash;</button>
			<input type="text" name="countItems" class="ipt" placeholder="${min}" value="${min}" ${not product.buyable ? 'disabled' : ''}>
			<button class="btn numeric-btn numeric-btn-up ${not product.buyable ? 'disabled' : ''}">+</button>
		</div>
		<div class="numeric-popover popover top">
			<div class="arrow"></div>
			<div class="popover-content"></div>
		</div>
	</div>
</div>	