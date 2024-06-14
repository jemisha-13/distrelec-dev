<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%-- set isOCI var --%>
<c:set var="isOCI" value="false" />
<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
	<c:set var="isOCI" value="true" />
</sec:authorize>

<spring:theme code="product.tabs.relatedproducts" text="Back" var="sRelatedProducts" />
<spring:theme code="product.eol.alternative" text="Alternative" var="sAlternativeProduct" />

<c:set var="statusCode" value="${product.salesStatus}" />

<views:page-default-md-full pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-product skin-layout-wide skin-layout-nonavigation isOCI-${isOCI}">

	<section class="breadcrumb-section">
		<div id="breadcrumb" class="breadcrumb">
			<mod:breadcrumb template="product" skin="product" />
		</div>
	</section>

	<mod:schema template="product" />

	<section class="product-section">
		<div class="container">
			<input class="isNewSalesStatusEnabled hidden" value="true"/>
			<div class="product-section__wrapper">
				<div class="content-holder">

					<mod:campaign skin="product-detail-page" template="product-detail-page" htmlClasses="hidden" productCode="${product.code}"  />
					<div class="campaign-top-product-detail-page"></div>

					<div class="row">

						<c:choose>
							<c:when test="${not empty propositionalComponent}">
								<cms:component component="${propositionalComponent}"/>
							</c:when>
							<c:otherwise>
								<cms:slot var="brandProposition" contentSlot="${slots.Proposition}">
									<div class="col-12 brand-proposition">
										<cms:component component="${brandProposition}"/>
									</div>
								</cms:slot>
							</c:otherwise>
						</c:choose>

						<c:url value="/cms/contact" var="contactUrl"/>
						<spring:message code="stock.contact.sales" var="contactText"/>
						<mod:global-messages type="error" skin=" col-12 error hidden js-contactSalesMessage"  headline='' body='<a class="link" href="${contactUrl}">${contactText}</a>'/>

						<div class="col-12 print-product-name">

							<spring:message code="price.match.email" var="priceMatchEmail"/>
							<spring:message code="price.match.email.subject" var="priceMatchEmailSubject"/>
							<spring:message code="price.match.email.body" var="priceMatchEmailBody"/>

							<mod:product-title title="${productTitle}" />
							<mod:article-numbers distManufacturer="${product.distManufacturer}" product="${product}" showExternalLink="false" showLink="true" />

							<c:choose>
								<c:when test="${product.catPlusItem}">
									<div class="service-plus-wrap">
										<mod:catplus-logo catplusLogoAltText="catplus logo" catplusLogoUrl="/Web/WebShopImages/catplus/catplus-logo.gif" />
									</div>
								</c:when>
								<c:otherwise>
									<div class="manufacturer-wrap">
										<mod:product-manufacturer distManufacturer="${product.distManufacturer}" showExternalLink="false" showLink="true" />
									</div>
								</c:otherwise>
							</c:choose>
						</div>

						<div class="col-12 d-print-none">
							<div class="msg-section">
								<c:if test="${not empty message}">
									<spring:theme code="${message}" />
								</c:if>
								<c:choose>
									<c:when test="${empty eolMessage}">
										<mod:global-messages />
									</c:when>
									<c:otherwise>
										<mod:global-messages headline="" body="${eolMessage}" type="information" widthPercent="100%" displayIcon="true" />
									</c:otherwise>
								</c:choose>
							</div>
						</div>

						<div class="col-12 product-status-section d-print-none">
							<spring:eval expression="@configurationService.configuration.getString('distrelec.noproduct.forsale.salestatus')" var="notforsales" scope="request" />

									<c:if test="${fn:contains(notforsales, statusCode) || statusCode eq 20 || statusCode eq 21 || statusCode eq 91 || statusCode eq 61 || statusCode eq 62 || statusCode eq 53
							 || statusCode eq 50 || statusCode eq 51 || statusCode eq 52}">
										<mod:product-status-box product="${product}" statusCode="${statusCode}" replacementProductList="${replacementProductList}" replacementProductLink="${replacementProductLink}" />
									</c:if>

							<c:if test="${!currentSalesOrg.calibrationInfoDeactivated && product.calibrationService && not empty product.calibrationItemArtNo}">
								<c:set var="bannerTemplate" value="${product.calibrated ? 'calibrated' : 'uncalibrated'}" />
								<mod:cal-plus-banner template="${bannerTemplate}" itemArtNo="${product.calibrationItemArtNo}" />
							</c:if>

						</div>

						<div class="col-12 col-lg-6 ">
							<mod:product-image-gallery product="${product}" galleryImages="${galleryImages}" />
							<mod:detailpage-toolbar htmlClasses="d-print-none" product="${product}" />
						</div>
						<div class="col-12 col-lg-6">
							<div class="row">
								<div class="col-12">
									<div class="product-right-info js-check-if-out-of-stock-scope">
										<div class="product-right-info__top">
											<mod:shipping-information product="${product}" template="pdp" skin="pdp" />
										</div>

										<div class="product-right-info__bottom">
											<mod:scaled-prices product="${product}" skin="single" template="single" />
											<div class="d-print-none">
												<mod:buying-section product="${product}" />
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="row">
								<div id="product-alternatives-list-carousel" class="col-12">
									<mod:product-card
										template="product-carousel"
										skin="product-carousel hidden"
										maxNumberToDisplay="4"
										detailPageShowMorePostfix="/alternatives"
									/>
								</div>
							</div>
						</div>
					</div>
					<mod:lightbox-image productName="${product.name}" productCalibrationService="${product.calibrationService}"/>
					<mod:lightbox-quotation />
					<mod:lightbox-quotation-confirmation />
					<mod:detail-tabs product="${product}" downloadSection="${downloadSection}" />
				</div>
			</div>
		</div>
	</section>

	<c:if test="${isOCI == false}">
		<div class="recommendations d-print-none">
			<div class="container">

				<div class="row">

					<div id="product-alternatives-list" class="col-12" >

						<mod:product-card
								template="product-alternatives"
								skin="product-alternatives product-alternatives-list hidden"
								maxNumberToDisplay="4"
								detailPageShowMorePostfix="/alternatives"
						/>

					</div>

				</div>

				<c:if test="${hasAccessories}">
					<div class="tab-pane section-info accessories ${product.productReferences.dist_accessory}" id="info-tab_accessoires">
						<div class="inner">

							<mod:product-card
									template="product-accessories"
									skin="product-accessories"
									productReferences="${product.productReferences.dist_accessory}"
									maxNumberToDisplay="4"
									detailPageShowMorePostfix="/accessories"
							/>

						</div>
					</div>
				</c:if>

				<br/>
				<cms:slot var="comp" contentSlot="${slots.AlsoBought}">
					<cms:component component="${comp}" />
				</cms:slot>

				<div class="bottom-consistentWith">
					<cms:slot var="comp" contentSlot="${slots.ConsistentWith}">
						<cms:component component="${comp}" />
					</cms:slot>
				</div>

				<div class="bottom-consistentWith" id="reevoo_tabbed">
					<c:if test="${isReevooActivatedForWebshop && product.eligibleForReevoo}">	
						<reevoo-product-reviews product-id="${product.code}" per-page="5"></reevoo-product-reviews>
					</c:if>
				</div>

			</div>
		</div>

		<div class="campaign-bottom-product-detail-page d-print-none"></div>
	</c:if>
	<mod:print-footer />
</views:page-default-md-full>
