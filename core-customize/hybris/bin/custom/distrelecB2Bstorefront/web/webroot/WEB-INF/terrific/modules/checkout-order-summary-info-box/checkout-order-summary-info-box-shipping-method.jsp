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

<h2 class="head">${sTitle}</h2>
<div class="box">
	<ul class="data-list">
		<li class="row-shipping-method">
			<ul class="data cf">
				<li class="entry">${deliveryModeName}:</li>
				<li class="entry shipping-cost">
					<c:choose>
						<c:when test="${empty deliveryCost.value || namicscommerce:isZero(deliveryCost.value)}">
							<spring:message code="checkoutdeliveryoptionslist.placeholder" />
						</c:when>
						<c:otherwise>
							<format:price format="defaultSplit" explicitMaxFractionDigits="2" priceData="${deliveryCost}" />
						</c:otherwise>
					</c:choose>
				</li>
				<c:if test="${showChangeButton}">
					<li class="action">
						<a href="/checkout/detail" class="btn btn-edit-new"><i></i> </a>
					</li>
				</c:if>
			</ul>
		</li>
	</ul>
</div>