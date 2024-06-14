<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>


<c:choose>
	<c:when test="${(cartData.freeFreightValue.value eq null)}">
		<div class="freight-box">
			<div class="freight-box__image">
				<i class="fas fa-shipping-fast"></i>
			</div>
			<div class="freight-box__content">
				<h3><spring:message code="freight-cost.title" /></h3>
				<p><spring:message code="freight-cost.text" arguments="${cartData.freeFreightValue.formattedValue}" argumentSeparator=";"/></p>
			</div>
		</div>
	</c:when>
	<c:when test="${(cartData.freeFreightValue.value gt 0)}">
		<div class="freight-box">
			<div class="freight-box__image">
				<i class="fas fa-shipping-fast"></i>
			</div>
			<div class="freight-box__content">
				<h3><spring:message code="freight-cost.title" /></h3>
				<p><spring:message code="freight-cost.text" arguments="${cartData.freeFreightValue.formattedValue}" argumentSeparator=";"/></p>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<div class="freight-box">
			<div class="freight-box__image freight-box__image--free">
				<i class="fas fa-shipping-fast"></i>
			</div>
			<div class="freight-box__content">
				<h3><spring:message code="freight-cost.title" /></h3>
				<p><spring:message code="freight-cost.text.over" /></p>
			</div>
		</div>
	</c:otherwise>
</c:choose>
