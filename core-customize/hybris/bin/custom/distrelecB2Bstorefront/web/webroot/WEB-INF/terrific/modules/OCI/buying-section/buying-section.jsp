<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="isLoggedin" value="false" />
<c:set var="salesUnitLowerCase" value="${fn:toLowerCase(product.salesUnit)}" />
<c:if test="${salesUnitLowerCase eq '1 pieces'}">
	<c:set var="salesUnitLowerCase" value="1 piece" />
</c:if>
<sec:authorize access="hasRole('ROLE_CUSTOMERGROUP')">
	<c:set var="isLoggedin" value="true" />
</sec:authorize>
<spring:message code='buyingSection.error.min.order.text' arguments="${product.orderQuantityMinimum}" var="sMinOrderText"/>
<spring:message code="buyingSection.error.step.order.quantity" arguments="${product.orderQuantityStep}" var="sStepPDP"/>
<spring:message code="buyingSection.quantity" var="sQuantityText"/>

<div class="elem">
    <c:if test="${!product.catPlusItem}">
		<div class="tooltip-hover tooltip-information"> <div class="shipping-content"></div> </div>	
		<div class="arrow-down"></div>
	</c:if>
</div>
<div class="elem elem-last">
    <div class="elem__content elem__content--first">
		<span class="hidden">${sQuantityText}</span>
		<div class="numeric"  data-product-code="${product.code}"
			 data-min="${product.orderQuantityMinimum}"
			 data-step="${product.orderQuantityStep}"
			 data-min-error='<spring:message code="buyingSection.error.min.order.quantity" arguments="${product.codeErpRelevant},${product.orderQuantityMinimum}" htmlEscape="true"/>'
			 data-step-error='<spring:message code="buyingSection.error.step.order.quantity" arguments="${product.orderQuantityStep}" htmlEscape="true"/>' >
			<div class="btn-wrapper">
				<button class="btn numeric-btn numeric-btn-down disabled">&ndash;</button>
				<label for="countItems" class="hidden">${sQuantityText}</label>
				<input type="text" id="countItems" name="countItems" placeholder="${product.orderQuantityMinimum}" class="ipt" value="${product.orderQuantityMinimum}">
				<input type="hidden" id="pdpSearchDatalayer" name="pdpSearchDatalayer" value="${pdpSearchDatalayer}">
				<button class="btn numeric-btn numeric-btn-up">+</button>

				<div class="numeric-popover popover top">
					<div class="arrow"></div>
					<div class="popover-content"></div>
				</div>
			</div>
			<span class="btn-text">
				${sMinOrderText}
				<c:if test="${product.orderQuantityStep > 1}">
					<span class="btn-text btn-text--step">
						${sStepPDP}
					</span>
				</c:if>
			</span>

		</div>
	</div>
	<div class="elem__content elem__content--second">
		<c:choose>
			<c:when test="${product.catPlusItem}">
				<button class="btn btn-primary btn-bulk-discount" style="padding: 10px 15px;float: right;max-width: 220px;" data-product-code="${product.code}" title="<spring:message code='buyingSection.quotation.request'/>">
					<span class="ellipsis" style="line-height: 18px;" title="<spring:message code="buyingSection.quotation.request"/>"><spring:message code="buyingSection.quotation.request"/></span>
				</button>
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${empty product.price and empty product.listPrice}">
						<button class="btn btn-primary btn-cart fb-add-to-cart disabled" disabled="disabled" data-product-code="${product.code}" title="<spring:message code='product.notBuyable.temporarly.message'/>"><i></i>
							<span class="ellipsis" title="<spring:message code="buyingSection.add.to.cart"/>"><spring:message code="buyingSection.add.to.cart"/></span>
						</button>
					</c:when>
					<c:otherwise>

						<button class="mat-button mat-button--action-green btn-cart fb-add-to-cart disabled" data-product-code="${product.code}" title="<spring:message code='buyingSection.add.to.cart'/>">
							<i class="fa fa-cart-plus" aria-hidden="true"></i><span class="ellipsis" title="<spring:message code="buyingSection.add.to.cart"/>"><spring:message code="buyingSection.add.to.cart"/></span>
						</button>

						<span class="btn-added-to-cart buying-section__OCI">
							<span class="ellipsis btn-added-to-cart--added" title="<spring:message code="text.added.to.cart"/>">
								<i class="far fa-check-circle"></i>
								<spring:message code="text.added.to.cart"/>
							</span>
						</span>

					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
	</div>
</div>