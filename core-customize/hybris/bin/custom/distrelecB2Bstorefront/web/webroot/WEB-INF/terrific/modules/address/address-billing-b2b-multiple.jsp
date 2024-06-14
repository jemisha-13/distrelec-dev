<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>


<c:url value="/checkout/address/update/b2b/billing" var="changeAddressUrl" />

<c:set var="userType" value="${cartData.b2bCustomerData.customerType}"/>
<div class="box box-address box-address-${address.id} address-billing-b2b">
	<div class="row">
		<c:choose>
			<c:when test="${fn:length(billingAddressList) gt 1}">
				<div class="col-1 box-address__input">
					<input id="address-${address.id}" type="radio" class="radio-big" name="${addressType}-${customerType}" value="${address.id}" ${address.id eq selectedAddressId ? 'checked' : ''} />
					<label for="address-${address.id}">&nbsp;</label>
				</div>
				<div class="col-11 box-address__preview">
					<c:if test="${not empty address.companyName}">
						<p>
							<c:out value="${address.companyName}" />
						</p>
					</c:if>
					<c:if test="${not empty address.companyName2}">
						<p>
							<c:out value="${address.companyName2}" />
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
			</c:when>
			<c:otherwise>
				<div class="col-12 box-address__preview">
					<c:if test="${not empty address.companyName}">
						<p>
							<c:out value="${address.companyName}" />
						</p>
					</c:if>
					<c:if test="${not empty address.companyName2}">
						<p>
							<c:out value="${address.companyName2}" />
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
			</c:otherwise>
		</c:choose>
	</div>
</div>
