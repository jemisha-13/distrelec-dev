<%-- Common module settings --%>
<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="tag" description="The tag name used for the module context (default=div)" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="htmlClasses" description="The HTML class values to add to the Terrific module element" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="skin" description="The skin used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="dataConnectors" description="The data-connectors used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="attributes" description="Additional attributes as comma-separated list, e.g. id='123',role='banner'" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="template" description="The template used for rendering of the module" type="java.lang.String" rtexprvalue="false" %>

<%-- Required tag libraries --%>
<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- Specific module settings --%>
<%@ tag description="Module: checkout-open-order-creation - Templates: default" %>
<%@ attribute name="openOrderDates" type="java.util.List" required="false" %>
<%@ attribute name="existingOpenOrders" type="java.util.Map" required="false" %>

<%@ attribute name="cartData" description="The Session Cart" type="de.hybris.platform.commercefacades.order.data.CartData" required="false" %>

<%-- Module template selection --%>
<terrific:mod name="checkout-open-order-creation" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
    <%@ include file="/WEB-INF/terrific/modules/checkout-open-order-creation/checkout-open-order-creation.jsp" %>
</terrific:mod>


