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
<%@ attribute name="itemData" type="java.lang.Object" %>
<%@ attribute name="position" type="java.lang.Integer" %>
<%@ attribute name="displayPromotionText" type="java.lang.Boolean" %>
<%@ attribute name="showLogo" type="java.lang.Boolean" required="false" %>
<%@ attribute name="hidePrice" type="java.lang.Boolean" required="false" %>
<%@ attribute name="cardTitle" type="java.lang.String" required="false" %>


<%-- Specific module settings --%>
<%@ tag description="Module: product-card-item - Templates: default" %>


<%-- Module template selection --%>
<terrific:mod name="product-card-item" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<%@ include file="/WEB-INF/terrific/modules/product-card-item/product-card-item.jsp" %>
</terrific:mod>
