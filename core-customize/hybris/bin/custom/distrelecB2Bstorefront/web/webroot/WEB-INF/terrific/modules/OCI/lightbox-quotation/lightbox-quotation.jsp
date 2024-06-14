<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="format1" tagdir="/WEB-INF/tags/shared/format" %>

<c:set var="articleNumber">
	<format1:articleNumber articleNumber="${product.codeErpRelevant}" />
</c:set>

<div class="modal" id="modalQuotation" tabindex="-1">
	<div class="hd">
		<div class="-left">
			<h3 class="title">
				<c:choose>
					<c:when test="${product.catPlusItem}"><spring:message code="lightboxquotation.catplus.title" /></c:when>
					<c:otherwise><spring:message code="lightboxquotation.title" /></c:otherwise>
				</c:choose>
			</h3>
		</div>
		<div class="-right">
			<a class="btn btn-close" href="#" data-dismiss="modal" aria-hidden="true"><spring:message code="base.close" /></a>
		</div>
	</div>
	<div class="bd base">
		<%-- form item: quantity --%>
		<div class="form-row">
			<label for="modalQuotationQuantity" class="small"><spring:message code="lightboxquotation.quantity.label" /></label>

			<div class="box cf">
				<div class="cell stepper">
					<div class="numeric numeric-small" 
						data-min="${product.orderQuantityMinimum}" 
						data-step="${product.orderQuantityStep}"
					 	data-min-error="<spring:message code='validation.error.min.order.quantity' arguments='${articleNumber},${product.orderQuantityMinimum}' htmlEscape='true' />"
					 	data-step-error='<spring:message code='validation.error.steps.order.quantity' arguments='${articleNumber},${product.orderQuantityStep}' htmlEscape="true"/>'
					>
						<button class="btn numeric-btn numeric-btn-down disabled"><i></i></button>
						<input class="ipt field" id="modalQuotationQuantity" name="modalQuotationQuantity" type="text" value="${product.orderQuantityMinimum}" />
						<button class="btn numeric-btn numeric-btn-up"><i></i></button>
						<div class="numeric-popover popover top">
							<div class="arrow"></div>
							<div class="popover-content"></div>
						</div>
					</div>
				</div><!--end cell-->

				<div class="cell prices">
					<%-- prices --%>
					<table>
						<tr class="total">
							<td class="label"><spring:message code="lightboxquotation.price.total" /></td>
							<td class="price">
								<span class="currency">${product.price.currencyIso}</span>
								<span class="price-total">${product.price.value}</span>
							</td>
						</tr>
						<tr class="list">
							<td class="label"><spring:message code="lightboxquotation.price.list" /></td>
							<td class="price">
								<format:price format="defaultSplit" priceData="${product.price}" />
							</td>
						</tr>
					</table>
				</div><!--end cell-->
			</div><!--end box-->
		</div><!--end form-row-->


		<sec:authorize access="hasAnyRole('ROLE_OCICUSTOMERGROUP','ROLE_ARIBACUSTOMERGROUP','ROLE_CXMLCUSTOMERGROUP')">
			<div class="oci-ariba-extra-fields">
				<div class="form-row">
					<label for="company" class="required"><spring:message code="register.company"/> *</label>
					<input type="text" name="company" id="company" maxlength="255" placeholder="<spring:message code='register.company' />" class="base field input-bulk validate-empty">
				</div>
				
				<div class="form-row">
					<label for="firstName" class="required"><spring:message code="register.firstName"/> *</label>
					<input type="text" name="firstName" id="firstName" maxlength="255" placeholder="<spring:message code='register.firstName' />" class="base field input-bulk validate-empty">
				</div>
				
				<div class="form-row">
					<label for="lastName" class="required"><spring:message code="register.lastName"/> *</label>
					<input type="text" name="lastName" id="lastName" maxlength="255" placeholder="<spring:message code='register.lastName' />" class="base field input-bulk validate-empty">
				</div>
				
				<div class="form-row">
					<label for="phone" class="required"><spring:message code="register.phoneNumber"/> *</label>
					<input type="text" name="phone" id="phone" maxlength="15" placeholder="<spring:message code='register.phoneNumber' />" class="base field input-bulk validate-empty">
				</div>	
				
				<div class="form-row">
					<label for="email" class="required"><spring:message code="register.email"/> *</label>
					<input type="text" name="email" id="email" required="required" placeholder="<spring:message code='register.email' />" class="base field input-bulk validate-empty validate-email">
				</div>					
			</div>
		</sec:authorize>				

		<%-- form item: message --%>
		<div class="form-row">
			<label for="modalQuotationMessage" class="small"><spring:message code="lightboxquotation.message.label" /></label>
			<textarea class="field textarea" id="modalQuotationMessage" name="modalQuotationMessage"></textarea>
		</div><!--end form-row-->
	</div>
	<%-- form item: submit, cancel --%>
	<div class="ft">
		<input type="submit" class="btn btn-secondary" value="<spring:message code="lightboxquotation.btn.cancel" />" data-dismiss="modal" aria-hidden="true" />
		<input type="submit" class="btn btn-primary btn-send" value="<spring:message code="lightboxquotation.btn.send" />" />
	</div>
</div>
