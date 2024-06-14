<%-- Common module settings --%>
<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="tag" description="The tag name used for the module context (default=div)" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="htmlClasses" description="The HTML class values to add to the Terrific module element" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="skin" description="The skin used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="dataConnectors" description="The data-connectors used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="attributes" description="Additional attributes as comma-separated list, e.g. id='123',role='banner'" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="template" description="The template used for rendering of the module" type="java.lang.String" rtexprvalue="false" %>

<%-- Required tag libraries --%>
<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- Specific module settings --%>
<%@ tag description="Module: checkout-order-summary-info-box - Templates: default" %>

<%-- Paramter --%>
<%@ attribute name="cartData" description="The current cart of the user" type="de.hybris.platform.commercefacades.order.data.CartData" %>
<%@ attribute name="orderData" description="The current order of the user" type="de.hybris.platform.commercefacades.order.data.OrderData" %>
<%@ attribute name="isEShopGroup" description="If the current user is in the EShopGroup" type="java.lang.Boolean" %>
<%@ attribute name="showChangeButton" description="If the Change button should be displayed" type="java.lang.Boolean" %>

<%-- Module template selection --%>
<terrific:mod name="checkout-order-summary-info-box" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<c:choose>
		<c:when test="${template == 'open-order'}">
			<%@ include file="/WEB-INF/terrific/modules/checkout-order-summary-info-box/checkout-order-summary-info-box-open-order.jsp" %>
		</c:when>
		<c:when test="${template == 'discount'}">
			<%@ include file="/WEB-INF/terrific/modules/checkout-order-summary-info-box/checkout-order-summary-info-box-discount.jsp" %>
		</c:when>
		<c:when test="${template == 'payment-method'}">
			<%@ include file="/WEB-INF/terrific/modules/checkout-order-summary-info-box/checkout-order-summary-info-box-payment-method.jsp" %>
		</c:when>
		<c:when test="${template == 'payment-method-new'}">
			<%@ include file="/WEB-INF/terrific/modules/checkout-order-summary-info-box/checkout-order-summary-info-box-payment-method-new.jsp" %>
		</c:when>		
		<c:when test="${template == 'shipping-method'}">
			<%@ include file="/WEB-INF/terrific/modules/checkout-order-summary-info-box/checkout-order-summary-info-box-shipping-method.jsp" %>
		</c:when>
		<c:when test="${template == 'shipping-method-new'}">
			<%@ include file="/WEB-INF/terrific/modules/checkout-order-summary-info-box/checkout-order-summary-info-box-shipping-method-new.jsp" %>
		</c:when>
		<c:when test="${template == 'shipping-date-new'}">
			<%@ include file="/WEB-INF/terrific/modules/checkout-order-summary-info-box/checkout-order-summary-info-box-shipping-date-new.jsp" %>
		</c:when>
		<c:otherwise>
			<%@ include file="/WEB-INF/terrific/modules/checkout-order-summary-info-box/checkout-order-summary-info-box.jsp" %>
		</c:otherwise>
	</c:choose>
</terrific:mod>
