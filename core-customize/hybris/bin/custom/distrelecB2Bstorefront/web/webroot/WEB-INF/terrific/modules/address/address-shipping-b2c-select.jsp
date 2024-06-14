<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<spring:message code="text.setDefault" var="sSetDefault" />

<div class="box box-address box-address-${address.id} address-shipping-b2b">
	<input type="text" class="hidden address-id" value="${address.id}" />
	<div class="row">
		<input type="radio" title="selectAddress" name="selectAddress" class="box-address__select" />
		<div class="box-address__preview col-6">
			<c:if test="${not empty address.title}">
				<p>
					<c:out value="${address.title}" />
				</p>
			</c:if>
			<c:if test="${not empty address.firstName}">
				<p>
					<c:out value="${address.firstName}" />&nbsp;

					<c:if test="${not empty address.lastName}">
						<c:out value="${address.lastName}" />
					</c:if>
				</p>
			</c:if>
			<c:if test="${not empty address.line1}">
				<p>
					<c:out value="${address.line1}" />
					<c:out value=" ${address.line2}" />
				</p>
			</c:if>
			<c:if test="${not empty address.postalCode}">
				<p>
					<c:out value="${address.postalCode}" />
					<c:out value=" ${address.town}" />
				</p>
			</c:if>
			<c:if test="${not empty address.country.name}">
				<p>
					<c:out value="${address.country.name}" />
				</p>
			</c:if>
		</div>
		<div class="box-address__remove col-5">
			<div class="box-address__remove__links">
				<c:if test="${not address.defaultShipping}">
					<a href="#" class="default-link" data-action="/my-account/set-default-address?addressCode=${address.id}&shipping=true">
							${sSetDefault}
					</a>
				</c:if>
				<c:if test="${!isExportShop}">
					<a href="#" class="shipping-edit">
						<spring:message code="checkout.summary.deliveryAddress.edit" />
					</a>
					<mod:lightbox-checkout-shipping-edit address="${address}" addressIndex="${addressIndex}"/>
				</c:if>
				<a href="#" class="remove-link">
					<spring:message code="text.remove" />
				</a>
			</div>
		</div>
	</div>
</div>
