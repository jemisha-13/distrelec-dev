<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>

<spring:message code="rma.success.returnItems"  var="sReturnItemsTitle" />
<spring:message code="rma.success.returnText" var="sReturnItemsText" />
<spring:message code="rma.success.printText" var="sPrintLabelText" />
<spring:message code="rma.success.printTextSecond" arguments="${rmaNumber}" var="sPrintLabelTextSecond" />
<spring:message code="rma.success.requireAssistance" var="sRequireAssistance" arguments="${sContactLink}, ${orderId}" />

<h2>${sReturnItemsTitle}</h2>
<p>${sReturnItemsText}</p>
<div class="print-holder">
    <mod:rma-button attributes='data-aainteraction="print shipping label",data-aaorder-id="${orderId}",data-aarma-id="${rmaNumber}",data-aalocation="return request created"' />
    <p>${sPrintLabelText}</p>
    <p class="rma-address">
        <b class="rma-address-text">
            ${rmaReturnAddress}
        </b>
    </p>
    <p>${sPrintLabelTextSecond}</p>
    <c:if test="${shouldProvideReturnAssistance}">
        <p><b>${sRequireAssistance}</b></p>
    </c:if>
</div>