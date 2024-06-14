<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="breadcrumb"
	tagdir="/WEB-INF/tags/desktop/nav/breadcrumb"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<spring:theme code="register.successTitle"
	text="Thank you for creating a Distrelec account!" var="sSuccessTitle" />

<spring:theme code="register.successTitle"
	text="Thank you for creating a Distrelec account!" var="sSuccessTitle" />
<spring:theme code="register.successShopUrl" text="/"
	var="sSuccessShopUrl" />
<spring:theme code="register.successThankYouText.1" text="Thanks."
	var="sSuccessThankYouText1" />
<spring:theme code="register.successConfirmationEmailText"
	text="A confirmation mail has been sent."
	var="sSuccessConfirmationEmailText" arguments="${registeredEmail}" />
<spring:theme code="register.successThankYouText.approvalByCSNeeded"
	text="Thanks." var="sSuccessThankYouTextApprovalByCSNeeded" />
<spring:theme code="register.successBackToShopLabel" text="Back to Shop"
	var="sSuccessBackToShopLabel" />

<views:page-checkout pageTitle="${pageTitle}"
	bodyCSSClass="mod mod-layout skin-layout-checkout skin-layout-login">
	<mod:checkout-progressbar processSteps="${processSteps}" />
	<mod:global-messages />
	<h1 class="base page-title">${sSuccessTitle}</h1>
	<div class="row">
		<div class="gu-12">
			<div class="row">
				<div class="gu-12">
					<h1 class="base page-title">${sSuccessTitle}</h1>
				</div>
			</div>

			<div class="border-top border-bottom base">
				<div class="inner row form-box">
					<div class="gu-10 base">
						<c:choose>
							<c:when test="${approvalByCustomerServiceNeeded}">
								<p class="small">${sSuccessThankYouTextApprovalByCSNeeded}</p>
							</c:when>
							<c:otherwise>
								<p class="small">${sSuccessThankYouText1}</p>
								<p class="small">${sSuccessConfirmationEmailText}</p>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
			</div>
		</div>
		<div class="gu-4 padding-left">
			<mod:cart-pricecalcbox skin="sticky" cartData="${cartData}"
				showTitle="true" />
		</div>
	</div>
</views:page-checkout>
