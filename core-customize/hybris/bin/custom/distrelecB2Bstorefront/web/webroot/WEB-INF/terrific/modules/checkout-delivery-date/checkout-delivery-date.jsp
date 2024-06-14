<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<spring:message code="text.store.dateformat.datepicker.selection" var="sDateFormat" />
<spring:theme code="checkout.deliveryPage.selectDate" text="Select date" var="sDeliveryDatePlaceholder" />

<c:url value="/checkout/detail/changeShippingDate" var="changeShippingDateUrl" />

<fmt:formatDate var="sDateMin" value="${minRequestedDeliveryDate}" type="date" dateStyle="short" pattern="${sDateFormat}" />
<fmt:formatDate var="sDateMax" value="${maxRequestedDeliveryDate}" type="date" dateStyle="short" pattern="${sDateFormat}" />
<fmt:formatDate var="deliveryDate" value="${cartData.reqDeliveryDateHeaderLevel}" dateStyle="short" pattern="${sDateFormat}" />

<div class="title">
	<h2><spring:message code="checkout.deliveryPage.scheduledTitle" /></h2>
	<p><spring:message code="checkout.deliveryPage.scheduledText" /></p>
</div>
<form:form action="${changeShippingDateUrl}" method="post" class="form notranslate">
	<label for="filter_date"><spring:message code="listfilter.date" /></label>
	<i class="fas fa-calendar-alt">&nbsp;</i>
	<input id="filter_date" type="text" name="requestedDeliveryDateHeaderLevel" placeholder="${sDeliveryDatePlaceholder}" value="${deliveryDate}" autocomplete="off" class="field js-date-picker notranslate" data-delivery-date="${deliveryDate}" data-format="${sDateFormat}" data-date-min="${sDateMin}" data-date-max="${sDateMax}" />
	<input id="format_date" type="hidden" name="requestedDeliveryDateFormat" value="${sDateFormat}">
</form:form>

