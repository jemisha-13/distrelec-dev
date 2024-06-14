<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<spring:message code="checkoutordersummarycostcenterbox.title" var="sTitle"/>
<spring:message code="checkoutordersummarycostcenterbox.costCenter" var="sCostCenter"/>
<spring:message code="checkoutordersummarycostcenterbox.projectNumber" var="sProjectNumber"/>
<spring:message code="checkoutordersummarycostcenterbox.editButtonText" var="sButtonChange"/>
<spring:message code="checkout.summary.deliveryAddress.edit" var="sButtonEdit"/>
<spring:message code="support.failed" var="sErrorMsg"/>
<spring:message code="addressform.buttonSave" var="sButtonSave"/>


<c:set var="isEditableByAllOption" value="${cartData != null ? cartData.openOrderEditableForAllContacts : orderData.openOrderEditableForAllContacts}" />

<h2 class="head">
	${sTitle}
	<c:if test="${orderData.status == 'PENDING_APPROVAL'}">
		<span id="btnEditOrderRef" class="js-btn-edit-order-ref">${sButtonEdit}</span>
	</c:if>
</h2>

<div class="box padding-left">
	<dl>
		<c:if test="${currentSalesOrg.erpSystem eq 'MOVEX'}">
			<dt class="label">${sCostCenter}</dt>
			<dd class="value"><c:out value="${costCenter}" /></dd>
		</c:if>
		<c:choose>
			<c:when test="${cartData != null}">
				<dd class="value"><c:out value="${order.projectNumber}" /></dd>
			</c:when>
			<c:otherwise>
				<c:if test="${orderData.openOrder}">
					<dd class="value open-order-reference"><c:out value="${orderData.openOrderReference}" /></dd>
				</c:if>
				<c:if test="${not orderData.openOrder}">
					<dd class="value open-order-reference">
						<div class="refInputContainer js-refInputContainer hidden">
							<form class="js-refInputForm">
								<input type="hidden" name="orderCode" value="${orderData.code}"/>
								<input type="hidden" name="workflowCode" value="${orderApprovalData.workflowActionModelCode}"/>
								<input type="text" name="orderReference" value="${orderData.projectNumber}" title="orderData.projectNumber" class="refInput js-refInput" placeholder="${sTitle}" data-placeholder="${sTitle}" />
							</form>
							<button class="btn btn-secondary saveDetail">${sButtonSave}</button>
						</div>
						<span class="refText js-reference-text"><c:out value="${orderData.projectNumber}" /></span>
						<div class="error js-reference-error hidden">${sErrorMsg}</div>
					</dd>
				</c:if>
			</c:otherwise>
		</c:choose>
	</dl>

	<c:if test="${isOrderEditable}">
		<a href="#" class="btn btn-secondary btn-change" data-order-code="${orderCode}"><i></i> ${sButtonChange}</a>
	</c:if>
</div>

<c:if test="${orderData.openOrder}">
	<mod:lightbox-order-reference orderData="${orderData}" />
</c:if>
