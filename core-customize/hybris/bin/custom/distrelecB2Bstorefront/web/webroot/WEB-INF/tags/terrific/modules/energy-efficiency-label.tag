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
<%@ tag description="Module: energy-efficiency-label - Templates: default" %>

<%-- When populated from FF --%>
<%@ attribute name="productCode" description="Product code" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="productEnergyEfficiency" description="Energy efficiency" type="java.lang.String" required="false" rtexprvalue="true" %>
<%@ attribute name="productManufacturer" description="Manufacturer" type="java.lang.String" required="false" rtexprvalue="true" %>
<%@ attribute name="productType" description="Type number" type="java.lang.String" required="false" rtexprvalue="true" %>
<%@ attribute name="productEnergyPower" description="Power in Watt" type="java.lang.String" required="false" rtexprvalue="true" %>
<%@ attribute name="energyTopText" description="Text to display at top of label" type="java.lang.String" required="false" rtexprvalue="true" %>
<%@ attribute name="energyBottomText" description="Text to display at bottom of label" type="java.lang.String" required="false" rtexprvalue="true" %>
<%@ attribute name="energyClassesBuiltInLed" description="Energy classes for LEDs, separated by semicolon" type="java.lang.String" required="false" rtexprvalue="true" %>
<%@ attribute name="energyClassesFitting" description="Energy classes for bulbs, separated by semicolon" type="java.lang.String" required="false" rtexprvalue="true" %>
<%@ attribute name="energyClassesIncludedBulb" description="Energy class for included bulb/LED" type="java.lang.String" required="false" rtexprvalue="true" %>

<%-- When populated from BE --%>
<%@ attribute name="product" description="The Product" type="de.hybris.platform.commercefacades.product.data.ProductData" required="false" rtexprvalue="true" %>

<%-- Module template selection --%>
<terrific:mod name="energy-efficiency-label" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<c:choose>
		<c:when test="${template == 'lamp'}">
			<%@ include file="/WEB-INF/terrific/modules/energy-efficiency-label/energy-efficiency-label-lamp.jsp" %>
		</c:when>
		<c:when test="${template == 'lampsearch'}">
			<%@ include file="/WEB-INF/terrific/modules/energy-efficiency-label/energy-efficiency-label-lamp-search.jsp" %>
		</c:when>
		<c:otherwise>
			<%@ include file="/WEB-INF/terrific/modules/energy-efficiency-label/energy-efficiency-label.jsp" %>
		</c:otherwise>
	</c:choose>
	
</terrific:mod>

