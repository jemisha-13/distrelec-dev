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
<%@ tag description="Module: Analytics - Templates: default" %>
<%@ attribute name="googleAdWordsConversionTrackingId" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="googleAdWordsConversionId" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="googleAdWordsConversionLabel" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="googleAnalyticsTrackingId" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="intelliAdTrackingId" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="affilinetConversionTrackingSiteId" type="java.lang.String" rtexprvalue="true" %>

<%@ attribute name="breadcrumbs" description="The actual breadcrumb" type="java.util.List" rtexprvalue="true" %>
<%@ attribute name="pageType" description="The actual breadcrumb" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="cartData" description="The current cart of the user" type="de.hybris.platform.commercefacades.order.data.CartData" required="false" %>
<%@ attribute name="orderData" description="The order of the user" type="de.hybris.platform.commercefacades.order.data.OrderData" required="false" %>
<%@ attribute name="product" description="The Product" type="de.hybris.platform.commercefacades.product.data.ProductData" rtexprvalue="true" %>

<%-- Module template selection --%>
<terrific:mod name="analytics" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<c:choose>
		<c:when test="${template == 'google-adwords'}">
			<%@ include file="/WEB-INF/terrific/modules/zzz/20-analytics/analytics-google-adwords.jsp" %>
		</c:when>
		<c:when test="${template == 'google-adwords-conversion-tracking'}">
			<%@ include file="/WEB-INF/terrific/modules/zzz/20-analytics/analytics-google-adwords-conversion-tracking.jsp" %>
		</c:when>
		<c:when test="${template == 'google-analytics'}">
			<%@ include file="/WEB-INF/terrific/modules/zzz/20-analytics/analytics-google-analytics.jsp" %>
		</c:when>
		<c:when test="${template == 'google-analytics-ecommerce-tracking'}">
			<%@ include file="/WEB-INF/terrific/modules/zzz/20-analytics/analytics-google-analytics-ecommerce-tracking.jsp" %>
		</c:when>
		<c:when test="${template == 'intelliad-conversion-tracking'}">
			<%@ include file="/WEB-INF/terrific/modules/zzz/20-analytics/analytics-intelliad-conversion-tracking.jsp" %>
		</c:when>
		<c:when test="${template == 'intelliad-newsletter'}">
			<%@ include file="/WEB-INF/terrific/modules/zzz/20-analytics/analytics-intelliad-newsletter.jsp" %>
		</c:when>
		<c:when test="${template == 'intelliad-registration'}">
			<%@ include file="/WEB-INF/terrific/modules/zzz/20-analytics/analytics-intelliad-registration.jsp" %>
		</c:when>
		<c:when test="${template == 'intelliad-cart'}">
			<%@ include file="/WEB-INF/terrific/modules/zzz/20-analytics/analytics-intelliad-cart.jsp" %>
		</c:when>
		<c:when test="${template == 'affilinet-conversion-tracking'}">
			<%@ include file="/WEB-INF/terrific/modules/zzz/20-analytics/analytics-affilinet-conversion-tracking.jsp" %>
		</c:when>
		<c:otherwise>
			<%@ include file="/WEB-INF/terrific/modules/zzz/20-analytics/analytics.jsp" %>
		</c:otherwise>
	</c:choose>	
</terrific:mod>
