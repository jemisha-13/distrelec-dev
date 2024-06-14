<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<spring:message code="checkoutordersummaryinfobox.discount.title" var="sTitle"/>
<spring:message code="checkoutordersummaryinfobox.buttonChange" var="sButtonChange"/>


<%-- Currently not used in Movex Scope. Check Code Structure with other templates when discount is going to be a feature --%>

<c:set var="discounts" value="${cartData != null ? cartData.discounts : orderData.discounts}" />

<h2 class="head">${sTitle}</h2>
<div class="box">
	<ul class="data-list">
		<li class="row">
			<ul class="data cf">
				<c:choose>
					<c:when test="${fn:length(discounts) == 0}">
						<li class="entry">
							<spring:message code="checkoutordersummaryinfobox.discount.noDiscount" text="No discount available"/>
						</li>
					</c:when>
					<c:otherwise>
						<c:forEach items="${discounts}" var="discount">
							<li class="entry"> ${discount.discountString} </li>
							<li class="entry"> ${discount.currencyData.isocode}&nbsps;${discount.value}	</li>
							<li class="entry"> ${discount} </li>
							<li class="action">
								<a href="/checkout/detail" class="btn btn-primary btn-change"><i></i> ${sButtonChange}</a>
							</li>
						</c:forEach>
					</c:otherwise>
				</c:choose>
			</ul>
		</li>
	</ul>
</div>