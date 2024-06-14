<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="tag" description="The tag name used for the module context (default=div)" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="htmlClasses" description="The HTML class values to add to the Terrific module element" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="skin" description="The skin used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="dataConnectors" description="The data-connectors used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="attributes" description="Additional attributes as comma-separated list, e.g. id='123',role='banner'" type="java.lang.String" rtexprvalue="false" %>

<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="address" type="de.hybris.platform.commercefacades.user.data.AddressData" required="true" %>
<%@ attribute name="addressIndex" type="java.lang.String" required="false" %>

<%@ tag description="Module: lightbox-checkout-shipping-edit - Templates: default" %>

<terrific:mod name="lightbox-checkout-shipping-edit" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<%@ include file="/WEB-INF/terrific/modules/lightbox-checkout-shipping-edit/lightbox-checkout-shipping-edit.jsp" %>
</terrific:mod>
