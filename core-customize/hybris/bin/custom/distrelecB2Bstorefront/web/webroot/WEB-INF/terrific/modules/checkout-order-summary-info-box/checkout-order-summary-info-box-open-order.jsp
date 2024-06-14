<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>

<spring:message code="checkout.summary.openOrder.detailsBox.title" var="sTitle"/>
<spring:message code="checkout.summary.openOrder.detailsBox.button.text" var="sButtonChange"/>

<spring:message code="text.store.dateformat" var="datePattern" />

<c:set var="isEditableByAllOption" value="${cartData != null ? cartData.openOrderEditableForAllContacts : orderData.openOrderEditableForAllContacts}" />
<c:set var="selectedClosingDate" value="${cartData != null ? cartData.openOrderSelectedClosingDate : orderData.openOrderSelectedClosingDate}"/>
<c:set var="orderCode" value="${cartData != null ? cartData.openOrderErpCode : orderData.erpOpenOrderCode}"/>
<c:set var="selectedClosingDateFormatted">
	<fmt:formatDate value="${selectedClosingDate}" dateStyle="short" timeStyle="short" type="date" pattern="${datePattern}" />
</c:set>

<h2 class="head">${sTitle}</h2>
<div class="box">
	<ul class="data-list">
		<li class="row">
			<ul class="data cf">
				<li class="entry"><spring:message code="checkout.summary.openOrder.detailsBox.orderClosingDate" /></li>
				<li class="entry open-order-closing-date-value" data-selected-closing-date="${selectedClosingDateFormatted}">
					${selectedClosingDateFormatted}
				</li>
			</ul>
		</li>
		<li class="row">
			<ul class="data cf">
				<li class="entry">
					<spring:message code="checkout.summary.openOrder.detailsBox.openForAllContacts" />
				</li>
				<li class="entry is-editable-by-all-value" 
					data-boolean="${isEditableByAllOption}"
					data-boolean-true='<spring:message code="checkout.summary.openOrder.detailsBox.openForAllContacts.boolean.true" />'
					data-boolean-false='<spring:message code="checkout.summary.openOrder.detailsBox.openForAllContacts.boolean.false" />'
				>
					<c:choose>
						<c:when test="${isEditableByAllOption}">
							<spring:message code="checkout.summary.openOrder.detailsBox.openForAllContacts.boolean.true" />
						</c:when>
						<c:otherwise>
							<spring:message code="checkout.summary.openOrder.detailsBox.openForAllContacts.boolean.false" />
						</c:otherwise>
					</c:choose>
				</li>
			</ul> 
		</li>
	</ul> 
	<c:if test="${isOrderEditable}">
		<a href="#" class="btn btn-secondary btn-change" data-order-code="${orderCode}"><i></i> ${sButtonChange}</a>
	</c:if>
	
	
</div>

<mod:lightbox-open-order cartData="${cartData}" orderData="${orderData}" />
