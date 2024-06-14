<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:set var="voucher" value="${cartData.erpVoucherInfoData}"/>
<c:set var="currency">
	<format:price format="currency" priceData="${cartData.subTotal}" fallBackCurrency="${cartData.totalPrice.currencyIso}" />
</c:set>
<c:set var="addVoucherUrl">
	<c:url value="/cart/redeemVoucher/"/>
</c:set>
<c:set var="removeVoucherUrl">
	<c:url value="/cart/resetVoucher"/>
</c:set>

<spring:message code="checkoutvoucherbox.title" var="sTitle"/>
<spring:message code="checkoutvoucherbox.inputLabel" var="sCostCenter"/>

<c:set var="voucherValueFormatted"><format:price format="price" explicitMaxFractionDigits="2" priceData="${voucher.fixedValue}" /></c:set>
<spring:message code="checkoutvoucherbox.voucherDetails" var="sVoucherDetails" arguments="${currency};${voucherValueFormatted}" htmlEscape="false" argumentSeparator=";"/>

<spring:message code="checkoutvoucherbox.addVoucherButton" var="sAddVoucherButton"/>
<spring:message code="checkoutvoucherbox.removeVoucherButton" var="sRemoveVoucherButton"/>

<h2 class="head">${sTitle}</h2>
<form:form action="${not empty voucher and voucher.valid ? removeVoucherUrl : addVoucherUrl}" data-form-type="${(not empty voucher and voucher.valid) ? 'remove' : 'redeem'}" method="post" id="voucher-form" class="mod-checkout-voucher-box__form hidden">
	<div class="form-box">
		<div class="form-box__item">
			<c:choose>
				<c:when test="${not empty voucher and voucher.valid}">
					<span class="voucher-code-text"><c:out value="${voucher.code}" /></span>
				</c:when>
				<c:otherwise>
					<label for="voucher" class="hidden">${sLabel}</label>
					<c:if test="${isVoucherEmpty}">
						<input id="voucher" name="voucher" class="field voucher-code" type="text"  maxlength="10" size="10" value="">
					</c:if>
					<c:if test="${not isVoucherEmpty}">
						<input id="voucher" name="voucher" class="field voucher-code" type="text"   maxlength="10" size="10" placeholder="<spring:message code="checkoutvoucherbox.placeholder" />" value="<c:out value='${voucher.code}' />">
					</c:if>
				</c:otherwise>
			</c:choose>
		</div>
		<div class="form-box__item form-box__item--second">
			<c:choose>
				<c:when test="${not empty voucher and voucher.valid}">
					<a href="#" class="mat-button mat-button--action-blue btn-voucher"> ${sRemoveVoucherButton}</a>
				</c:when>
				<c:otherwise>
					<a href="#" class="mat-button mat-button--action-blue btn-voucher"> ${sAddVoucherButton}</a>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
	<div class="form-box-action">
		<c:if test="${not empty voucher and voucher.valid}">
			<div class="form-box-action__item form-box-action__item--success">
				<div class="icon-holder">
					<i class="fas fa-check-circle"></i>
				</div>
				<div class="text-holder">
					<span class="voucher-details"><c:out value="${sVoucherDetails}" /></span>
				</div>
			</div>
		</c:if>
		<c:if test="${not empty voucher and not voucher.valid}">
			<div class="form-box-action__item form-box-action__item--error">
				<div class="icon-holder">
					<i class="fas fa-times-circle"></i>
				</div>
				<div class="text-holder">
					<span class="voucher-details"><spring:message code="${voucherErrorMessageKey}" text="There was an error with your Voucher" /></span>
				</div>
			</div>
		</c:if>
	</div>
</form:form>
