<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oscache" uri="http://www.opensymphony.com/oscache" %>
<%@ taglib prefix="nam" uri="/WEB-INF/tld/namicscommercetags.tld" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"  %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<spring:message code="metahd.account.my" text="My Account" var="sMyAccount" />
<spring:message code="metahd.account.myAccount" text="Your Account" var="sYourAccount" />
<spring:message code="metahd.account.account-details" text="Account Details" var="sAccountDetails" />
<spring:message code="metahd.account.order-manager" text="Order Manager" var="sOrderManager" />
<spring:message code="metahd.account.invoice-manager" text="Invoice Manager" var="sInvoiceManager" />
<spring:message code="metahd.account.quotation-manager" text="Quote Manager" var="sQuoteManager" />
<spring:theme code="metahd.lists.favorites-shopping" text="Favorites & Shopping" var="sFavoritesShopping" />
<spring:theme code="metahd.lists.import-tool" text="Import Tool" var="sImportTool" />
<spring:theme code="metahd.lists.favorite-products" text="My Favorite Products" var="sFavoriteProducts" />
<spring:message code="text.viewAllManufacturers" text="View All Manufacturers" var="sManufacturers" />

<views:page-default-md-full pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-wide skin-layout-sitemap">
<!-- All Categories Section Start-->

	<div id="breadcrumb" class="breadcrumb">
		<mod:breadcrumb template="product" skin="product" />
	</div>

	<div class="md-system container sitemap-page">

		<mod:global-messages />

			<div class="sitemap-page__left-content">

				<ul class="cat-list">

					<c:if test="${not empty categories}">
						<c:forEach items="${categories}" var="l1_cat">

							<li class="sitemap-page__category-holder">
								<a href="${l1_cat.url}" class="sitemap-page__category">${l1_cat.name}</a>
								<c:forEach items="${l1_cat.children}" var="l2_cat">
									<a href="${l2_cat.url}" class="sitemap-page__sub-category">${l2_cat.name}</a>
								</c:forEach>
							</li>

						</c:forEach>
					</c:if>

				</ul>

			</div>

			<div class="sitemap-page__right-content">
				<!-- MY Account Section -->
				<div class="sitemap-page__category-holder sitemap-page__category-holder--block ">
					<ul>
						<li>
							<c:url value="/my-account" var="accountDetailUrl" />
							<a href="${accountDetailUrl}" class="sitemap-page__category"><b>${sMyAccount}</b></a>
						</li>
						<li>
							<c:url value="/my-account" var="accountDetailUrl" />
							<a href="${accountDetailUrl}" class="sitemap-page__sub-category">${sAccountDetails}</a>
						</li>
						<c:if test="${not empty user.unit.erpCustomerId && (shippingOptionsEditable || paymentOptionsEditable)}">
							<li>
								<c:url value="/my-account/payment-and-delivery-options" var="settingsUrl" />
								<a href="${settingsUrl}" class="sitemap-page__sub-category">${sSettings}</a>
							</li>
						</c:if>
						<li>
							<c:url value="/my-account/order-history" var="orderManagerUrl" />
							<a href="${appReqCount > 0 ? appReqUrl : orderManagerUrl}" class="sitemap-page__sub-category">
									${sOrderManager}
								<c:if test="${appReqCount > 0}">
									<span class="badge"><i>${appReqCount}</i></span>
								</c:if>
							</a>
						</li>
						<li>
							<c:url value="/my-account/invoice-history" var="invoiceManagerUrl" />
							<a href="${invoiceManagerUrl}" class="sitemap-page__sub-category">${sInvoiceManager}</a>
						</li>
						<li>
							<a href="${quoteUrl}" class="sitemap-page__sub-category">
									${sQuoteManager}
								<c:if test="${quoteCount > 0}">
									<span class="badge"><i>${quoteCount}</i></span>
								</c:if>
							</a>
						</li>
					</ul>
					<ul <sec:authorize access="hasRole('ROLE_CUSTOMERGROUP')"> -signed-in</sec:authorize>>
						<li>
							<a href="/compare" class="sitemap-page__sub-category"><spring:message code="metahd.compare.list" /> </a>
						</li>
						<sec:authorize access="!hasRole('ROLE_B2BEESHOPGROUP')">
							<sec:authorize access="!hasRole('ROLE_EPROCUREMENTGROUP')">
								<li>
									<a href="/shopping/favorite" class="sitemap-page__sub-category">${sFavoriteProducts}</a>
								</li>
								<li>
									<a href="/shopping" class="sitemap-page__sub-category"><spring:message code="metahd.lists.shopping-list.loggedout" /></a>
								</li>
							</sec:authorize>
						</sec:authorize>
						<li class="import"> <a href="/bom-tool" class="sitemap-page__sub-category">${sImportTool}</a> </li>
					</ul>
					<!-- MY Account Section End-->
				</div>

				<div class="sitemap-page__category-holder sitemap-page__category-holder--block ">
					<!-- Other CMS Pages Section Start-->
					<div class="common-component">
						<cms:slot var="feature" contentSlot="${slots['RightSideNavNodes']}">
							<cms:component component="${feature}" />
						</cms:slot>

						
							<c:url value="/manufacturer-stores/cms/manufacturer" var="manufacturerUrl" />
							<a href="${manufacturerUrl}" class="sitemap-page__category"><b>${sManufacturers}</b></a>
						
						<br>
						<div class="sitemap-page__right-side-cms-links">

							<cms:slot var="feature" contentSlot="${slots['RightSideCMSLinks']}">
								<cms:component component="${feature}" />
								<br>
							</cms:slot>
						</div>

					</div>
					<!-- Other CMS Pages Section End-->
				</div>


			</div>

	</div>

</views:page-default-md-full>



