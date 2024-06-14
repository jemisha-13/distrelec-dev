<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<spring:message code="stock.notification.email.instruction.new" var="sStockNotificationEmailInstruction" />

<div class="modal js-availabilityPopupModal" id="modalLog" tabindex="-1">

	<section class="padded-content -header -text-center">
		<h3 id="popup.availability.get.notified"><spring:message code="availability.get.notified"/>!</h3>
		<i data-dismiss="modal" aria-hidden="true" class="fa fa-times"></i>
	</section>

	<div class="stock-notification padded-content">

		<div class="stock-notification__form-content">

			<form:form id="stock_notification" class="stock-notification__form js-stock-notification-form" method="GET">
				<div id="popup.availability.enter.email" class="stock-notification__form--instruction"><spring:message code="availability.enter.email"/></div>

				<div class="button-input">
					<input id="popup.stock.notification.email.placeholder" type="text" name="email" maxlength="255" placeholder="<spring:message code="stock.notification.email.placeholder" />" class="stock-notification__form--emailinput" value="${user.email}">
					<button id="popup.availability.notify.me" type="submit" class="mat-button mat-button__solid--action-green stock-notification__form--cta" data-aainteraction="out of stock submission" data-location="pdp"><spring:message code="availability.notify.me"/></button>
				</div>

				<span id="popup.stock.notification.email.blankText" class="stock-notification--error error-empty hidden"><spring:message code="stock.notification.email.blankText"/></span>
				<span id="popup.stock.notification.email.invalidText" class="stock-notification--error error-emailvalid hidden"><spring:message code="stock.notification.email.invalidText" /></span>

				<p id="popup.availability.privacy.policy" class="stock-notification__form--instruction-policy stock-notification__form--assurance-text">
					<spring:message code="availability.privacy.policy" arguments="/data-protection/cms/datenschutz"/>
				</p>
			</form:form>

		</div>

		<div class="stock-notification__success hidden">
			<div class="stock-notification__message-container">
				<span class="icon-wrapper">
					<i class="fa fa-check" aria-hidden="true"></i>
				</span>
				<span id="popup.stock.notification.email.successText" class="stock-notification--message"><spring:message code="stock.notification.email.successText"/></span>
			</div>
			<button id="popup.success.rohs.underReview.continueShopping" data-dismiss="modal" type="button" class="mat-button mat-button__solid--action-green"><spring:message code="rohs.underReview.continueShopping"/></button>
		</div>

		<div class="stock-notification__failure hidden">
			<div class="stock-notification__message-container">
				<span class="icon-wrapper">
					<i class="fa fa-check" aria-hidden="true"></i>
				</span>
				<span id="popup.stock.notification.email.existingUserText" class="stock-notification--message"><spring:message code="stock.notification.email.existingUserText"/></span>
			</div>
			<button id="popup.failure.rohs.underReview.continueShopping" data-dismiss="modal" type="button" class="mat-button mat-button__solid--action-green"><spring:message code="rohs.underReview.continueShopping"/></button>
		</div>
	</div>
</div>
