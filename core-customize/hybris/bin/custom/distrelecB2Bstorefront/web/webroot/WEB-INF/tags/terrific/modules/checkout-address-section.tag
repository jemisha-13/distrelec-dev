<%-- Common module settings --%>
<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="tag" description="The tag name used for the module context (default=div)" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="htmlClasses" description="The HTML class values to add to the Terrific module element" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="skin" description="The skin used for the module" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="dataConnectors" description="The data-connectors used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="attributes" description="Additional attributes as comma-separated list, e.g. id='123',role='banner'" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="template" description="The template used for rendering of the module" type="java.lang.String" rtexprvalue="true" %>

<%-- Required tag libraries --%>
<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- Specific module settings --%>
<%@ tag description="Module: checkout-address-singlesdit - Templates: default" %>
<%@ attribute name="cartData" type="de.hybris.platform.commercefacades.order.data.CartData" rtexprvalue="true" %>
<%@ attribute name="billingAddresses" type="java.util.List" required="false" %>
<%@ attribute name="shippingAddresses" type="java.util.List" required="false" %>
<%@ attribute name="pickupWarehouses" type="java.util.List" required="false" %>
<%@ attribute name="deliveryType" type="java.lang.Integer" required="false" %>
<%@ attribute name="selectedAddressId" type="java.lang.String" required="false" %>
<%@ attribute name="isEShopGroup" type="java.lang.Boolean" required="false" %>
<%@ attribute name="page" type="java.lang.String" required="false" %>
<%@ attribute name="shippingMode" type="java.lang.String" required="false" %>

<%-- Module template selection --%>
<terrific:mod name="checkout-address-section" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
    <c:choose>
        <c:when test="${template == 'billing'}">
            <%@ include file="/WEB-INF/terrific/modules/checkout-address-section/checkout-address-section-billing.jsp" %>
        </c:when>
        <c:when test="${template == 'billing-multiple'}">
            <%@ include file="/WEB-INF/terrific/modules/checkout-address-section/checkout-address-section-billing-multiple.jsp" %>
        </c:when>
        <c:when test="${template == 'billing-limited'}">
            <%@ include file="/WEB-INF/terrific/modules/checkout-address-section/checkout-address-section-billing-limited.jsp" %>
        </c:when>
        <c:when test="${template == 'shipping'}">
            <%@ include file="/WEB-INF/terrific/modules/checkout-address-section/checkout-address-section-shipping.jsp" %>
        </c:when>
        <c:when test="${template == 'pickup'}">
            <%@ include file="/WEB-INF/terrific/modules/checkout-address-section/checkout-address-section-pickup.jsp" %>
        </c:when>
        <c:otherwise>
            <%@ include file="/WEB-INF/terrific/modules/checkout-address-section/checkout-address-section.jsp" %>
        </c:otherwise>
    </c:choose>
</terrific:mod>


