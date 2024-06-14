<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>

<spring:message code="checkoutordersummaryinfobox.shippingMethod.title" var="sTitle"/>
<spring:message code="checkoutordersummaryinfobox.buttonChange" var="sButtonChange"/>

<c:set var="deliveryMode" value="${cartData != null ? cartData.deliveryMode : orderData.deliveryMode}"/>
<c:set var="deliveryCost" value="${cartData != null ? cartData.deliveryCost : orderData.deliveryCost}"/>

<c:set var="deliveryModeName">
	<spring:message code="${deliveryMode.translationKey}" text="${deliveryMode.name}" />
</c:set>

<div class="title">
	<h4>${sTitle}:</h4>
	<c:if test="${!isExportShop}">
		<span class="title__edit">
			<a href="/checkout/detail">${sButtonChange}</a>
		</span>
	</c:if>
</div>
<div class="method">
	<p>
		${deliveryModeName} :

		<c:choose>
			<c:when test="${empty deliveryCost.value || namicscommerce:isZero(deliveryCost.value)}">
				<spring:message code="checkout.summary.deliveryCost.free" />
			</c:when>
			<c:otherwise>
				<format:price format="defaultSplit" explicitMaxFractionDigits="2" priceData="${deliveryCost}" />
			</c:otherwise>
		</c:choose>
	</p>
</div>