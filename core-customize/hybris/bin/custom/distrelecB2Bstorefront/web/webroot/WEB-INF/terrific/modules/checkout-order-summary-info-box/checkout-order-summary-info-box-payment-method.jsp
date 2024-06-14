<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>

<spring:message code="checkoutordersummaryinfobox.paymentMethod.title" var="sTitle"/>
<spring:message code="checkoutordersummaryinfobox.buttonChange" var="sButtonChange"/>
<spring:message code="checkoutordersummaryinfobox.paymentMethod.percentage" var="sPercentageOfCart"/>

<c:set var="paymentMode" value="${cartData != null ? cartData.paymentMode : orderData.paymentMode}"/>
<c:set var="paymentCost" value="${cartData != null ? cartData.paymentCost : orderData.paymentCost}"/>

<c:set var="paymentModeName">
	<spring:message code="${paymentMode.translationKey}" text="${paymentMode.name}" />
</c:set>

<h2 class="head">${sTitle}</h2>
<div class="box">
	<ul class="data-list">
		<li class="row">
			<ul class="data cf">
				<li class="entry">
					${paymentModeName}
				</li>
				<li class="entry payment-cost">
					<%-- Payment cost absolut --%>
					<c:choose>
						<c:when test="${empty paymentCost.value || namicscommerce:isZero(paymentCost.value)}">
							<spring:message code="checkoutpaymentoptionslist.placeholder" />
						</c:when>
						<c:otherwise>
							<format:price format="defaultSplit" explicitMaxFractionDigits="2" priceData="${paymentCost}" />
						</c:otherwise>
					</c:choose>
				</li>
				<li class="entry payment-percentage">
					<%-- Payment cost  in percentage, only available if payment type is credit card --%>
					<c:choose>
						<c:when test="${empty paymentMode.paymentCost.value || namicscommerce:isZero(paymentMode.paymentCost.value)}">
							&nbsp;
						</c:when>
						<c:otherwise>
							${paymentMode.paymentCost.formattedValue}&nbsp;${sPercentageOfCart}
						</c:otherwise>
					</c:choose>
				</li>
				<c:if test="${not isEShopGroup and showChangeButton}">
					<li class="action">
						<a href="/checkout/detail/#choose-payment" class="btn btn-secondary btn-change"><i></i> ${sButtonChange}</a>
					</li>
				</c:if>
			</ul>
		</li>
	</ul>
</div>