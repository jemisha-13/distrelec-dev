<%-- Common module settings --%>
<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="tag" description="The tag name used for the module context (default=div)" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="htmlClasses" description="The HTML class values to add to the Terrific module element" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="skin" description="The skin used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="dataConnectors" description="The data-connectors used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="attributes" description="Additional attributes as comma-separated list, e.g. id='123',role='banner'" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="template" description="The template used for rendering of the module" type="java.lang.String" rtexprvalue="true" %>

<%-- Required tag libraries --%>
<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- Specific module settings --%>
<%@ tag description="Module: address-form - Templates: default" %>

<%@ attribute name="modelAttribute" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="addressType" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="customerType" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="customerChannel" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="addressForm" type="com.namics.distrelec.b2b.storefront.forms.AbstractDistAddressForm" rtexprvalue="true" %>
<%@ attribute name="cancelUrl" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="actionUrl" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="formTitle" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="isBillingAddress" type="java.lang.Boolean" rtexprvalue="true" %>
<%@ attribute name="isShippingAddress" type="java.lang.Boolean" rtexprvalue="true" %>
<%@ attribute name="showDeleteButton" type="java.lang.Boolean" rtexprvalue="true" %>

<%-- Module template selection --%>
<terrific:mod name="address-form" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<c:choose>
        <c:when test="${template == 'b2b'}">
            <%@ include file="/WEB-INF/terrific/modules/address-form/address-form-b2b.jsp" %>
        </c:when>
        <c:when test="${template == 'b2c'}">
            <%@ include file="/WEB-INF/terrific/modules/address-form/address-form-b2c.jsp" %>
        </c:when>
        <c:otherwise>
            <%@ include file="/WEB-INF/terrific/modules/address-form/address-form.jsp" %>
        </c:otherwise>
    </c:choose>
</terrific:mod>