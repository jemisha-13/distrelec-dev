<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>

<spring:message code="accountlist.addresses.title" var="sTitle" />
<spring:message code="accountlist.addresses.billingAddress.title" var="sBillingAddressTitle" />
<spring:message code="accountlist.addresses.billingAddress.titlePlural" var="sBillingAddressTitlePlural" />
<spring:message code="accountlist.addresses.deliveryAddress.title" var="sDeliveryAddressTitle" />
<spring:message code="accountlist.addresses.deliveryAddress.titlePlural" var="sDeliveryAddressTitlePlural" />
<spring:message code="accountlist.addresses.deliveryAddress.infoTitle" var="sDeliveryAddressInfoTitle" />
<spring:message code="accountlist.addresses.deliveryAddress.infoMessage" var="sDeliveryAddressInfoMessage" />
<spring:message code="accountlist.addresses.name" var="sName" />
<spring:message code="accountlist.addresses.companyName" var="sCompanyName" />
<spring:message code="accountlist.addresses.companyName2" var="sCompanyName2" />
<spring:message code="accountlist.addresses.city" var="sCity" />
<spring:message code="accountlist.addresses.street" var="sStreet" />
<spring:message code="accountlist.addresses.addressAdditional" var="sAddressAdditional" />

<spring:message code="accountlist.addresses.department" var="sDepartment" />
<spring:message code="accountlist.addresses.telephone" var="sTelephone" />
<spring:message code="accountlist.addresses.contactPhone" var="sContactPhone" />
<spring:message code="accountlist.addresses.country" var="sCountry" />
<spring:message code="accountlist.addresses.button.addAddress" var="sButtonAddAddress" />
<spring:message code="accountlist.addresses.button.showMore" var="sButtonShowMore" />
<spring:message code="text.setDefault" var="sSetDefault" />
<spring:theme code="addressform.deleteLightbox.title" var="lightboxTitle" />
<spring:theme code="addressform.deleteLightbox.message" var="lightboxMessage" />
<spring:theme code="addressform.deleteLightbox.confirm" var="lightboxConfirmButtonText" />
<spring:theme code="addressform.deleteLightbox.deny" var="lightboxDenyButtonText" />
<spring:theme code="lightbox.confirm.set.address.default" var="sSetAddressDefault" />

<c:set var="addressesPerPage" value="10" />
<c:set var="pageSize" value="${addressesPerPage}" />

<c:choose>
	<c:when test="${isExportShop}"> <div class="small gu-8 address-change-info"> <spring:message code="accountlist.addresses.companyName.noedit.export" text=" " /> </div> </c:when>
	<c:when test="${user.customerType eq 'B2B'}"> <div class="small gu-8 address-change-info"> <spring:message code="accountlist.addresses.companyName.noedit.b2b" text=" " /> </div> </c:when>
	<c:when test="${user.customerType eq 'B2B_KEY_ACCOUNT'}"> <div class="small gu-8 address-change-info"> <spring:message code="accountlist.addresses.companyName.noedit.keyaccount" text=" " /> </div> </c:when>
</c:choose>
<div class="billing-address 2">
	<c:choose>
		<c:when test="${fn:length(paymentAddresses) > 1}"> 	<h2>${sBillingAddressTitlePlural}</h2>  </c:when>
		<c:otherwise> <h2>${sBillingAddressTitle}</h2>  </c:otherwise>
	</c:choose>

	<ul class="data-list">
		<c:forEach items="${paymentAddresses}" var="paymentAddress">
			<li class="row">
				<c:choose>
					<c:when test="${user.customerType eq 'B2C'}">
						<h3 class="base"><c:out value="${paymentAddress.title}" />&nbsp;<c:out value="${paymentAddress.firstName}" />&nbsp;<c:out value="${paymentAddress.lastName}" /></h3>
						<ul class="data cf">
							<li class="entry">
								<dl>
									<dt class="label">${sStreet}</dt>
									<dd class="value"><c:out value="${paymentAddress.line1} " /><c:out value="${paymentAddress.line2}" /></dd>
								</dl>
							</li>
							<li class="entry">
								<dl>
									<dt class="label">&nbsp;</dt> <dd class="value">&nbsp;</dd> <dt class="label">${sCity}</dt>
									<dd class="value"><c:out value="${paymentAddress.postalCode}" />&nbsp;<c:out value="${paymentAddress.town}" /></dd>
								</dl>
							</li>
							<li class="entry">
								<dl>
									<dt class="label">&nbsp;</dt> <dd class="value">&nbsp;</dd> <dt class="label">${sCountry}</dt>
									<dd class="value"><c:out value="${paymentAddress.country.name}" /></dd>
								</dl>
							</li>
							<li class="action">  <a href="edit-address/b2c/${paymentAddress.id}" class="more"><i></i></a>  </li>
						</ul>
					</c:when>
					<c:otherwise>
						<h3 class="base"><c:out value="${paymentAddress.companyName}" /></h3>

						<div class="company-name-sub">
							<dl class="entry">
								<dt class="label">${sCompanyName2}</dt>
								<dd class="value">
									<c:out value="${not empty paymentAddress.companyName2 ? paymentAddress.companyName2 : '-'}" />
									<c:if test="${not empty paymentAddress.companyName3}"><br/><c:out value="${paymentAddress.companyName3}" /></c:if>
								</dd>
							</dl>
						</div>

						<ul class="data">
							<li class="entry">
								<dl>
									<dt class="label">${sStreet}</dt>
									<dd class="value"><c:out value="${paymentAddress.line1} " /><c:out value="${paymentAddress.line2}" /></dd>
								</dl>
							</li>
							<li class="entry">
								<dl>
									<dt class="label">${sCity}</dt>
									<dd class="value">
										<c:out value="${paymentAddress.postalCode}" />&nbsp;<c:out value="${paymentAddress.town}" />
									</dd>
								</dl>
							</li>
							<li class="entry">
								<dl>
									<dt class="label">${sCountry}</dt>
									<dd class="value ellipsis countryName" title="${paymentAddress.country.name}"><c:out value="${paymentAddress.country.name}" /></dd>
								</dl>
							</li>
							<c:if test="${user.customerType != 'B2B_KEY_ACCOUNT' && isExportShop eq false}" >
								<li class="action">
									<a href="edit-address/b2bbilling/${paymentAddress.id}" class="more"><i></i></a>
								</li>
							</c:if>
							<c:if test="${ paymentAddress.defaultBilling eq false and fn:length(paymentAddresses) > 1 }">
								<div class="set-as-default ellipsis 1" title="${sSetDefault}">
									<a href="#"
									   data-action="/my-account/set-default-address?addressCode="
									   data-is-billing="${paymentAddress.billingAddress}"
									   data-is-shipping="${paymentAddress.shippingAddress}"
									   data-address-id="${paymentAddress.id}"
									   data-lightbox-title="${lightboxTitle}"
									   data-lightbox-message="${sSetAddressDefault}"
									   data-lightbox-btn-deny="${lightboxDenyButtonText}"
									   data-lightbox-show-confirm-button="true"
									   data-lightbox-btn-conf="${lightboxConfirmButtonText}"
									   class="btn btn-secondary btn-set-default set-as-default-button ellipsis">${sSetDefault}</a>
								</div>	
							</c:if>								
						</ul>
					</c:otherwise>
				</c:choose>
			</li>
		</c:forEach>
	</ul>
</div>

<div class="delivery-addresses">
	<%-- info message
	<c:if test="${fn:toLowerCase(currentChannel.type) == 'b2b'}">
		<mod:global-messages type="information" displayIcon="true" headline="${sDeliveryAddressInfoTitle}" body="${sDeliveryAddressInfoMessage}" />
	</c:if>
	 --%>
	<c:choose>
		<c:when test="${fn:length(deliveryAddresses) > 1}">
			<h2>${sDeliveryAddressTitlePlural}</h2>
		</c:when>
		<c:otherwise>
			<h2>${sDeliveryAddressTitle}</h2>
		</c:otherwise>
	</c:choose>

	<%-- selectbox: order by --%>
	<div class="action-box">
		<%-- button: add new address --%>
		<%-- export users are not allowed to add more addresses --%>
		<c:if test="${isExportShop eq false}">
			<c:url value="add-address" var="addAddress" />
			<a href="${addAddress}" class="btn btn-primary btn-add"><i></i> ${sButtonAddAddress}</a>
		</c:if>		
		<%-- module account-order --%>
		<mod:account-order template="addresses" skin="print" />
	</div>

	<%-- shipping address data --%>
	<ul class="data-list">
		<c:forEach items="${deliveryAddresses}" var="deliveryAddress" varStatus="status">
			<li class="row${status.count > pageSize ? ' paged' : ''}">
				<c:choose>
					<c:when test="${user.customerType eq 'B2C'}">
						<h3 class="base">
							<c:out value="${deliveryAddress.title}" />&nbsp;
							<c:out value="${deliveryAddress.firstName}" />&nbsp;
							<c:out value="${deliveryAddress.lastName}" />
						</h3>
						<ul class="data cf">
							<li class="entry">
								<dl>
									<dt class="label">${sStreet}</dt>
									<dd class="value"><c:out value="${deliveryAddress.line1} " /><c:out value="${deliveryAddress.line2}" /></dd>									
								</dl>
							</li>						
							<li class="entry">
								<dl>
									<dt class="label phone">${sContactPhone}</dt> 
									<dd class="value"><c:out value="${not empty deliveryAddress.phone ? deliveryAddress.phone : '-'}" /></dd>
									<dt class="label">${sCity}</dt>
									<dd class="value"><c:out value="${deliveryAddress.postalCode}" />&nbsp;<c:out value="${deliveryAddress.town}" /></dd>
								</dl>
							</li> 
							<li class="entry">
								<dl>
									<dt class="label">&nbsp;</dt><dd class="value">&nbsp;</dd><dt class="label">${sCountry}</dt>
									<dd class="value"><c:out value="${deliveryAddress.country.name}" /></dd>
								</dl>
							</li>							
							<li class="action">  <a href="edit-address/b2c/${deliveryAddress.id}" class="more"><i></i></a> </li>
							<c:if test="${deliveryAddress.defaultShipping eq false and fn:length(deliveryAddresses) > 1}">
								<div class="set-as-default ellipsis 2" title="${sSetDefault}">
									<a href="#"
									   data-action="/my-account/set-default-address?addressCode="
									   data-is-billing="false"
									   data-is-shipping="true"
									   data-address-id="${deliveryAddress.id}"
									   data-lightbox-title="${lightboxTitle}"
									   data-lightbox-message="${sSetAddressDefault}"
									   data-lightbox-btn-deny="${lightboxDenyButtonText}"
									   data-lightbox-show-confirm-button="true"
									   data-lightbox-btn-conf="${lightboxConfirmButtonText}"
									   class="btn btn-secondary btn-set-default set-as-default-button ellipsis">${sSetDefault}</a>
								</div>	
							</c:if>						
						</ul>
					</c:when>
					<c:otherwise>
						<h3 class="base"> <c:out value="${deliveryAddress.companyName}" /> </h3>
						<ul class="data cf">
							<li class="entry">
								<dl>
									<dt class="label">${sCompanyName2}</dt>
									<dd class="value">
										<c:out value="${not empty deliveryAddress.companyName2 ? deliveryAddress.companyName2 : '-'}" /><br/>
										<c:choose><c:when test="${not empty deliveryAddress.companyName3}"><c:out value="${deliveryAddress.companyName3}" /></c:when><c:otherwise><br/></c:otherwise></c:choose>
									</dd>
									<dt class="label">${sStreet}</dt>
									<dd class="value"><c:out value="${deliveryAddress.line1} " /><c:out value="${deliveryAddress.line2}" /></dd>
								</dl>
							</li>
							<li class="entry">
								<dl>
									<dt class="label">${sName}</dt>
									<dd class="value">
										<c:out value="${deliveryAddress.title}" /><br/>
										<c:out value="${deliveryAddress.firstName}" />&nbsp;
										<c:out value="${deliveryAddress.lastName}" />
									</dd>
									<dt class="label">${sCity}</dt>
									<dd class="value"><c:out value="${deliveryAddress.postalCode}" />&nbsp;<c:out value="${deliveryAddress.town}" /></dd>
								</dl>
							</li>
							<li class="entry">
								<dl>
									
									<dt class="label">${sCountry}</dt>
									<dd class="value"><c:out value="${deliveryAddress.country.name}" /></dd>
								</dl>
							</li>
							<c:if test="${isExportShop eq false}">
								<li class="action"> 	<a href="edit-address/b2bshipping/${deliveryAddress.id}" class="more"><i></i></a> </li>
							</c:if>
							<c:if test="${deliveryAddress.defaultShipping eq false and fn:length(deliveryAddresses) > 1}">
								<div class="set-as-default ellipsis 2" title="${sSetDefault}">
									<a href="#"
									   data-action="/my-account/set-default-address?addressCode="
									   data-is-billing="false"
									   data-is-shipping="true"
									   data-address-id="${deliveryAddress.id}"
									   data-lightbox-title="${lightboxTitle}"
									   data-lightbox-message="${sSetAddressDefault}"
									   data-lightbox-btn-deny="${lightboxDenyButtonText}"
									   data-lightbox-show-confirm-button="true"
									   data-lightbox-btn-conf="${lightboxConfirmButtonText}"
									   class="btn btn-secondary btn-set-default set-as-default-button ellipsis">${sSetDefault}</a>
								</div>	
							</c:if>						
						</ul>
					</c:otherwise>
				</c:choose>
			</li>
		</c:forEach>
	</ul>
	<c:if test="${(fn:length(deliveryAddresses) > addressesPerPage)}">
		<div class="row row-show-more">
			<a class="show-more-link b-show-more" href="#" data-page-size="${pageSize}"><i></i> ${sButtonShowMore}</a>
		</div>
	</c:if>
</div>
