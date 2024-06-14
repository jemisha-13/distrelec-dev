<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>

<spring:message code="checkoutordersummaryinfobox.paymentMethod.title" var="sTitle"/>
<spring:message code="checkoutordersummaryinfobox.buttonChange" var="sButtonChange"/>

<c:set var="paymentMode" value="${cartData != null ? cartData.paymentMode : orderData.paymentMode}"/>
<c:set var="paymentCost" value="${cartData != null ? cartData.paymentCost : orderData.paymentCost}"/>
<c:set var="cardNumber" value="${cartData != null ? cartData.paymentInfo.cardNumber : orderData.paymentInfo.cardNumber}"/>
<c:set var="cardSaved" value="${cartData != null ? cartData.paymentInfo.saved : orderData.paymentInfo.saved}"/>

<c:if test="${empty cardNumber || !cardSaved}">
	<c:set var="cardNumber" value="New" />
</c:if>

<c:if test="${cartData.b2bCustomerData.budget.exceededYearlyBudget gt 0}">
	<c:set var="yearlyBudgetExceeds" value="${true}" />
</c:if>

<c:if test="${cartData.b2bCustomerData.budget.exceededOrderBudget gt 0}">
	<c:set var="orderBudgetExceeds" value="${true}" />
</c:if>

<c:set var="paymentModeName">
	<spring:message code="${paymentMode.translationKey}" text="${paymentMode.name}" />
</c:set>

<div class="title">
	<h4>${sTitle}:</h4>
	<c:if test="${currentCountry.isocode ne 'EX'}">
		<c:if test="${yearlyBudgetExceeds or orderBudgetExceeds ne 'true'}">
			<span class="title__edit">
				<a href="/checkout/address">${sButtonChange}</a>
			</span>
		</c:if>
	</c:if>
</div>
<c:choose>
	<c:when test="${paymentModeName ne 'Credit Card'}">
		<div class="method">
			<p>${paymentModeName}</p>
		</div>
	</c:when>
	<c:otherwise>
		<div class="method">
			<p>${paymentModeName} : ${cardNumber}</p>
		</div>
	</c:otherwise>
</c:choose>

