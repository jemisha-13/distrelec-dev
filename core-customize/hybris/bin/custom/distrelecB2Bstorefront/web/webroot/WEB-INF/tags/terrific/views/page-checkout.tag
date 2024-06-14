<%@ tag body-content="scriptless" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="header" tagdir="/WEB-INF/tags/desktop/common/header" %>
<%@ taglib prefix="footer" tagdir="/WEB-INF/tags/desktop/common/footer" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="namics" uri="/WEB-INF/tld/namicscommercetags.tld"%> 
<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<%@ attribute name="pageTitle" required="false" rtexprvalue="true"%>
<%@ attribute name="bodyCSSClass" required="false" rtexprvalue="true"%>

<views:document-default pageTitle="${pageTitle}" bodyCSSClass="${bodyCSSClass}">
	<header id="header" class="header md-system-rebuild js-sticky-sidebar-fixed-header">
		<div class="container">
			<div class="row align-items-center">
				<div id="checkoutHeaderLogo" class="col-md-3 header__logo">
					<mod:logo skin="checkout"/>
				</div>
				<div class="col-md-6 header__progressbar">
					<mod:checkout-progressbar processSteps="${processSteps}"/>
				</div>
				<div class="col-md-3 header__info text-md-right hidden-md-down">
					<div class="header__secure-info">
						<strong class="fw-b"><i class="fa fa-lock"></i>&nbsp;<span id="checkoutSecureCheckoutLabel"><spring:message code="header.checkout.secure"/></span></strong>
					</div>
				</div>
			</div>
		</div>
	</header>

	<main class="mod-layout--checkout__main md-system-rebuild">
		<jsp:doBody />
	</main>

	<footer class="footer checkout md-system-rebuild" id="footer">
		<cms:slot var="distFooterComponent" contentSlot="${slots.FooterLinks}">
			<cms:component component="${distFooterComponent}" />
		</cms:slot>
	</footer>

	<script id="tmpl-global-messages" type="text/x-template-dotjs">
		<div class="mod mod-banner-ux">
			{{~it.messages :item}}
			<div class="ux-banner is-alert-banner is-center is-{{= item.type}} js-global-messages-item" style="display: none;">
				<div class="ux-banner__text">
					{{? item.message}}
					<p id="jsGlobalErrorMessage">{{= item.message}}</p>
					{{?}}
					<button class="is-close js-global-messages-close" type="button"><i class="material-icons-round" style="opacity: 1;">&#xe5cd;</i></button>
				</div>
			</div>
			{{~}}
		</div>
	</script>
</views:document-default>
