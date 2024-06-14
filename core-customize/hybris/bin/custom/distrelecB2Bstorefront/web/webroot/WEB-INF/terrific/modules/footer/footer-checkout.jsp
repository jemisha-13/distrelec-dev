<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate var="currentYear" value="${now}" pattern="yyyy" />

<spring:message code="footer.checkout.paymentSecured" var="sPaymentSecured" />
<spring:message code="footer.checkout.helpMessage" arguments="/cms/contact" var="sHelpMessage" />
<spring:message code="footer.checkout.copyright" var="sCopyright" arguments="${currentYear}"/>

<div class="cr-footer">
	<div class="container">
		<div class="row align-items-center">
			<div class="col-md-6">
				<c:if test="${not empty footerComponentData.paymentMethods}">
					<mod:footer-payment-methods htmlClasses="col-md payment-methods" paymentMethods="${footerComponentData.paymentMethods}" template="checkout" />
				</c:if>
			</div>

			<div class="col-md-6">
				<div class="cr-footer__secure">
					<i class="fa fa-lock"></i><strong id="checkoutFooterSecureCheckoutLabel" class="fw-b">${sPaymentSecured}</strong>
				</div>
			</div>
		</div>

		<div class="row">
			<div class="col-12 text-center">
				<div id="checkoutFooterCopyright" class="cr-footer__copy">${sHelpMessage}.&nbsp;${sCopyright}</div>
			</div>
		</div>
	</div>
</div>
