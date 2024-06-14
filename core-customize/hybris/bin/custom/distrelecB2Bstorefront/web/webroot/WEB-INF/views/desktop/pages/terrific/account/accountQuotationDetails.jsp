<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<views:page-default-md-full pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-account-detail skin-layout-account-quotation-detail skin-layout-wide">
	<c:set var="isLoggedIn" value="false" />
	<sec:authorize access="hasRole('ROLE_CUSTOMERGROUP')">
		<c:set var="isLoggedIn" value="true" />
	</sec:authorize>

	<div id="breadcrumb" class="breadcrumb">
		<mod:breadcrumb template="new-account" skin="new-account" />
	</div>
	<div class="quote-detail-content">
		<div class="container">
			<div class="row">
				<div class="col-12">
					<mod:global-messages />
				</div>
				<div class="col-12">
					<h1 class="base page-title">${cmsPage.title}</h1>
					<mod:order-overview-box template="quote"/>
					<mod:cart-list template="quote-detail" skin="quote-detail" />
					<div class="bottom-holder">
						<div class="row">
							<div class="gu-8-1 border-top">
								<mod:cart-toolbar template="quote-detail" skin="quote-detail" orderData="${orderData}" />
							</div>
							<div class="gu-4-1">
								<mod:cart-pricecalcbox template="quote" skin="quote" orderData="${orderData}" showTitle="false"/>
							</div>
						</div>
						<c:if test="${quotationData.purchasable}">
							<div class="row btnAddToCart">
								<mod:quotation-add-to-cart quotationId="${quotationData.quotationId}" />
							</div>
						</c:if>
					</div>
				</div>
			</div>
		</div>
	</div>
	<mod:print-footer/>
	<mod:lightbox-quotation-confirmation template="form" />
</views:page-default-md-full>
