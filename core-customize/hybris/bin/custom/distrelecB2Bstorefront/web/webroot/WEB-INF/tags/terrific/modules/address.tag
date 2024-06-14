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
<%@ tag description="Module: address" %>
<%@ attribute name="addressType" type="java.lang.String" required="false" %>
<%@ attribute name="customerType" type="java.lang.String" required="false" %>
<%@ attribute name="address" type="de.hybris.platform.commercefacades.user.data.AddressData" required="false" %>
<%@ attribute name="warehouse" type="com.namics.distrelec.b2b.facades.order.warehouse.data.WarehouseData" required="false" %>
<%-- addressActionMode can be either edit, change, select, radio and determines which address controls (e.g. Change, Edit, Select Button or Radio Button) are displayed --%>
<%@ attribute name="addressActionMode" type="java.lang.String" description="Address Action (Show either radio button if its in a address list or show select button)" required="false" %>
<%@ attribute name="addressEditMode" type="java.lang.String" description="Weather the address can be edited or not" required="false" %>
<%@ attribute name="addressCount" type="java.lang.Integer" description="Address count" required="false" %>

<%@ attribute name="shippingMode" type="java.lang.String" description="shippingMode" required="false" %>
<%@ attribute name="selectedAddressId" type="java.lang.String" required="false" %>
<%@ attribute name="addressIndex" type="java.lang.String" required="false" %>

<%-- Module template selection --%>
<terrific:mod name="address" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
    <c:choose>
        <c:when test="${template == 'billing-b2b'}">
            <%@ include file="/WEB-INF/terrific/modules/address/address-billing-b2b.jsp" %>
        </c:when>
        <c:when test="${template == 'billing-b2b-multiple'}">
            <%@ include file="/WEB-INF/terrific/modules/address/address-billing-b2b-multiple.jsp" %>
        </c:when>
        <c:when test="${template == 'billing-b2b-list'}">
            <%@ include file="/WEB-INF/terrific/modules/address/address-billing-b2b-list.jsp" %>
        </c:when>
        <c:when test="${template == 'billing-b2c'}">
            <%@ include file="/WEB-INF/terrific/modules/address/address-billing-b2c.jsp" %>
        </c:when>
        <c:when test="${template == 'billing-b2b-new'}">
            <%@ include file="/WEB-INF/terrific/modules/address/address-billing-b2b-new.jsp" %>
        </c:when>
        <c:when test="${template == 'billing-b2c-new'}">
            <%@ include file="/WEB-INF/terrific/modules/address/address-billing-b2c-new.jsp" %>
        </c:when>
        <c:when test="${template == 'billing-b2b-myaccount'}">
            <%@ include file="/WEB-INF/terrific/modules/address/address-billing-b2b-myaccount.jsp" %>
        </c:when>
        <c:when test="${template == 'billing-b2c-myaccount'}">
            <%@ include file="/WEB-INF/terrific/modules/address/address-billing-b2c-myaccount.jsp" %>
        </c:when>
        <c:when test="${template == 'shipping-b2b'}">
            <%@ include file="/WEB-INF/terrific/modules/address/address-shipping-b2b.jsp" %>
        </c:when>
        <c:when test="${template == 'shipping-b2c'}">
            <%@ include file="/WEB-INF/terrific/modules/address/address-shipping-b2c.jsp" %>
        </c:when>
        <c:when test="${template == 'shipping-b2b-select'}">
            <%@ include file="/WEB-INF/terrific/modules/address/address-shipping-b2b-select.jsp" %>
        </c:when>
        <c:when test="${template == 'shipping-b2c-select'}">
            <%@ include file="/WEB-INF/terrific/modules/address/address-shipping-b2c-select.jsp" %>
        </c:when>
        <c:when test="${template == 'shipping-b2b-new'}">
            <%@ include file="/WEB-INF/terrific/modules/address/address-shipping-b2b-new.jsp" %>
        </c:when>
        <c:when test="${template == 'shipping-b2c-new'}">
            <%@ include file="/WEB-INF/terrific/modules/address/address-shipping-b2c-new.jsp" %>
        </c:when>        
        <c:when test="${template == 'pickup'}">
            <%@ include file="/WEB-INF/terrific/modules/address/address-pickup.jsp" %>
        </c:when>
        <c:when test="${template == 'pickup-review'}">
            <%@ include file="/WEB-INF/terrific/modules/address/address-pickup-review.jsp" %>
        </c:when>
         <c:when test="${template == 'billing-eshop-b2b'}">
            <%@ include file="/WEB-INF/terrific/modules/address/address-billing-eshop-b2b.jsp" %>
        </c:when>
        <c:otherwise>
            <%@ include file="/WEB-INF/terrific/modules/address/address.jsp" %>
        </c:otherwise>
    </c:choose>
</terrific:mod>


