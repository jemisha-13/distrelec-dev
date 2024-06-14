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
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ attribute name="carouselData" type="java.util.List" required="false" %>
<%@ attribute name="title" type="java.lang.String" required="false" %>
<%@ attribute name="showLogo" type="java.lang.Boolean" required="false" %>
<%@ attribute name="displayPromotionText" type="java.lang.Boolean" required="false" %>
<%@ attribute name="maxNumberToDisplay" type="java.lang.Integer" required="true" %>
<%@ attribute name="productReferences" type="java.util.ArrayList" required="false" %>
<%@ attribute name="detailPageShowMorePostfix" type="java.lang.String" required="false" %>

<%-- Specific module settings --%>
<%@ tag description="Module: product-card - Templates: default" %>

<c:set var="isOCI" value="false" />
<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
	<c:set var="isOCI" value="true" />
</sec:authorize>

<%-- Module template selection --%>
<terrific:mod name="product-card" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<c:choose>
		<c:when test="${isOCI eq false}">
			<c:choose>
				<c:when test="${template == 'product-recommendations' && searchExperience == 'factfinder'}">
					<%@ include file="/WEB-INF/terrific/modules/product-card/product-card-product-recommendations.jsp" %>
				</c:when>
				<c:when test="${template == 'product-recommendations-cart'}">
					<%@ include file="/WEB-INF/terrific/modules/product-card/product-card-product-recommendations-cart.jsp" %>
				</c:when>
				<c:when test="${template == 'product-alsobought' && searchExperience == 'factfinder'}">
					<%@ include file="/WEB-INF/terrific/modules/product-card/product-card-product-alsobought.jsp" %>
				</c:when>
				<c:when test="${template == 'replacement'}">
					<%@ include file="/WEB-INF/terrific/modules/product-card/product-card-replacement.jsp" %>
				</c:when>
				<c:when test="${template == 'product-accessories'}">
					<%@ include file="/WEB-INF/terrific/modules/product-card/product-card-product-accessories.jsp" %>
				</c:when>
				<c:when test="${template == 'product-consistentwith'}">
					<%@ include file="/WEB-INF/terrific/modules/product-card/product-card-product-consistentwith.jsp" %>
				</c:when>
				<c:when test="${template == 'product-alternatives'}">
					<%@ include file="/WEB-INF/terrific/modules/product-card/product-card-product-alternatives.jsp" %>
				</c:when>
				<c:when test="${template == 'product-carousel'}">
					<%@ include file="/WEB-INF/terrific/modules/product-card/product-card-product-carousel.jsp" %>
				</c:when>
				<c:otherwise>
					<%@ include file="/WEB-INF/terrific/modules/product-card/product-card.jsp" %>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<%@ include file="/WEB-INF/terrific/modules/OCI/product-card/product-card-product-carousel.jsp" %>
		</c:otherwise>
	</c:choose>
</terrific:mod>
