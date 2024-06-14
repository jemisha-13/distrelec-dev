<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<spring:message code="checkout.completedelivery.title" text="Complete delivery" var="sCompleteDelivery" />
<spring:message code="checkout.completedelivery.checkbox" text="Complete delivery" var="sCompleteDeliveryText" />

<div class="mod-global-messages">
	<div class="bd warning">
		<div class="ct">
			<div class="c-center">
				<div class="c-center-content">
					<div class="col-c">
						<p>
							<formUtil:formCheckbox idKey="completeDelivery"  inputCSS="checkbox-big checkbox-big__delivery" path="reviewForm.completeDelivery" mandatory="false"  />
							<span>${sCompleteDeliveryText}</span>
						</p>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
