<%-- Common module settings --%>
<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="tag" description="The tag name used for the module context (default=div)" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="htmlClasses" description="The HTML class values to add to the Terrific module element" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="skin" description="The skin used for the module" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="dataConnectors" description="The data-connectors used for the module" type="java.lang.String" rtexprvalue="false" %>
<%@ attribute name="attributes" description="Additional attributes as comma-separated list, e.g. id='123',role='banner'" type="java.lang.String" rtexprvalue="true" %>
<%@ attribute name="template" description="The template used for rendering of the module" type="java.lang.String" rtexprvalue="true" %>

<%-- Required tag libraries --%>
<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%--Module specific attributes --%>
<%@ attribute name="product" required="false" type="de.hybris.platform.commercefacades.product.data.ProductData" %>
<%@ attribute name="matchingProduct" required="false" type="com.namics.distrelec.b2b.facades.importtool.data.ImportToolMatchingData" %>
<%@ attribute name="wishlistEntry" required="false" type="com.namics.distrelec.b2b.facades.wishlist.data.NamicsWishlistEntryData" %>
<%@ attribute name="productCounter" required="false" type="java.lang.String" %>
<%@ attribute name="shoppinglistId" required="false" type="java.lang.String" %>
<%@ attribute name="listViewType" required="false" type="java.lang.String" %>
<%@ attribute name="position" type="java.lang.Integer" %>
<%@ attribute name="producttype" required="false" type="java.lang.String" %>
<%@ attribute name="carriedReference" required="false" type="java.lang.String" %>
<%@ attribute name="carriedQuantity" required="false" type="java.lang.String" %>

<c:set var="isOCI" value="false" />
<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
	<c:set var="isOCI" value="true" />
</sec:authorize>

<%-- Module template selection --%>
<terrific:mod name="product" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
	<c:choose>
		<c:when test="${isOCI eq false}">
			<c:choose>
				<c:when test="${template == 'shopping'}">
					<%@ include file="/WEB-INF/terrific/modules/product/product-shopping.jsp" %>
				</c:when>
				<c:when test="${template == 'favorite'}">
					<%@ include file="/WEB-INF/terrific/modules/product/product-favorite.jsp" %>
				</c:when>
				<c:when test="${template == 'detail-page'}">
					<%@ include file="/WEB-INF/terrific/modules/product/product-detail-page.jsp" %>
				</c:when>
				<c:when test="${template == 'bom'}">
					<%@ include file="/WEB-INF/terrific/modules/product/product-bom.jsp" %>
				</c:when>
				<c:when test="${template == 'bom-controllbar'}">
					<%@ include file="/WEB-INF/terrific/modules/product/product-bom-controllbar.jsp" %>
				</c:when>
				<c:when test="${template == 'compare-list'}">
					<%@ include file="/WEB-INF/terrific/modules/product/product-compare-list.jsp" %>
				</c:when>
				<c:when test="${template == 'compare-list-eol'}">
					<%@ include file="/WEB-INF/terrific/modules/product/product-compare-list-eol.jsp" %>
				</c:when>
				<c:when test="${template == 'dot-tpl'}">
					<%@ include file="/WEB-INF/terrific/modules/product/product-dot-tpl.jsp" %>
				</c:when>
				<c:when test="${template == 'technical-dot-tpl'}">
					<%@ include file="/WEB-INF/terrific/modules/product/product-technical-dot-tpl.jsp" %>
				</c:when>
				<c:when test="${template == 'technical'}">
					<%@ include file="/WEB-INF/terrific/modules/product/product-technical.jsp" %>
				</c:when>
				<c:when test="${template == 'family'}">
					<%@ include file="/WEB-INF/terrific/modules/product/product-family.jsp" %>
				</c:when>
				<c:when test="${template == 'compare-head'}">
					<%@ include file="/WEB-INF/terrific/modules/product/product-compare-head.jsp" %>
				</c:when>
				<c:otherwise>
					<%@ include file="/WEB-INF/terrific/modules/product/product.jsp" %>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<%@ include file="/WEB-INF/terrific/modules/OCI/product/product-family.jsp" %>
		</c:otherwise>
	</c:choose>
</terrific:mod>
