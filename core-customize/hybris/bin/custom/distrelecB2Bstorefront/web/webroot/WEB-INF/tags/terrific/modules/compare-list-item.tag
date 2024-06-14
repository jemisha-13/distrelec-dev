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
<%@ tag description="Module: compare-list-item - Templates: default" %>

<%-- Module Specific Attributes --%>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" rtexprvalue="true" %>
<%@ attribute name="isActive" description="If tools item is Active" type="java.lang.Boolean" rtexprvalue="true" %>
<%@ attribute name="position" type="java.lang.Integer" %>

<%-- Module template selection --%>
<terrific:mod name="compare-list-item" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<c:choose>
		<c:when test="${template == 'head'}">	 
			<%@ include file="/WEB-INF/terrific/modules/compare-list-item/compare-list-item-head.jsp" %>
		</c:when>
		<c:otherwise>
			<%@ include file="/WEB-INF/terrific/modules/compare-list-item/compare-list-item.jsp" %>
		</c:otherwise>
	</c:choose>
</terrific:mod>
