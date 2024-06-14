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

<div class="gu-2 cell cell-numeric-tabular">
	<div class="numeric numeric-small"
	<c:if test="${not isSubItem}">
		data-min="${min}" 
		data-step="${step}" 
		data-min-error="<spring:message code='validation.error.min.order.quantity' arguments='${articleNumber},${min}' htmlEscape='true' />"
		data-step-error="<spring:message code='validation.error.steps.order.quantity' arguments='${articleNumber},${step}' htmlEscape='true'/>"
	</c:if>
	>	
		<input type="text" name="countItems" class="ipt" value="1">
		<div class="btn-wrapper">
	        <button class="btn numeric-btn numeric-btn-up">+</button>
	        <button class="btn numeric-btn numeric-btn-down disabled">&ndash;</button>
        </div>
		<div class="numeric-popover popover top">
			<div class="arrow"></div>
			<div class="popover-content"></div>
		</div>
	</div>
</div>	