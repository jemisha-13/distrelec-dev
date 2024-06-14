<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="customerType" value="${cartData.b2bCustomerData.customerType eq 'B2C' ? 'b2c' : 'b2b'}" />
<c:set var="keyAccountUser" value="${cartData.b2bCustomerData.customerType eq 'B2B_KEY_ACCOUNT'}" />

<div class="title">
	<h1><spring:message code="checkout.address.${customerType}.billing.address" text="Billing Address" /></h1>
</div>
<div class="form">
	<c:choose>
		<c:when test="${isExportShop}">
			<div class="mod-global-messages hidden">
				<div class="bd warning">
					<div class="ct">
						<div class="c-center">
							<div class="c-center-content">
								<div class="col-c">
									<p><spring:message code="accountlist.addresses.companyName.noedit.export" /></p>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</c:when>
		<c:when test="${customerType eq 'b2b'}">
			<div class="mod-global-messages hidden">
				<div class="bd warning">
					<div class="ct">
						<div class="c-center">
							<div class="c-center-content">
								<div class="col-c">
									<p><spring:message code="accountlist.addresses.companyName.noedit.b2b" /></p>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</c:when>
		<c:when test="${keyAccountUser}">
			<div class="mod-global-messages hidden">
				<div class="bd warning">
					<div class="ct">
						<div class="c-center">
							<div class="c-center-content">
								<div class="col-c">
									<p><spring:message code="accountlist.addresses.companyName.noedit.keyaccount" /></p>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</c:when>
	</c:choose>
	<mod:address
		template="billing-${cartData.b2bCustomerData.customerType eq 'B2C' ? 'b2c' : 'b2b'}"
		skin="billing" address="${billingAddresses[0]}"
		addressEditMode="${keyAccountUser or isExportShop ? 'false' : 'true'}"
		customerType="${cartData.b2bCustomerData.customerType}" />
</div>
