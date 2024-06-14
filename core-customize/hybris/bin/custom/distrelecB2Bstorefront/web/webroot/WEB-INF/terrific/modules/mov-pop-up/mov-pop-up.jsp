<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:formatNumber value="${movLimitValue}" maxFractionDigits="0" minFractionDigits="0" var="formattedMovLimitValue"/>
<fmt:formatNumber value="${movCartValue}" maxFractionDigits="2" minFractionDigits="2" var="formattedMovCartValue"/>

<spring:message code="movpopup.text" var="sText" arguments="${[formattedMovLimitValue,cartData.movCurrency]}"/>
<spring:message code="cart.product.clearedOut.buttonOk" var="sButton" />
<spring:message code="movpopup.text.cartvalue" var="sMovCartValue" arguments="${[formattedMovCartValue, cartData.totalPrice.currencyIso]}" />

<div class="mov-pop-up">
	<div class="mov-pop-up__text">
		<p>${sText}</p>
		<p>${sMovCartValue}</p>
	</div>
	<div class="mov-pop-up__button">
		<button class="mat-button mat-button--action-red">
			${sButton}
		</button>
	</div>
</div>
<div class="mov-pop-up-background"></div>

<script type="text/javascript">
	// Send the tracking event only when the pop up appears first time and the page has loaded
	document.addEventListener("DOMContentLoaded", function () {
		var sendEvent = setInterval(isPopUpOpen, 1000);
		function isPopUpOpen() {
			if (document.readyState === "complete" && $('.mod-mov-pop-up').hasClass('mod-mov-pop-up--show')) {
				Bootstrapper.ensEvent.trigger("Minimum order warning");
				clearInterval(sendEvent);
			}
		}

		$(document).on('click', '.mod-mov-pop-up .mat-button', function () {
			$('.mod-mov-pop-up').removeClass('mod-mov-pop-up--show');
		});

		$('.btn-checkout').click(function (e) {
			e.preventDefault();
			$('.mod-mov-pop-up').addClass('mod-mov-pop-up--show');
		});
	});
</script>
