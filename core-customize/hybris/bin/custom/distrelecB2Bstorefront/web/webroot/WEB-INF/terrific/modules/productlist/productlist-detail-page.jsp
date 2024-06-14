<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<c:set var="productFamilyUrl" value="${product.productFamilyUrl}" />

<%-- DISTRELEC-6690 --%> 
<%-- Do not show any items initially, everything is loaded via AJAX --%> 
<c:set var="initialProductsPerPage" value="0" />
<c:set var="productsPerPage" value="10" />
<c:set var="pageSize" value="${productsPerPage}" />


<div class="loading-similars">
	<spring:message code="product.gallery.magic360.loading" />
</div>

<div class="productlist">
	<ul class="list row">
	 <%-- we always need the product templates since they are also used for the list view switch between standard and technical view --%>
		<mod:product tag="li" template="dot-tpl" skin="template" />
		<mod:product tag="li" template="technical-dot-tpl" skin="template" />
	</ul>
</div>
<%-- DISTRELEC-6690 --%>
<%-- Always display "Show more" button; initial content is loaded via simulated click --%>
<div class="row row-show-more">
	<a class="btn btn-down btn-show-more" href="#" data-page-size="${pageSize}" data-action-url="${product.url}" data-ajax-url-postfix="${detailPageShowMorePostfix}">
		<spring:message code="product.list.show.more" /><i></i>
	</a>
</div>
<c:if test="${showProductFamilyButton and not empty productFamilyUrl }">
       <spring:message code="product.family.buttonText" var="sProductFamilyLinkText"/>
       <a class="btn btn-secondary btn-product-family" title="${sProductFamilyLinkText}" href="<c:out value="${productFamilyUrl}" />">${sProductFamilyLinkText}</a>
</c:if>


