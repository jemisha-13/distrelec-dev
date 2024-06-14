<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:useBean id="now" class="java.util.Date" />
<fmt:formatDate var="currentYear" value="${now}" pattern="yyyy" />

<spring:message code="footer.checkout.paymentSecured" var="sPaymentSecured"/>
<spring:message code="footer.checkout.helpMessage" arguments="/cms/contact" var="sHelpMessage"/>
<spring:message code="footer.checkout.copyright" var="sCopyright" arguments="${currentYear}"/>

<div class="footer__background footer__main-content-OCI">
    <div class="ct">
        <p>
            <strong><i class="fa fa-lock">&nbsp;</i> ${sPaymentSecured}</strong>
        </p>
        <p>${sHelpMessage}. ${sCopyright}</p>
        <p>
            <i class="fab fa-cc-visa">&nbsp;</i>
            <i class="fab fa-cc-mastercard">&nbsp;</i>
        </p>
    </div>
</div>
