<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<spring:theme code="login.returntocart" text="Return to cart" var="sCancelText" />

<views:page-checkout pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout mod-layout--checkout">
	<div class="container-fluid body">
		<div class="row">
			<div class="container">
				<div class="row">
					<mod:global-messages htmlClasses="col-12 order-2"/>
					<section class="login order-3 col-12">
						<div class="row">
							<div class="col-12 text-center">
								<h2 id="loginCheckoutPageTitle" class="login__page-title">
									<spring:theme code="login.checkout.page.title" />
								</h2>
							</div>
						</div>

						<div class="row">
							<div class="col-12 col-md-6 col-lg-5 offset-lg-1 login__existing">
								<div class="login__card">
									<c:choose>
										<c:when test="${isTokenInvalid}">
											<mod:login template="checkout" skin="checkout" htmlClasses="js-login-checkout hidden"/>
											<mod:login template="checkout-forgotten-password" skin="checkout-forgotten-password" htmlClasses="js-login-checkout-fp"/>
											<mod:login template="checkout-forgotten-password-success" skin="checkout-forgotten-password-success" htmlClasses="hidden js-login-checkout-fp-success"/>
										</c:when>
										<c:when test="${isResetPassword}">
											<mod:login template="checkout" skin="checkout" htmlClasses="js-login-checkout hidden"/>
											<mod:checkout-reset-password/>
											<mod:login template="checkout-forgotten-password" skin="checkout-forgotten-password" htmlClasses="hidden js-login-checkout-fp"/>
											<mod:login template="checkout-forgotten-password-success" skin="checkout-forgotten-password-success" htmlClasses="hidden"/>
										</c:when>
										<c:otherwise>
											<mod:login template="checkout" skin="checkout" htmlClasses="js-login-checkout"/>
											<mod:login template="checkout-forgotten-password" skin="checkout-forgotten-password" htmlClasses="hidden js-login-checkout-fp"/>
											<mod:login template="checkout-forgotten-password-success" skin="checkout-forgotten-password-success" htmlClasses="hidden js-login-checkout-fp-success"/>
										</c:otherwise>
									</c:choose>
								</div>

								<div class="hidden-md-down">
									<a id="returnToCartLink" class="login__returnlink ux-link" href="/cart"><i class="fa fa-arrow-left"></i>${sCancelText}</a>
								</div>
							</div>
							<div class="col-12 col-md-6 col-lg-5 login__new">
								<div class="login__card">
									<mod:login template="new-customer-info"/>
								</div>


								<c:if test="${isGuestCheckoutEnabled}">
									<mod:login template="guest-checkout" skin="guest-checkout" htmlClasses="js-guest-checkout"/>
								</c:if>
							</div>
						</div>
					</section>
				</div>

				<div class="row">
					<div class="col-12">
						<div class="hidden-md-up">
							<a id="returnToCartLinkMobile" class="login__returnlink ux-link" href="/cart"><i class="fa fa-arrow-left"></i>${sCancelText}</a>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</views:page-checkout>
