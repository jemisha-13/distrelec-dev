<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/terrific/user"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/form"%>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>

<c:url value="/my-account/set-payment-info" var="changePaymentUrl" />
<c:url value="/my-account/set-payment-info" var="selectPaymentInfoUrl" />
<c:url value="/my-account/set-payment-info" var="setDefaultUrl" />
<c:url value="/my-account/request-invoice-payment-mode" var="requestInvoicePaymentMode" />

<spring:theme code="checkoutpaymentoptionslist.lightbox.title" var="lightboxTitle" />
<spring:theme code="checkoutpaymentoptionslist.lightbox.message" var="lightboxMessage" />
<spring:theme code="checkoutpaymentoptionslist.lightbox.button" var="lightboxConfirmButtonText" />
<spring:theme code="checkoutpaymentoptionslist.paywith" var="sPaywithLabel" />
<spring:theme code="checkoutpaymentoptionslist.requesttopaywith" var="requestToPayWithLabel"/>
<spring:theme code="checkoutpaymentoptionslist.lightbox.buttonCancel" var="lightboxCancelButtonText" />
<spring:theme code="text.setDefault" var="sSetDefault" />

<div class="title">
	<h2>
		<spring:message code="checkoutpaymentoptionslist.choosePayment" />
	</h2>
</div>

<div class="list">
	<form:form action="${changePaymentUrl}" method="post" id="choose-payment" class="row">
		<c:forEach items="${paymentOptions}" var="paymentOption">
			<c:choose>
				<c:when test="${!paymentOption.creditCardPayment}">
					<div class="col-12 list__item list__item--invoice">
						<input id="paymentOption${paymentOption.code}" type="radio" class="paymentInfo" data-form-method="${paymentOption.code}" ${paymentOption.code eq defaultPaymentMode ? 'checked' : ''} data-form-action="${changePaymentUrl}" />
						<span class="tick">
							<i class="fa fa-check-circle">&nbsp;</i>
						</span>
						<label for="paymentOption${paymentOption.code}">
							<span class="small">${sPaywithLabel}</span>
							<span class="big"><spring:message code="${paymentOption.translationKey}" text="${paymentOption.code}" /> <i class="far fa-file-alt">&nbsp;</i></span>
						</label>
					</div>
				</c:when>
				<c:otherwise>
					<div class="col-12 list__item">
						<input id="paymentOption${paymentOption.code}" type="radio" class="js-option-creditcard paymentInfo" data-form-method="${paymentOption.code}" ${paymentOption.code eq defaultPaymentMode ? 'checked="checked"' : ''} data-form-action="${changePaymentUrl}" />
						<span class="tick">
							<i class="fa fa-check-circle">&nbsp;</i>
						</span>
						<label for="paymentOption${paymentOption.code}">
							<span class="small">${sPaywithLabel}</span>
							<span class="big"><spring:message code="${paymentOption.translationKey}" text="${paymentOption.code}" /> <i class="fa fa-credit-card">&nbsp;</i></span>
						</label>
					</div>
					<div class="col-12 list__saved ">
						<c:forEach items="${ccPaymentInfos}" var="ccPaymentInfo">
						  <c:if test="${ccPaymentInfo.saved}">
							<div class="list__item__option${ccPaymentInfo.isValid eq false ? ' list__item__option--expired' : ''}">
								<c:choose>
									<c:when test="${ccPaymentInfo.isValid}">
										<input type="radio" id="ccPaymentInfo${ccPaymentInfo.id}" name="ccPaymentInfo" class="paymentInfo" value="${ccPaymentInfo.id}" value="${paymentOption.code}"${ccPaymentInfo.id eq defaultPaymentInfo ? 'checked="checked"' : ''} data-form-action="${selectPaymentInfoUrl}" />
										<div class="list__item__option__content">
											<label class="list__item__option__content__title" for="ccPaymentInfo${ccPaymentInfo.id}">
													${ccPaymentInfo.cardTypeData.name}
											</label>
											<dl>
												<dt>
													<span><spring:message code="checkoutpaymentoptionslist.creditCard.number" /></span> : <span>${ccPaymentInfo.cardNumber}</span>
												</dt>
												<dt>
													<span><spring:message code="checkoutpaymentoptionslist.creditCard.expDate" /></span> : <span>${ccPaymentInfo.expiryMonth}/${ccPaymentInfo.expiryYear}</span>
												</dt>
											</dl>
										</div>
									</c:when>
									<c:otherwise>
										<div class="list__item__option__content">
											<label class="list__item__option__content__title" for="ccPaymentInfo${ccPaymentInfo.id}">
													${ccPaymentInfo.cardTypeData.name} [EXPIRED]
											</label>
											<dl>
												<dt>
													<span><spring:message code="checkoutpaymentoptionslist.creditCard.number" /></span> : <span>${ccPaymentInfo.cardNumber}</span>
												</dt>
												<dt>
													<span><spring:message code="checkoutpaymentoptionslist.creditCard.expDate" /></span> : <span>${ccPaymentInfo.expiryMonth}/${ccPaymentInfo.expiryYear}</span>
												</dt>
											</dl>
										</div>
									</c:otherwise>
								</c:choose>
								<div class="list__item__option__remove add">
									<c:if test="${ccPaymentInfo.isValid eq true && ccPaymentInfo.defaultPaymentInfo ne true}">
										<a href="#" class="btn-default" data-action-url="${setDefaultUrl}" data-payment-id="${ccPaymentInfo.id}">${sSetDefault}</a>
									</c:if>
									<a href="#" class="btn-remove" data-lightbox-title="${lightboxTitle}" data-lightbox-message="${lightboxMessage}" data-lightbox-btn-conf="${lightboxConfirmButtonText}" data-lightbox-show-confirm-button="true" data-lightbox-btn-deny="${lightboxCancelButtonText}" data-action-url="/my-account/removePaymentInfo" data-payment-id="${ccPaymentInfo.id}">
										<spring:message code="text.remove" />
									</a>
								</div>
							</div>
							</c:if>
						</c:forEach>
					</div>
				</c:otherwise>
			</c:choose>
		</c:forEach>
		<c:if test="${siteUid eq 'distrelec_FR'}">
			<mod:request-invoice htmlClasses="col-12 invoice-request-myaccount" />
		</c:if>
	</form:form>
</div>

<mod:lightbox-delete-card />