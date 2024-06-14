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

<%--Module specific attributes --%>
<%@ attribute name="product" required="false" type="de.hybris.platform.commercefacades.product.data.ProductData" %>

<%@ attribute name="bomQty" description="TbomQty" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="shoppingListQty" required="false" type="java.lang.String"%>

<%-- Specific module settings --%>
<%@ tag description="Module: Numeric Stepper - Templates: default" %>

<%-- Module template selection --%>
<terrific:mod name="numeric-stepper" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<c:choose>
		<c:when test="${template == 'product-list-technical'}">
			<%@ include file="/WEB-INF/terrific/modules/numeric-stepper/numeric-stepper-product-list-technical.jsp" %>
		</c:when>
		<c:when test="${template == 'product-list-shopping'}">
			<%@ include file="/WEB-INF/terrific/modules/numeric-stepper/numeric-stepper-product-list-shopping.jsp" %>
		</c:when>
		<c:when test="${template == 'productlist-search-tabular'}">
			<%@ include file="/WEB-INF/terrific/modules/numeric-stepper/numeric-stepper-productlist-search-tabular.jsp" %>
		</c:when>
		<c:otherwise>
			<%@ include file="/WEB-INF/terrific/modules/numeric-stepper/numeric-stepper.jsp" %>
		</c:otherwise>
	</c:choose>
</terrific:mod>
