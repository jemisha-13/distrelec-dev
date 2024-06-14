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
<%@ tag description="Module: category-manager-card - Templates: default" %>
<%@ attribute name="name" type="java.lang.String" required="false" %>
<%@ attribute name="jobTitle" type="java.lang.String" required="false" %>
<%@ attribute name="organisation" type="java.lang.String" required="false" %>
<%@ attribute name="quote" type="java.lang.String" required="false" %>
<%@ attribute name="tipp" type="java.lang.String" required="false" %>
<%@ attribute name="image" type="de.hybris.platform.commercefacades.product.data.ImageData" required="false" %>
<%@ attribute name="ctaText" type="java.lang.String" required="false" %>
<%@ attribute name="ctaLink" type="java.lang.String" required="false" %>
<%@ attribute name="rightFloat" type="java.lang.Boolean" required="false" %>

<%-- Module template --%>
<terrific:mod name="category-manager-card" tag="${tag}" htmlClasses="${htmlClasses} ${rightFloat ? 'float-right' : 'float-left' }" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<%@ include file="/WEB-INF/terrific/modules/category-manager-card/category-manager-card.jsp" %>
</terrific:mod>