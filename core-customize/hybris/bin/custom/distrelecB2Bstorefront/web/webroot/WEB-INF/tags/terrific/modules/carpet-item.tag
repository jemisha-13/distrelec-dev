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

<%-- Required backend data --%>
<%@ attribute name="productItemData" type="de.hybris.platform.commercefacades.product.data.ProductData" %>
<%@ attribute name="teaserItemData"  type="com.namics.distrelec.b2b.facades.cms.data.DistCarpetContentTeaserData" %>
<%@ attribute name="position" type="java.lang.Integer" %>


<%-- Specific module settings --%>
<%@ tag description="Module: Carpet-Item - Templates: default" %>

<%-- Module template selection --%>
<terrific:mod name="carpet-item" tag="article" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<c:choose>
		<c:when test="${template == 'product'}">
			<%@ include file="/WEB-INF/terrific/modules/carpet-item/carpet-item-product.jsp" %>
		</c:when>
		<c:when test="${template == 'product-large'}">
			<%@ include file="/WEB-INF/terrific/modules/carpet-item/carpet-item-product-large.jsp" %>
		</c:when>
		<c:when test="${template == 'teaser'}">
				<%@ include file="/WEB-INF/terrific/modules/carpet-item/carpet-item-teaser.jsp" %>
		</c:when>
		<c:when test="${template == 'teaser-large'}">
			<%@ include file="/WEB-INF/terrific/modules/carpet-item/carpet-item-teaser-large.jsp" %>
		</c:when>
	</c:choose>
</terrific:mod>
