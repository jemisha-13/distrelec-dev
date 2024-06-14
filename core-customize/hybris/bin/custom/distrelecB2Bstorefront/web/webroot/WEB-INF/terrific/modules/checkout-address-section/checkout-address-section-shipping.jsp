<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="billingNotShippable" value="${not empty cartData.billingAddress and (cartData.billingAddress.billingAddress and not cartData.billingAddress.shippingAddress)}"/>
<spring:message code="checkout.deliveryPage.billingAddress" var="sBillingAddressLabel" />
<spring:message code="checkout.deliveryPage.differentAddress" var="sDifferentAddressLabel" />
<spring:message code="checkout.deliveryPage.distrelecStore"  var="sDistrelecPickLabel"/>

<div class="title">
	<h2><spring:message code="checkout.deliveryPage.shippingAddress" text="Shipping Address" /></h2>
</div>

<mod:address
	htmlClasses="selected-shipping-address"
	template="shipping-${cartData.b2bCustomerData.customerType eq 'B2B_KEY_ACCOUNT' or cartData.b2bCustomerData.customerType eq 'B2B' ? 'b2b' : 'b2c'}"
	skin="shipping"
	address="${defaultShippingAddress[0]}"
	customerType="${cartData.b2bCustomerData.customerType eq 'B2B_KEY_ACCOUNT' or cartData.b2bCustomerData.customerType eq 'B2B' ? 'b2b' : 'b2c'}"
	addressEditMode="${cartData.b2bCustomerData.customerType eq 'B2B_KEY_ACCOUNT' && shippingAddresses[0].billingAddress or isExportShop eq true ? 'false' : 'true'}"
	shippingMode="${shippingMode}"
/>

<div class="col-12 box-address__edit">
	<c:choose>
		<c:when test="${shippingMode == 'billing'}">
			<ul class="box-address__edit__list">
				<c:choose>
					<c:when test="${billingNotShippable eq true}">
						<li class="box-address__edit__list__item hidden">
							<input type="radio" name="deliverTo" id="billing" />
							<label for="billing"><span class="box-address__label">${sBillingAddressLabel}</span></label>
						</li>
						<c:choose>
							<c:when test="${!isExportShop && not isEShopGroup}">
								<li class="box-address__edit__list__item">
									<input type="radio" name="deliverTo" id="different" checked />
									<label for="different"><span class="box-address__label">${sDifferentAddressLabel}</span></label>
								</li>
							</c:when>
							<c:when test="${isExportShop && fn:length(shippingAddresses) ge 1 && not isEShopGroup}">
								<li class="box-address__edit__list__item">
									<input type="radio" name="deliverTo" id="different" />
									<label for="different"><span class="box-address__label">${sDifferentAddressLabel}</span></label>
								</li>
							</c:when>
						</c:choose>
						<c:if test="${not empty pickupWarehouses && not isEShopGroup  && not cartData.waldom}">
							<li class="box-address__edit__list__item">
								<input type="radio" name="deliverTo" id="pickup" />
								<label for="pickup"><span class="box-address__label">${sDistrelecPickLabel}</span></label>
							</li>
						</c:if>
					</c:when>
					<c:otherwise>
						<li class="box-address__edit__list__item">
							<input type="radio" name="deliverTo" id="billing" checked />
							<label for="billing"><span class="box-address__label">${sBillingAddressLabel}</span></label>
						</li>
						<c:if test="${cartData.b2bCustomerData.customerType ne 'GUEST'}">
							<c:choose>
								<c:when test="${!isExportShop && not isEShopGroup}">
									<li class="box-address__edit__list__item">
										<input type="radio" name="deliverTo" id="different" />
										<label for="different"><span class="box-address__label">${sDifferentAddressLabel}</span></label>
									</li>
								</c:when>
								<c:when test="${isExportShop && fn:length(shippingAddresses) ge 1 && not isEShopGroup}">
									<li class="box-address__edit__list__item">
										<input type="radio" name="deliverTo" id="different" />
										<label for="different"><span class="box-address__label">${sDifferentAddressLabel}</span></label>
									</li>
								</c:when>
							</c:choose>
							<c:if test="${not empty pickupWarehouses && not isEShopGroup && not cartData.waldom}">
								<li class="box-address__edit__list__item">
									<input type="radio" name="deliverTo" id="pickup" />
									<label for="pickup"><span class="box-address__label">${sDistrelecPickLabel}</span></label>
								</li>
							</c:if>
						</c:if>
					</c:otherwise>
				</c:choose>
			</ul>
		</c:when>
		<c:when test="${shippingMode =='shipping'}">
			<ul class="box-address__edit__list">
				<c:choose>
					<c:when test="${billingNotShippable eq true}">
						<li class="box-address__edit__list__item hidden">
							<input type="radio" name="deliverTo" id="billing" />
							<label for="billing"><span class="box-address__label">${sBillingAddressLabel}</span></label>
						</li>
						<c:choose>
							<c:when test="${!isExportShop}">
								<li class="box-address__edit__list__item">
									<input type="radio" name="deliverTo" id="different" checked />
									<label for="different"><span class="box-address__label">${sDifferentAddressLabel}</span></label>
								</li>
							</c:when>
							<c:when test="${isExportShop && fn:length(shippingAddresses) ge 1}">
								<li class="box-address__edit__list__item">
									<input type="radio" name="deliverTo" id="different" checked />
									<label for="different"><span class="box-address__label">${sDifferentAddressLabel}</span></label>
								</li>
							</c:when>
						</c:choose>
						<c:if test="${not empty pickupWarehouses && not isEShopGroup && not cartData.waldom}">
							<li class="box-address__edit__list__item">
								<input type="radio" name="deliverTo" id="pickup" />
								<label for="pickup"><span class="box-address__label">${sDistrelecPickLabel}</span></label>
							</li>
						</c:if>
					</c:when>
					<c:otherwise>
						<li class="box-address__edit__list__item">
							<input type="radio" name="deliverTo" id="billing" />
							<label for="billing"><span class="box-address__label">${sBillingAddressLabel}</span></label>
						</li>
						<c:choose>
							<c:when test="${!isExportShop}">
								<li class="box-address__edit__list__item">
									<input type="radio" name="deliverTo" id="different" checked />
									<label for="different"><span class="box-address__label">${sDifferentAddressLabel}</span></label>
								</li>
							</c:when>
							<c:when test="${isExportShop && fn:length(shippingAddresses) ge 1}">
								<li class="box-address__edit__list__item">
									<input type="radio" name="deliverTo" id="different" checked />
									<label for="different"><span class="box-address__label">${sDifferentAddressLabel}</span></label>
								</li>
							</c:when>
						</c:choose>
						<c:if test="${not empty pickupWarehouses && not isEShopGroup && not cartData.waldom}">
							<li class="box-address__edit__list__item">
								<input type="radio" name="deliverTo" id="pickup" />
								<label for="pickup"><span class="box-address__label">${sDistrelecPickLabel}</span></label>
							</li>
						</c:if>
					</c:otherwise>
				</c:choose>
			</ul>
		</c:when>
		<c:otherwise>
			<ul class="box-address__edit__list">
				<c:choose>
					<c:when test="${billingNotShippable eq true}">
						<li class="box-address__edit__list__item hidden">
							<input type="radio" name="deliverTo" id="billing" />
							<label for="billing"><span class="box-address__label">${sBillingAddressLabel}</span></label>
						</li>
						<c:choose>
							<c:when test="${!isExportShop}">
								<li class="box-address__edit__list__item">
									<input type="radio" name="deliverTo" id="different" />
									<label for="different"><span class="box-address__label">${sDifferentAddressLabel}</span></label>
								</li>
							</c:when>
							<c:when test="${isExportShop && fn:length(shippingAddresses) ge 1}">
								<li class="box-address__edit__list__item">
									<input type="radio" name="deliverTo" id="different" />
									<label for="different"><span class="box-address__label">${sDifferentAddressLabel}</span></label>
								</li>
							</c:when>
						</c:choose>
						<c:if test="${not empty pickupWarehouses && not isEShopGroup && not cartData.waldom}">
							<li class="box-address__edit__list__item">
								<input type="radio" name="deliverTo" id="pickup" checked />
								<label for="pickup"><span class="box-address__label">${sDistrelecPickLabel}</span></label>
							</li>
						</c:if>
					</c:when>
					<c:otherwise>
						<li class="box-address__edit__list__item">
							<input type="radio" name="deliverTo" id="billing" />
							<label for="billing"><span class="box-address__label">${sBillingAddressLabel}</span></label>
						</li>
						<c:if test="${cartData.b2bCustomerData.customerType ne 'GUEST'}">
							<c:choose>
								<c:when test="${!isExportShop}">
									<li class="box-address__edit__list__item">
										<input type="radio" name="deliverTo" id="different" />
										<label for="different"><span class="box-address__label">${sDifferentAddressLabel}</span></label>
									</li>
								</c:when>
								<c:when test="${isExportShop && fn:length(shippingAddresses) ge 1}">
									<li class="box-address__edit__list__item">
										<input type="radio" name="deliverTo" id="different" />
										<label for="different"><span class="box-address__label">${sDifferentAddressLabel}</span></label>
									</li>
								</c:when>
							</c:choose>
							<c:if test="${not empty pickupWarehouses && not isEShopGroup && not cartData.waldom}">
								<li class="box-address__edit__list__item">
									<input type="radio" name="deliverTo" id="pickup" checked />
									<label for="pickup"><span class="box-address__label">${sDistrelecPickLabel}</span></label>
								</li>
							</c:if>
						</c:if>
					</c:otherwise>
				</c:choose>
			</ul>
		</c:otherwise>
	</c:choose>
</div>

<div class="different-address-holder hidden">
	<c:if test="${!isExportShop}">
		<a href="#" class="shipping-add-new" data-target="#addressModal">
			<i class="fa fa-plus">&nbsp;</i>
			<span>
				<spring:message code="checkout.deliveryPage.billingAddress.new" />
			</span>
		</a>
	</c:if>
	<c:if test="${not empty shippingAddresses}">
		<mod:address-list
				skin="shipping"
				addressList="${shippingAddresses}"
				customerType="${cartData.b2bCustomerData.customerType eq 'B2B_KEY_ACCOUNT' or cartData.b2bCustomerData.customerType eq 'B2B' ? 'b2b' : 'b2c'}"
				addressType="shipping"
				selectedAddressId="${defaultShippingAddress[0].id}"
				addressActionMode="select"
				addressEditMode="false"
		/>
	</c:if>
</div>

<c:if test="${!isExportShop}">
	<mod:lightbox-checkout-shipping />
</c:if>

<mod:checkout-address-section template="pickup" skin="pickup" cartData="${cartData}" pickupWarehouses="${pickupWarehouses}" htmlClasses="${deliveryType == '2' ? 'active' : ''} hidden" attributes="data-delivery-type='2'" />