<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>

<spring:theme code="checkout.orderConfirmation.title" text="Order Complete" var="sOrderConfirmTitle" />
<spring:theme code="checkout.orderConfirmation.orderId" text="Order Id:" var="sOrderId" />
<spring:message code="service.nav.telephone" var="sCustomerNumber" />

<c:choose>
   	 <c:when test="${isEShopGroup == false}">
		<spring:theme code="checkout.orderConfirmation.next.step" text="This is the Order Confirmation" var="sConfirmationText1" arguments="${userId}, ${sCustomerNumber}" />
	</c:when>
	<c:otherwise>
		<spring:theme code="checkout.orderConfirmation.next.step" text="This is the Order Confirmation for b2e customer" var="sConfirmationText1" arguments="${userId}, ${sCustomerNumber}" />
	</c:otherwise>
</c:choose>
<spring:theme code="checkout.orderConfirmation.text2" text="Keep your Order Number as a reference" var="sConfirmationText2" />
<spring:theme code="checkout.orderConfirmation.backToStore.buttonText" text="Return to store" var="sButtonText" />
<c:url var="continueUrl" value="/" />

<div class="card-wrapper card-wrapper--pickup">
	<div class="card-wrapper__item">
		<h2>
			${sOrderConfirmTitle}
		</h2>

		<spring:message code="checkout.orderConfirmation.processing.error" text="Cannot generate your order confirmation number" var="loadOrderCodeError"/>

		<div class="order-code-loading" data-order-code="${orderCode}">
			<span class="loading">
				<img class="loading-icon img-fluid" src="/_ui/all/media/img/page-preloader.gif">
				<span class="loading__text">
					<spring:message code="checkout.orderConfirmation.processing" text="Processing order number..." />
				</span>
			</span>
		</div>
		<div class="order-code" data-load-order-code-error="${loadOrderCodeError}">
			<div class="order-code__number">
				<span class="meta">${sOrderId}</span>
				<span class="big" >-</span>
			</div>
		</div>

		<div class="item-text">
			<p>${sConfirmationText1}</p>
		</div>

		<div class="item-text">
			<p>${sConfirmationText2}</p>
		</div>

		<div class="item-text">
			<p class="confirmation-text-2 makeItBold">
				<spring:message code="checkout.orderConfirmation.pickup.title" text="Pickup your order now" />
			</p>
		</div>

		<mod:address
			template="pickup-review" 
			warehouse="${pickupLocation}" 
			addressActionMode="location" 
			addressType="pickup" 
			customerType="b2b" 
		/>
	</div>

	<c:if test="${updateProfile}">
		<spring:message code="form.global.error.erpcommunication" var="sError"/>
		<spring:message code="form.select.empty" var="sSelect"/>
		<spring:message code="validate.error.dropdown" var="sMandatory"/>

		<div class="card-wrapper__item card-wrapper__item--second update-profile" data-error-message="${sError}" data-mandatory-message="${sMandatory}">
			<div class="update-profile__item">
				<h3><spring:message code="checkout.orderConfirmation.updateProfile" text="Please help us by completing your profile information" /></h3>
				<c:if test="${not empty departments}">
					<div class="update-profile__item__field">
						<label for="department"><spring:message code="register.department" text="Department" /></label>
					</div>
					<div class="update-profile__item__field">
						<select id="department" class="selectpicker validate-dropdown">
							<option value="" disabled="disabled" selected="selected">${sSelect}</option>
							<c:forEach items="${departments}" var="department">
								<option value="${department.code}">
										${department.name}
								</option>
							</c:forEach>
						</select>
					</div>
				</c:if>
				<c:if test="${not empty functions}">
					<div class="update-profile__item__field">
						<label for="function"><spring:message code="register.function" text="Function" /></label>
					</div>
					<div class="update-profile__item__field">
						<select id="function" class="selectpicker validate-selectbox">
							<option value="" disabled="disabled" selected="selected">${sSelect}</option>
							<c:forEach items="${functions}" var="function">
								<option value="${function.code}">
										${function.name}
								</option>
							</c:forEach>
						</select>
					</div>
				</c:if>
				<div class="button-container">
					<a class="btn btn-primary btn-update-profile" href="#"><spring:message code="checkout.orderConfirmation.updateProfile.submit" text="Save" /><i class="icon-arrow"></i></a>
					<span class="error"></span>
				</div>
			</div>
		</div>

	</c:if>
</div>
