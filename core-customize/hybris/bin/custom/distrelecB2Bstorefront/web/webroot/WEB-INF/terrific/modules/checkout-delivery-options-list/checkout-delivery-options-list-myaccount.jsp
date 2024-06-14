<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:url value="/my-account/set-delivery-option" var="changeShippingUrl" />
<spring:message code="checkoutdeliveryoptionslist.shipUsing" text="Ship using" var="sShipUsing" />
<spring:message code="checkoutdeliveryoptionslist.shipUsing.express.note" text="Express Shipping Note" var="sShippingNoteExpress"/>

<div class="title">
	<h2>
		<spring:message code="checkoutdeliveryoptionslist.chooseShipment" />
	</h2>
</div>

<div class="list">
	<form:form action="${changeShippingUrl}" method="post" id="choose-shipment" class="row">
		<c:forEach items="${shippingOptions}" var="shippingOption">
			<div class="col-12 list__item">
				<input id="standard${shippingOption.code}" type="radio" class="radio-big deliveryInfo" name="shippingOption" value="${shippingOption.code}" ${selectedShippingOption eq shippingOption.code ? 'checked' : ''}>
				<span class="tick">
					<i class="fa fa-check-circle">&nbsp;</i>
				</span>
				<label for="standard${shippingOption.code}">
					<span class="small">${sShipUsing}</span>
					<c:set var = "deliveryType" value = "${digitaldata.page.pageInfo.contentgroup}.${shippingOption.translationKey}" />
					<span class="big">
						<spring:message code="${deliveryType}" text="${shippingOption.name}" /> <i class="fa fa-box-open">&nbsp;</i>
					</span>
				</label>
			</div>
			<c:if test="${siteUid eq 'distrelec_FR' and shippingOption.code eq 'SAP_N2'}">
				<div class="shipping-note-express hidden">
					<p>${sShippingNoteExpress}</p>
				</div>
			</c:if>
		</c:forEach>
	</form:form>
</div>



