<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="productFamilyUrl" value="${product.productFamilyUrl}" />
<c:set var="escapeProductTitle" value="${fn:escapeXml(productTitle)}" />
<spring:message code="product.family.linkTextExtended" var="sProductFamilyLinkText"/>
<spring:message code="product.tabs.specificationTitle" var="sSpecificationTitle" />
<spring:message code="product.tabs.emailshare" var="sEmailShare" />
<spring:message code="product.tabs.printButton" var="sPrintButton" />
<spring:message code="product.tabs.emailSubject" var="sEmailSubject" />
<spring:message code="product.tabs.emailDescription" var="sEmailDescription" />

<c:choose>
	<c:when test="${hasDownloads}">
		<c:set var="isDownloads" value="col-12 col-lg-8 d-print-none" />
	</c:when>
	<c:otherwise>
		<c:set var="isDownloads" value="col-12 d-print-none" />
	</c:otherwise>
</c:choose>

<div class="container-more-information">
	<div class="tab-content">
		<div class="row">
			<c:if test="${hasDownloads}">
				<div class="col-12 col-lg-4 d-print-none">
					<div class="tab-content__holder  tab-content__holder--downloads" id="info-tab_download">
						<div class="tab-content__header">
							<input class="download-data hidden" value="${product.url}">
							<h2 class="downloads-title">
								<spring:message code="product.tabs.download" />
							</h2>
						</div>
						<div class="inner">
							<div class="row-holder">
								<mod:detail-tabs-content template="download" skin="download" />
							</div>
						</div>
					</div>
				</div>
			</c:if>

				<div class="${isDownloads}">
					<div class="tab-content__holder">
						<div class="specification-holder">
							<c:if test="${not empty productFeature.classifications}">
								<h2 class="spec-title">${sSpecificationTitle}</h2>
								<c:if test="${not empty product.classifications}">
									<mod:detail-tabs-content template="technical-information" skin="technical-information" product="${product}"/>
								</c:if>
							</c:if>
							<div class="content-holder">
								<a data-aainteraction="share by email" data-location="pdp" class="email-share" href="mailto:?subject=${sEmailSubject}&amp;body=${sEmailDescription}&nbsp;${escapeProductTitle}%0D%0A${siteBaseURL}${product.url}">
									<i class="far fa-envelope" aria-hidden="true"></i> ${sEmailShare}
								</a>
								<span class="sep">|</span>
								<a data-aainteraction="print page" data-location="pdp" class="print-page" href="#">
									<i class="fa fa-print" aria-hidden="true"></i> ${sPrintButton}
								</a>
								<mod:error-feedback productID="${product.code}" productName="${product.name}" />
							</div>
						</div>
					</div>
				</div>
			<c:if test="${not empty product.productInformation}">
				<div class="col-12 d-print-flex">
					<div class="tab-content__holder technical" id="info-tab_technical">
						<div class="content-title">
							<h2><spring:message code="product.tabs.technical.attributes" /></h2>
						</div>
						<div class="content-holder">
								<%-- Service Plus Products never have product information, DISTRELEC-3243 --%>
							<c:if test="${not isServicePlusProduct}">
								<mod:detail-tabs-content template="product-information" skin="product-information" product="${product}"  />
							</c:if>
						</div>
					</div>
				</div>

			</c:if>
		</div>
	</div>
</div>
