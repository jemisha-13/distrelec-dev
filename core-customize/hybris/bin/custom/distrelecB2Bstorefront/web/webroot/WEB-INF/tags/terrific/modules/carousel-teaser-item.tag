<%-- Common module settings --%>
<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="tag" description="The tag name used for the module context (default=div)" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="htmlClasses" description="The HTML class values to add to the Terrific module element" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="skin" description="The skin used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="dataConnectors" description="The data-connectors used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="attributes" description="Additional attributes as comma-separated list, e.g. id='123',role='banner'" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="template" description="The template used for rendering of the module" type="java.lang.String" rtexprvalue="false" %>

<%-- Required tag libraries --%>
<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- Specific module settings --%>
<%@ tag description="Module: Carousel Teaser Item - Templates: category | manufacturer" %>
<%@ attribute name="itemData" type="java.lang.Object" %>
<%@ attribute name="position" type="java.lang.Integer" %>
<%@ attribute name="displayPromotionText" type="java.lang.Boolean" %>
<%@ attribute name="showLogo" type="java.lang.Boolean" required="false" %>
<%@ attribute name="hidePrice" type="java.lang.Boolean" required="false" %>


<%-- Module template selection --%>
<terrific:mod name="carousel-teaser-item" tag="article" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<c:choose>
		<c:when test="${template == 'category'}">
			<%@ include file="/WEB-INF/terrific/modules/carousel-teaser-item/carousel-teaser-item-category.jsp" %>
		</c:when>
		<c:when test="${template == 'manufacturer'}">
			<%@ include file="/WEB-INF/terrific/modules/carousel-teaser-item/carousel-teaser-item-manufacturer.jsp" %>
		</c:when>
		<c:when test="${template == 'product'}">
			<%@ include file="/WEB-INF/terrific/modules/carousel-teaser-item/carousel-teaser-item-product.jsp" %>
		</c:when>
		<c:when test="${template == 'product-box'}">
			<%@ include file="/WEB-INF/terrific/modules/carousel-teaser-item/carousel-teaser-item-product-box.jsp" %>
		</c:when>		
		<c:when test="${template == 'product-dot-tpl'}">
			<%@ include file="/WEB-INF/terrific/modules/carousel-teaser-item/carousel-teaser-item-product-dot-tpl.jsp" %>
		</c:when>
		<c:when test="${template == 'product-dot-tpl-vertical'}">
			<%@ include file="/WEB-INF/terrific/modules/carousel-teaser-item/carousel-teaser-item-product-dot-tpl-vertical.jsp" %>
		</c:when>
		<c:when test="${template == 'product-dot-tpl-vertical-cal'}">
			<%@ include file="/WEB-INF/terrific/modules/carousel-teaser-item/carousel-teaser-item-product-dot-tpl-vertical-cal.jsp" %>
		</c:when>					
		<c:otherwise>
			<%@ include file="/WEB-INF/terrific/modules/carousel-teaser-item/carousel-teaser-item.jsp" %>
		</c:otherwise>
	</c:choose>
</terrific:mod>
