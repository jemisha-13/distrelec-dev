<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="row accordion-title">
	<h2 class="base padding-left"><spring:message code="product.accordion.more.information" /></h2>
	<button class="btn-expand btn-expand-close"><i></i><span><spring:message code="product.accordion.close.all" /></span></button>
	<button class="btn-expand btn-expand-show"><i></i><span><spring:message code="product.accordion.show.all" /></span></button>
</div>

<%-- TECHNICAL, open on page load --%>
<c:if test="${not empty product.classifications}">
	<div class="row detailaccordion">
		<a class="anchor" name="technical"></a>
		<div class="detailaccordion-header">
			<i></i>
			<h3><spring:message code="product.accordion.technical.attributes" /></h3>
		</div>
		<div class="detailaccordion-content">
			<div class="inner">
				<mod:detail-accordion-content template="technical-information" skin="technical-information" product="${product}"/>
			</div>
		</div>
	</div>
</c:if>

<%-- INFORMATION --%>
<%-- Service Plus Products never have product information, DISTRELEC-3243 --%>
<c:if test="${not isServicePlusProduct}">
	<div class="row detailaccordion">
		<a class="anchor" name="information"></a>
		<div class="detailaccordion-header">
			<i></i>
			<h3><spring:message code="product.accordion.product.information" /></h3>
		</div>
		<div class="detailaccordion-content">
			<div class="inner">
				<div class="row">
					<mod:detail-accordion-content template="product-information" skin="product-information" product="${product}" />
				</div>
			</div>
		</div>
	</div>
</c:if>

<%-- DOWNLOADS --%>
<c:if test="${not empty product.downloads}">
	<div class="row detailaccordion">
		<a class="anchor" name="download"></a>
		<div class="detailaccordion-header">
			<i></i>
			<h3><spring:message code="product.accordion.download" /></h3>
		</div>
		<div class="detailaccordion-content">
			<div class="inner">
				<div class="row">
					<ul>
						<c:forEach items="${product.downloads}" var="downloadSection" end="5">
							<mod:detail-accordion-content template="download" skin="download" tag="li" downloadSection="${downloadSection}"/>
						</c:forEach>
					</ul>
				</div>
				<c:if test="${fn:length(product.downloads) gt 6}">
					<div class="row show-more"><a class="show-more-link btn-show-more" href="/"><span><spring:message code="product.accordion.show.more" /></span></a></div>
				</c:if>
			</div>
		</div>
	</div>
</c:if>

<%-- SIMILAR, open on page load --%>
<c:if test="${not empty product.productReferences.dist_similar}">
	<div class="row detailaccordion">
		<a class="anchor" name="similar"></a>
		<div class="detailaccordion-header">
			<i></i>
			<h3><spring:message code="product.accordion.similar.products" /><span class="title-suffix"><spring:message code="product.accordion.similar.productsSuffix" /></span></h3>
		</div>
		<div class="detailaccordion-content">
			<div class="inner">
				<mod:productlist template="similar-detail-page" skin="similar-detail-page" productReferences="${product.productReferences.dist_similar}" showProductFamilyButton="true" 
					htmlClasses="similar-products" />
			</div>
		</div>
	</div>
</c:if>

<%-- ACCESSORIES --%>
<c:if test="${not empty product.productReferences.dist_accessory}">
	<div class="row detailaccordion detailaccordion-last">
		<a class="anchor" name="accessories"></a>
		<div class="detailaccordion-header">
			<i></i>
			<h3><spring:message code="product.accordion.accessories" /></h3>
		</div>
		<div class="detailaccordion-content">
			<div class="inner">
				<mod:productlist template="accessories-detail-page" skin="accessories-detail-page" productReferences="${product.productReferences.dist_accessory}" 
					htmlClasses="product-accessories"/>
			</div>
		</div>
	</div>
</c:if>
