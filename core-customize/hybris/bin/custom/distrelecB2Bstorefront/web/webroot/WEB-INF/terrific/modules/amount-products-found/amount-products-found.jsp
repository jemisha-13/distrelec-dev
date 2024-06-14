<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<spring:theme code="search.page.totalResults.singular" text="Product found" var="sProductsFoundLabelSingular"/>
<spring:theme code="search.page.totalResults" text="Products found" var="sProductsFoundLabelPlural"/>
<spring:theme code="search.page.totalResultsCat.singular" text="Product found" var="sProductsFoundLabelSingularCat"/>
<spring:theme code="search.page.totalResultsCat" text="Products found" var="sProductsFoundLabelPluralCat"/>
<spring:theme code="search.page.viewAllProducts" text="View all Products" var="sProductsFoundViewAll"/>

<c:choose>
	<c:when test="${not empty searchPageData}">
		<c:choose>
			<c:when test="${searchPageData.pagination.totalNumberOfResults > 1}">
				<c:set var="sProductsFoundLabel" value="${sProductsFoundLabelPlural}" />
			</c:when>
			<c:otherwise>
				<c:set var="sProductsFoundLabel" value="${sProductsFoundLabelSingular}" />
			</c:otherwise>
		</c:choose>
		<c:if test="${searchPageData.freeTextSearch eq '*'}">
			<c:set var="sProductsFoundLabel" value="${sProductsFoundLabelPluralCat}" />
		</c:if>
		<span class="count" id="productsCount" data-text-singular="${sProductsFoundLabelSingular}" data-text-plural="${sProductsFoundLabelPlural}" >
			<span class="nr"><fmt:formatNumber type="number" value="${searchPageData.pagination.totalNumberOfResults}" /></span> 
			<span class="text">${sProductsFoundLabel}</span>
			
			<c:if test="${searchPageData.freeTextSearch != '*'}">
				<span class="text-red">"${searchPageData.freeTextSearch}"</span>
			</c:if>
			
			<span class="service-plus${searchPageData.pagination.totalNumberOfCatalogPlusResults == 0 ? ' hidden' : ''}">
				<fmt:formatNumber type="number" value="${searchPageData.pagination.totalNumberOfCatalogPlusResults}" var="totalNumberOfCatalogPlusResults" />
					<spring:theme code="search.page.totalServicePlusResults" arguments="${totalNumberOfCatalogPlusResults},${searchPageData.pagination.catalogPlusQueryString}" />
			</span>

			<span class="view-all">
				<a href="#select-productlist-orderSelectBoxItContainer">${sProductsFoundViewAll}</a>
			</span>
		</span>
	</c:when>
	<c:when test="${not empty productsCount}">
		<c:choose>
			<c:when test="${productsCount > 1}">
				<spring:theme code="search.page.totalResults" text="Products found" var="sProductsFoundLabel"/>
			</c:when>
			<c:otherwise>
				<spring:theme code="search.page.totalResults.singular" text="Product found" var="sProductsFoundLabel"/>	
			</c:otherwise>
		</c:choose>
		<span class="count"><span class="nr"><fmt:formatNumber type="number" value="${productsCount}" /></span> ${sProductsFoundLabel}</span>
	</c:when>
</c:choose>