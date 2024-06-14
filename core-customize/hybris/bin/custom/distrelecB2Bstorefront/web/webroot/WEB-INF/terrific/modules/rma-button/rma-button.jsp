<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>

<spring:message code="rma.success.printLabel" var="sPrintLabel" />

<a href="" class="mat-button mat-button--action-red">
    <i class="fa fa-print" aria-hidden="true"></i><span>${sPrintLabel}</span>
</a>

<mod:lightbox-return-shipping-label/>