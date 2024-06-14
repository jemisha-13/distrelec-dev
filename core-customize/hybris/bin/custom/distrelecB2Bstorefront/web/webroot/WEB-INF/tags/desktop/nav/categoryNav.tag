<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="pageData" required="true" type="com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData" %>
<%@ attribute name="categoryPageData" type="com.namics.distrelec.b2b.facades.category.data.DistCategoryPageData" %>
<%--
 Tag to display the category navigation.
 If the first facet is the cateogry facet, then only that facet is shown. Otherwise the full facet nav is shown.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>

<c:choose>
	<c:when test="${showCategoriesOnly}">
		<nav:categoryNavLinks categories="${categoryPageData.subCategories}"/>
	</c:when>

	<c:otherwise>
		<nav:facetNavAppliedFilters pageData="${pageData}"/>
		<nav:facetNavRefinements pageData="${pageData}"/>
	</c:otherwise>
</c:choose>
