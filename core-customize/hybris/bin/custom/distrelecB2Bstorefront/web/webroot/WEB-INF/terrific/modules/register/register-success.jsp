<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>

<spring:theme code="register.successThankYouText.1" text="Thanks." var="sSuccessThankYouText1" />
<spring:theme code="register.successThankYouText.2" text="Lorem ipsum." var="sSuccessThankYouText2" arguments="${registeredEmail}" />
<spring:theme code="register.successTitle" var="sExistingTitle" />
<spring:theme code="register.successThankYouText.approvalByCSNeeded" var="sExistingText" />

<section class="checkout-register-holder checkout-register-holder--success">
	<div class="container">
		<div class="row">
			<div class="col-12">
				<div class="card-wrapper">
					<i class="far fa-envelope" aria-hidden="true"></i>
						<div class="card-wrapper__text">
							<h4 class="mb-4">${sExistingTitle}</h4>
							<p class="small">${sExistingText}</p>
						</div>
				</div>
			</div>
		</div>
	</div>
</section>

