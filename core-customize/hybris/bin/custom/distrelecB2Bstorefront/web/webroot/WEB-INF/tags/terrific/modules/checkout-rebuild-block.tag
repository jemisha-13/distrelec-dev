<%-- Common module settings --%>
<%@ attribute name="tag" description="The tag name used for the module context (default=div)" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="htmlClasses" description="The HTML class values to add to the Terrific module element" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="blockHtmlClasses" description="The HTML class values to add to the Terrific module element" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="skin" description="The skin used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="dataConnectors" description="The data-connectors used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="attributes" description="Additional attributes as comma-separated list, e.g. id='123',role='banner'" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="blockAttributes" description="Additional attributes as comma-separated list, e.g. id='123',role='banner'" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="template" description="The template used for rendering of the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="blockTitleKey" type="java.lang.String" %>
<%@ attribute name="blockTitleClass" type="java.lang.String" %>
<%@ attribute name="cartData" required="false" type="de.hybris.platform.commercefacades.order.data.CartData" %>

<%-- Required tag libraries --%>
<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%-- Specific module settings --%>
<%@ tag description="Module: checkout-rebuild-block - Templates: default" %>

<spring:message code="text.store.dateformat.datepicker.selection" var="sDateFormat" />

<fmt:formatDate var="sDateMin" value="${minRequestedDeliveryDate}" type="date" dateStyle="short" pattern="${sDateFormat}" />
<fmt:formatDate var="sDateMax" value="${maxRequestedDeliveryDate}" type="date" dateStyle="short" pattern="${sDateFormat}" />
<fmt:formatDate var="deliveryDate" value="${cartData.reqDeliveryDateHeaderLevel}" dateStyle="short" pattern="${sDateFormat}" />

<c:set var="totalProducts" value="${fn:length(cartData.entries)}" />

<c:choose>
	<c:when test="${isGuestCheckout}">
		<c:set var="customerType" value="GUEST" />
	</c:when>
	<c:when test="${isEShopGroup}">
		<c:set var="customerType" value="B2E" />
	</c:when>
	<c:otherwise>
		<c:set var="customerType" value="${user.customerType}" />
	</c:otherwise>
</c:choose>

<%-- Module template selection --%>
<terrific:mod name="checkout-rebuild-block" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<div class="mod-checkout-rebuild-block__wrapper">
		<c:if test="${not empty blockTitleKey}">
			<h3 id="${template}_BlockTitle" class="mod-checkout-rebuild-block__title js-rebuild-block-title ${blockTitleClass}"><spring:message code="${blockTitleKey}"/></h3>
		</c:if>

		<div class="mod-checkout-rebuild-block__content js-rebuild-block-content">
			<c:choose>
				<%-- Delivery page --%>
				<c:when test="${template eq 'delivery-options'}">
					<%@ include file="/WEB-INF/terrific/modules/checkout-rebuild/checkout-rebuild-delivery-options.jsp" %>
				</c:when>
				<c:when test="${template eq 'billing-details'}">
					<%@ include file="/WEB-INF/terrific/modules/checkout-rebuild/checkout-rebuild-billing-details.jsp" %>
				</c:when>
				<c:when test="${template eq 'delivery-details'}">
					<%@ include file="/WEB-INF/terrific/modules/checkout-rebuild/checkout-rebuild-delivery-details.jsp" %>
				</c:when>
				<c:when test="${template eq 'order-summary' or template eq 'review-pay-order-summary'}">
					<%@ include file="/WEB-INF/terrific/modules/checkout-rebuild/checkout-rebuild-order-summary.jsp" %>
				</c:when>
				<c:when test="${template eq 'pickup-location'}">
					<%@ include file="/WEB-INF/terrific/modules/checkout-rebuild/checkout-rebuild-pickup-location.jsp" %>
				</c:when>

				<%-- Review and pay page --%>
				<c:when test="${template eq 'review-pay-info'}">
					<%@ include file="/WEB-INF/terrific/modules/checkout-rebuild/checkout-rebuild-review-pay-info.jsp" %>
				</c:when>
				<c:when test="${template eq 'review-pay-codice'}">
					<%@ include file="/WEB-INF/terrific/modules/checkout-rebuild/checkout-rebuild-review-pay-codice.jsp" %>
				</c:when>
				<c:when test="${template eq 'review-pay-method'}">
					<%@ include file="/WEB-INF/terrific/modules/checkout-rebuild/checkout-rebuild-review-pay-method.jsp" %>
				</c:when>
			</c:choose>

			<c:if test="${template eq 'delivery-options' or template eq 'billing-details' or template eq 'delivery-details' or template eq 'review-pay-method'}">
				<mod:loading-spinner spinnerID="spinner_${template}"/>
			</c:if>
		</div>
	</div>
</terrific:mod>
