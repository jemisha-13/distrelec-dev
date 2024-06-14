<%-- Common module settings --%>
<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="tag" description="The tag name used for the module context (default=div)" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="htmlClasses" description="The HTML class values to add to the Terrific module element" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="skin" description="The skin used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="dataConnectors" description="The data-connectors used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="attributes" description="Additional attributes as comma-separated list, e.g. id='123',role='banner'" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="template" description="The template used for rendering of the module" type="java.lang.String" rtexprvalue="true" %>

<%-- Required tag libraries --%>
<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- Specific module settings --%>
<%@ tag description="Module: checkout-address-continue - Templates: default" %>
<%@ attribute name="deliveryType" type="java.lang.Integer" required="false" %>
<%@ attribute name="billingAddressId" type="java.lang.String" required="false" %>
<%@ attribute name="billingIsShippingAddress" type="java.lang.Boolean" required="false" %>
<%@ attribute name="shippingAddressId" type="java.lang.String" required="false" %>
<%@ attribute name="pickupLocationCode" type="java.lang.String" required="false" %>
<%@ attribute name="isOpenOrder" type="java.lang.String" required="false" %>
<%@ attribute name="openOrderType" type="java.lang.String" required="false" %>
<%@ attribute name="page" type="java.lang.String" required="false" %>
<%@ attribute name="selectedPaymentOptionCode" type="java.lang.String" required="false" %>

<%-- Module template selection --%>
<terrific:mod name="checkout-btn-continue" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<%@ include file="/WEB-INF/terrific/modules/checkout-btn-continue/checkout-btn-continue.jsp" %>
</terrific:mod>


