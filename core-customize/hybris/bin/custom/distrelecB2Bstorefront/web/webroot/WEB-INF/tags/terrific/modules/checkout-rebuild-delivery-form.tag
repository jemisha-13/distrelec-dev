<%-- Common module settings --%>
<%@ attribute name="tag" description="The tag name used for the module context (default=div)" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="htmlClasses" description="The HTML class values to add to the Terrific module element" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="skin" description="The skin used for the module" type="java.lang.String" %>
<%@ attribute name="dataConnectors" description="The data-connectors used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="attributes" description="Additional attributes as comma-separated list, e.g. id='123',role='banner'" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="template" description="The template used for rendering of the module" type="java.lang.String" %>
<%@ attribute name="shippingItem" type="de.hybris.platform.commercefacades.user.data.AddressData" %>

<%-- Required tag libraries --%>
<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%-- Specific module settings --%>
<%@ tag description="Module: checkout-rebuild-billing-form - Templates: default" %>

<%-- Module template selection --%>
<terrific:mod name="checkout-rebuild-delivery-form" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<c:choose>
		<%-- FORMS --%>
		<c:when test="${template eq 'B2B' || template eq 'B2B_KEY_ACCOUNT'}">
			<%@ include file="/WEB-INF/terrific/modules/checkout-rebuild/checkout-rebuild-delivery-details/checkout-rebuild-delivery-form-b2b.jsp" %>
		</c:when>
		<c:when test="${template eq 'B2C'}">
			<%@ include file="/WEB-INF/terrific/modules/checkout-rebuild/checkout-rebuild-delivery-details/checkout-rebuild-delivery-form-b2c.jsp" %>
		</c:when>
	</c:choose>
</terrific:mod>
