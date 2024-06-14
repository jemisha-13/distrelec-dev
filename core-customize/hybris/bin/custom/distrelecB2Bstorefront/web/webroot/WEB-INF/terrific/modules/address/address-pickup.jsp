<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<div class="address-list">
	<input type="text" name="warehouseCode" title="warehouseCode" class="address-list__warehouse hidden" value="${warehouse.code}" />
	<h2 class="address-pick-up"><spring:message code="orderdetailsection.pickupLocation" /></h2>
	<div class="address-list__title">
		<c:choose>
			<c:when test="${addressActionMode == 'radio'}">
				<h3>
					<input id="warehouse-${warehouse.code}" type="radio" class="radio-big" name="${addressType}" value="${warehouse.code}" ${warehouse.code eq selectedAddressId ? 'checked' : ''}/>
					<label for="warehouse-${warehouse.code}" ><c:out value="${warehouse.name}" /></label>
				</h3>
			</c:when>
			<c:otherwise>
				<h3><c:out value="${warehouse.name}" /></h3>
			</c:otherwise>
		</c:choose>
	</div>
	<div class="address-list__details">
		<div class="address-list__details__item">
			<div class="row">
				<div class="col-6">
					<span class="address-key"><spring:message code="checkout.address.street" text="Street" /></span>
					<span class="address-value"><c:out value="${warehouse.streetName}" />&nbsp;<c:out value="${warehouse.streetNumber}" /></span>
				</div>
				<div class="col-6">
					<span class="address-key"><spring:message code="checkout.address.city" text="City" /></span>
					<span class="address-value"><c:out value="${warehouse.town}" /></span>
				</div>
				<div class="col-12">
						<span class="address-key"><spring:message code="address.postcode" text="Post Code" /></span>
						<span class="address-value"><c:out value="${warehouse.postalCode}" /></span>
				</div>
				<div class="col-12">
					<span class="address-key"><spring:message code="checkout.address.phone" text="Phone" /></span>
					<span class="address-value"><c:out value="${warehouse.phone}" /></span>
				</div>
				<div class="col-6">
					<span class="address-key"><spring:message code="address.shipping.opening.hours" text="Pick-up desk opening hours" />:&nbsp;</span>
					<span class="address-key"><spring:message code="checkout.address.mo2fr" text="Mo.-Fr." /></span>
					<span class="address-value"><c:out value="${warehouse.openingsHourMoFr}" /></span>
				</div>
				<div class="col-6">
					<c:if test="${not empty warehouse.openingsHourSa}">
						<span class="address-key"><spring:message code="checkout.address.saturday" text="Sa." /></span>
						<span class="address-value"><c:out value="${warehouse.openingsHourSa}" /></span>
					</c:if>
				</div>
				<c:if test="${currentSalesOrg.erpSystem ne 'MOVEX'}">
					<div class="col-12">
						<mod:toolsitem template="toolsitem-availability" skin="availability" warehouseCode="${warehouse.code}"/>
					</div>
				</c:if>
			</div>
		</div>
	</div>
</div>
