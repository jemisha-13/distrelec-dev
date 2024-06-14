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
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@ attribute name="carouselData" type="java.util.List" required="false" %>
<%@ attribute name="componentWidth" type="java.lang.String" required="true" %>
<%@ attribute name="layout" type="java.lang.String" required="false" %>
<%@ attribute name="title" type="java.lang.String" required="true" %>
<%@ attribute name="autoplay" type="java.lang.Boolean" required="true" %>
<%@ attribute name="autoplayTimeout" type="java.lang.Integer" required="true" %>
<%@ attribute name="autoplayDirection" type="java.lang.String" required="true" %>
<%@ attribute name="displayPromotionText" type="java.lang.Boolean" required="true" %>
<%@ attribute name="maxNumberToDisplay" type="java.lang.Integer" required="true" %>
<%@ attribute name="wtTeaserTrackingId" type="java.lang.String" required="false" %>
<%@ attribute name="showLogo" type="java.lang.Boolean" required="false" %>
<%@ attribute name="isCheckout" type="java.lang.Boolean" required="false" %>

<%-- Specific module settings --%>
<%@ tag description="Module: Carousel Teaser - Templates: product | category | manufacturer" %>
<%-- Module template selection --%>

<c:set var="isOCI" value="false" />
<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
	<c:set var="isOCI" value="true" />
</sec:authorize>

<terrific:mod name="carousel-teaser" tag="aside" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<c:choose>
		<c:when test="${isOCI eq false}">
			<c:choose>
				<c:when test="${template == 'campaign'}">
					<%@ include file="/WEB-INF/terrific/modules/carousel-teaser/carousel-teaser-campaign.jsp" %>
				</c:when>
				<c:when test="${template == 'product-lazy-load'}">
					<%@ include file="/WEB-INF/terrific/modules/carousel-teaser/carousel-teaser-product-lazy-load.jsp" %>
				</c:when>
				<c:when test="${template == 'product-lazy-load-vertical'}">
					<%@ include file="/WEB-INF/terrific/modules/carousel-teaser/carousel-teaser-product-lazy-load-vertical.jsp" %>
				</c:when>
				<c:when test="${template == 'product-lazy-load-accessories'}">
					<%@ include file="/WEB-INF/terrific/modules/carousel-teaser/carousel-teaser-product-lazy-load-accessories.jsp" %>
				</c:when>
				<c:when test="${template == 'product-box-horizontal'}">
					<%@ include file="/WEB-INF/terrific/modules/carousel-teaser/carousel-teaser-product-box-horizontal.jsp" %>
				</c:when>
				<c:when test="${template == 'product-box-vertical'}">
					<%@ include file="/WEB-INF/terrific/modules/carousel-teaser/carousel-teaser-product-box-vertical.jsp" %>
				</c:when>
				<c:otherwise>
					<%@ include file="/WEB-INF/terrific/modules/carousel-teaser/carousel-teaser.jsp" %>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="${template == 'campaign'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/carousel-teaser/carousel-teaser-campaign.jsp" %>
				</c:when>
				<c:when test="${template == 'product-lazy-load'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/carousel-teaser/carousel-teaser-product-lazy-load.jsp" %>
				</c:when>
				<c:when test="${template == 'product-lazy-load-vertical'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/carousel-teaser/carousel-teaser-product-lazy-load-vertical.jsp" %>
				</c:when>
				<c:when test="${template == 'product-lazy-load-accessories'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/carousel-teaser/carousel-teaser-product-lazy-load-accessories.jsp" %>
				</c:when>
				<c:when test="${template == 'product-box-horizontal'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/carousel-teaser/carousel-teaser-product-box-horizontal.jsp" %>
				</c:when>
				<c:when test="${template == 'product-box-vertical'}">
					<%@ include file="/WEB-INF/terrific/modules/OCI/carousel-teaser/carousel-teaser-product-box-vertical.jsp" %>
				</c:when>
				<c:otherwise>
					<%@ include file="/WEB-INF/terrific/modules/OCI/carousel-teaser/carousel-teaser.jsp" %>
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>
</terrific:mod>
