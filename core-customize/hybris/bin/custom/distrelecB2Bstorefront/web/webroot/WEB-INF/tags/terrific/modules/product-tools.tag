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
<%@ tag description="Module: product-tools - Templates: default" %>
<%@ attribute name="productId" description="The Id of the product" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="positionIndex" description="The index of the product lisiting count" type="java.lang.String" required="false" %>
<%@ attribute name="productOrderQuantityMinimum" description="The minimum order of the product" type="java.lang.String" required="false" %>

<%-- Module template selection --%>
<terrific:mod name="product-tools" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<c:choose>
		<c:when test="${template == 'favorite'}">
			<%@ include file="/WEB-INF/terrific/modules/product-tools/product-tools-favorite.jsp" %>
		</c:when>
		<c:when test="${template == 'favorite-eol'}">
			<%@ include file="/WEB-INF/terrific/modules/product-tools/product-tools-favorite-eol.jsp" %>
		</c:when>
		<c:when test="${template == 'compare-list'}">
			<%@ include file="/WEB-INF/terrific/modules/product-tools/product-tools-compare-list.jsp" %>
		</c:when>
		<c:when test="${template == 'bom'}">
			<%@ include file="/WEB-INF/terrific/modules/product-tools/product-tools-bom.jsp" %>
		</c:when>
		<c:when test="${template == 'tabular'}">
			<%@ include file="/WEB-INF/terrific/modules/product-tools/product-tools-tabular.jsp" %>
		</c:when>
		<c:when test="${template == 'technical'}">
			<%@ include file="/WEB-INF/terrific/modules/product-tools/product-tools-technical.jsp" %>
		</c:when>
		<c:otherwise>
			<%@ include file="/WEB-INF/terrific/modules/product-tools/product-tools.jsp" %>
		</c:otherwise>
	</c:choose>
</terrific:mod>
