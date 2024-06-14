<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format" %>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld" %>
<%@ taglib prefix="nam" uri="/WEB-INF/tld/namicscommercetags.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<spring:message code="checkoutordersummaryinfobox.shippingDate.title" var="sTitle"/>
<spring:message code="checkoutordersummaryinfobox.buttonChange" var="sButtonChange"/>
<spring:message code="text.store.dateformat.datepicker.selection" var="sDateFormat"/>
<spring:message code="listfilter.date" var="shippingModeName"/>

<c:set var="deliveryMode" value="${cartData != null ? cartData.deliveryMode : orderData.deliveryMode}"/>
<c:set var="deliveryCost" value="${cartData != null ? cartData.deliveryCost : orderData.deliveryCost}"/>
<fmt:formatDate var="sDate" value="${cartData.reqDeliveryDateHeaderLevel}" dateStyle="short" pattern="${sDateFormat}"/>

<c:if test="${not empty sDate}">
    <div class="title">
        <h4>${sTitle}</h4>
        <c:if test="${currentCountry.isocode ne 'EX'}">
            <span class="title__edit">
                <a href="/checkout/detail">${sButtonChange}</a>
            </span>
        </c:if>
    </div>
    <div class="method">
        <p>${shippingModeName} : ${sDate}</p>
    </div>
</c:if>
