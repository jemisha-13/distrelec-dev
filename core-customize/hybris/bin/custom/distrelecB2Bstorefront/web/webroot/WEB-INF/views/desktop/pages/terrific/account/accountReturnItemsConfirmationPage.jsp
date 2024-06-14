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

<views:page-default pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-account-detail skin-layout-wide skin-layout-wide">
	<div id="breadcrumb" class="breadcrumb">
		<mod:breadcrumb />
	</div>
	<mod:global-messages />
	<div class="ct">
		<div class="row">
			<%-- DISTRELEC-9432 --%>
			<%-- mod:checkout-progressbar template="return-items" processSteps="${processSteps}" /--%>
			<h1 class="base page-title"><spring:message code="cart.return.items.confirmation.title" /></h1>
			<spring:message code="cart.return.items.confirmation.text1" /> </br>
			<spring:message code="cart.return.items.confirmation.text2" text="Voucher" arguments="${rmaCode}" />   </br>
			<spring:message code="cart.return.items.confirmation.text3" /> </br>
			<spring:message code="cart.return.items.confirmation.text4" /> </br>
		<div class="row return-items-buttons return-items-buttons-confirmation">
			<div class="gu-4 ">
				<a href="/my-account/order-history/" class="btn btn-primary btn-change"><i></i><spring:message code="cart.return.items.confirmation.return" /> </br></a>
			</div>
		</div>		
		</div>
	</div>
	<mod:print-footer/>
</views:page-default>
