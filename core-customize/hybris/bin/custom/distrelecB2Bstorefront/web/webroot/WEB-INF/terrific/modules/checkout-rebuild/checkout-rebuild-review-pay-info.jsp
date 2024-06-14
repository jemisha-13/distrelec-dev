<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<spring:message code="text.store.dateformat.datepicker.selection" var="sDateFormat"/>

<fmt:formatDate var="scheduleDate" value="${cartData.reqDeliveryDateHeaderLevel}" dateStyle="medium"/>

<c:set var="customerType" value="${cartData.b2bCustomerData.customerType}"/>
<c:set var="isCustomerB2B" value="${customerType eq 'B2B_KEY_ACCOUNT' or customerType eq 'B2B'}"/>
<c:set var="isKeyAccountUser" value="${customerType eq 'B2B_KEY_ACCOUNT'}"/>
<c:set var="isSameBillingAndDeliveryAddress" value="${cartData.billingAddress.id eq cartData.deliveryAddress.id}"/>
<c:set var="isPickup" value="${cartData.deliveryMode.code eq 'SAP_A1'}"/>

<div class="mod-checkout-rebuild-block__content__item">
    <h3 id="reviewPayDeliveryOptionsTitle" class="mod-checkout-rebuild-block__title"><spring:message code="text.account.deliveryOptions"/></h3>

    <div class="o-cr-editable-form__edit">
        <button id="reviewPayDeliveryOptionsEditButton" class="ux-link" onclick="location.href='/checkout/delivery'">
            <spring:message code="text.edit"/><i class="material-icons-round mode_edit">&#xe254;</i>
        </button>
    </div>

    <ul class="o-cr-editable-form__info-list is-review-pay-delivery-options">
        <li id="reviewPayDeliveryOptionsModeAndScheduleInfo"><strong>${cartData.deliveryMode.translation}<c:if test="${not empty scheduleDate}">&nbsp;-&nbsp;<spring:message code="checkout.rebuild.review.pay.scheduled.delivery"/></c:if></strong></li>

        <c:choose>
            <c:when test="${isPickup and pickupWarehouses[0].code eq '7374'}">
                <c:choose>
                    <c:when test="${pickupWarehouses[0].availableForImmediatePickup}">
                        <li id="reviewPayDeliveryOptionsImmediatePickupAvailabile">
                            <spring:message code="checkout.rebuild.pickup.available.warehouse.info" arguments="${pickupWarehouses[0].pickupDate}"/>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li id="reviewPayDeliveryOptionsImmediatePickupNotAvailabile">
                            <spring:message code="checkout.rebuild.pickup.not.available.warehouse.info" arguments="${pickupWarehouses[0].pickupDate}"/>
                        </li>
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:when test="${not empty scheduleDate}">
                <li id="reviewPayDeliveryOptionsArriving"><spring:message code="checkout.rebuild.review.pay.arrives"/>&nbsp;${scheduleDate}</li>
            </c:when>
            <c:otherwise>
                <li id="reviewPayDeliveryOptionsDescription">${cartData.deliveryMode.description}</li>
            </c:otherwise>
        </c:choose>
    </ul>
</div>

<div class="mod-checkout-rebuild-block__content__item">
    <h3 id="reviewPayBillingDeliveryTitle" class="mod-checkout-rebuild-block__title"><spring:message code="${isSameBillingAndDeliveryAddress and not isPickup ? 'checkout.rebuild.billing.and.delivery' : 'checkout.rebuild.billing.details'}"/></h3>

    <c:if test="${showBillingEdit}">
        <div class="o-cr-editable-form__edit">
            <button id="reviewPayBillingDeliveryEditButton" class="ux-link" onclick="location.href='/checkout/delivery?edit=billing'">
                <spring:message code="text.edit"/><i class="material-icons-round mode_edit">&#xe254;</i>
            </button>
        </div>
    </c:if>

    <ul class="o-cr-editable-form__info-list is-review-pay-billing-details">
        <c:choose>
            <c:when test="${isCustomerB2B}">
                <li id="reviewPayBillingDeliveryCompany"><c:out value="${cartData.billingAddress.companyName}"/></li>

                <c:if test="${cartData.billingAddress.companyName2}">
                    <li id="reviewPayBillingDeliveryCompany2"><c:out value="${cartData.billingAddress.companyName2}"/></li>
                </c:if>

                <c:if test="${not empty cartData.billingAddress.line1}">
                    <li id="reviewPayBillingDeliveryAddress"><c:out value="${cartData.billingAddress.line1}"/><c:if test="${not empty cartData.billingAddress.line2}">&nbsp;<c:out value="${cartData.billingAddress.line2}"/></c:if></li>
                </c:if>

                <c:if test="${not empty cartData.billingAddress.postalCode}">
                    <li id="reviewPayBillingDeliveryPostalCodeAndTown"><c:out value="${cartData.billingAddress.postalCode}"/>&nbsp;<c:out value="${cartData.billingAddress.town}"/>,</li>
                </c:if>

                <c:if test="${not empty cartData.billingAddress.country.name}">
                    <li id="reviewPayBillingDeliveryRegionAndCountry"><c:if test="${not empty cartData.billingAddress.region.name}"><c:out value="${cartData.billingAddress.region.name}"/>,&nbsp;</c:if><c:out value="${cartData.billingAddress.country.name}"/></li>
                </c:if>

                <c:choose>
                    <c:when test="${not empty cartData.billingAddress.cellphone}">
                        <li id="reviewPayBillingDeliveryPhone"><c:out value="${cartData.billingAddress.cellphone}"/></li>
                    </c:when>
                    <c:otherwise>
                        <li id="reviewPayBillingDeliveryPhone"><c:out value="${cartData.billingAddress.phone1}"/></li>
                    </c:otherwise>
                </c:choose>
            </c:when>

            <c:otherwise>
                <li id="reviewPayBillingDeliveryTitle"><c:out value="${cartData.billingAddress.title}"/>&nbsp;<c:out value="${cartData.billingAddress.firstName}"/>&nbsp;<c:out value="${cartData.billingAddress.lastName}"/></li>

                <c:if test="${not empty cartData.billingAddress.line1}">
                    <li id="reviewPayBillingDeliveryAddress"><c:out value="${cartData.billingAddress.line1}"/><c:if test="${not empty cartData.billingAddress.line2}">&nbsp;<c:out value="${cartData.billingAddress.line2}"/></c:if></li>
                </c:if>

                <c:if test="${not empty cartData.billingAddress.postalCode}">
                    <li id="reviewPayBillingDeliveryPostalCodeAndTown"><c:out value="${cartData.billingAddress.postalCode}"/>&nbsp;<c:out value="${cartData.billingAddress.town}"/>,</li>
                </c:if>

                <c:if test="${not empty cartData.billingAddress.country.name}">
                    <li id="reviewPayBillingDeliveryRegionAndCountry"><c:if test="${not empty cartData.billingAddress.region.name}"><c:out value="${cartData.billingAddress.region.name}"/>,&nbsp;</c:if><c:out value="${cartData.billingAddress.country.name}"/></li>
                </c:if>

                <c:if test="${customerType eq 'B2C' or customerType eq 'GUEST'}">
                    <c:choose>
                        <c:when test="${not empty cartData.billingAddress.cellphone}">
                            <li id="reviewPayBillingDeliveryPhone"><c:out value="${cartData.billingAddress.cellphone}"/></li>
                        </c:when>
                        <c:otherwise>
                            <li id="reviewPayBillingDeliveryPhone"><c:out value="${cartData.billingAddress.phone1}"/></li>
                        </c:otherwise>
                    </c:choose>
                </c:if>

                <c:if test="${customerType eq 'B2E'}">
                    <c:if test="${not empty cartData.billingAddress.phone1}">
                        <li id="reviewPayBillingDeliveryPhone"><c:out value="${cartData.billingAddress.phone1}"/></li>
                    </c:if>
                </c:if>
            </c:otherwise>
        </c:choose>
    </ul>
</div>

<c:choose>
    <c:when test="${isPickup}">
        <div class="mod-checkout-rebuild-block__content__item">
            <h3 id="reviewPayPickupTitle" class="mod-checkout-rebuild-block__title is-full"><spring:message code="orderdetailsection.pickupLocation"/></h3>

            <ul class="o-cr-editable-form__info-list is-review-pay-delivery-details">
                <li id="reviewPayPickupName"><c:out value="${pickupWarehouses[0].name}"/></li>

                <c:if test="${not empty pickupWarehouses[0].streetName}">
                    <li id="reviewPayPickupStreetNameAndNumber"><c:out value="${pickupWarehouses[0].streetName}"/>&nbsp;<c:out value="${pickupWarehouses[0].streetNumber}"/></li>
                </c:if>

                <c:if test="${not empty pickupWarehouses[0].postalCode}">
                    <li id="reviewPayPickupPostalCodeAndTown"><c:out value="${pickupWarehouses[0].postalCode}"/>&nbsp;<c:out value="${pickupWarehouses[0].town}"/>,</li>
                </c:if>

                <c:if test="${not empty pickupWarehouses[0].phone}">
                    <li id="reviewPayPickupPhone"><c:out value="${pickupWarehouses[0].phone}"/></li>
                </c:if>

                <c:if test="${not empty pickupWarehouses[0].openingsHourMoFr}">
                    <li id="reviewPayPickupOpeningHrs"><spring:message code="checkout.rebuild.pickup.opening.hours"/>&nbsp;<spring:message
                            code="checkout.rebuild.pickup.opening.hours.mon.fri"/>&nbsp;<c:out
                            value="${pickupWarehouses[0].openingsHourMoFr}"/><c:if
                            test="${not empty pickupWarehouses[0].openingsHourSa}">&nbsp;/&nbsp;<c:out
                            value="${pickupWarehouses[0].openingsHourSa}"/></c:if></li>
                </c:if>
            </ul>
        </div>
    </c:when>
    <c:otherwise>
        <c:if test="${not isSameBillingAndDeliveryAddress and (isCustomerB2B or customerType eq 'B2C')}">
            <div class="mod-checkout-rebuild-block__content__item">
                <h3 id="reviewPayDeliveryBlockTitle" class="mod-checkout-rebuild-block__title"><spring:message code="checkout.rebuild.delivery.details"/></h3>

                <div class="o-cr-editable-form__edit">
                    <button id="reviewPayDeliveryEditButton" class="ux-link" onclick="location.href='/checkout/delivery?edit=delivery'">
                        <spring:message code="text.edit"/><i class="material-icons-round mode_edit">&#xe254;</i>
                    </button>
                </div>

                <ul class="o-cr-editable-form__info-list is-review-pay-delivery-details">
                    <c:choose>
                        <c:when test="${isCustomerB2B}">
                            <li id="reviewPayDeliveryCompany"><c:out value="${cartData.deliveryAddress.companyName}"/></li>

                            <c:if test="${cartData.deliveryAddress.companyName2}">
                                <li id="reviewPayDeliveryCompany2"><c:out value="${cartData.deliveryAddress.companyName2}"/></li>
                            </c:if>

                            <c:if test="${not empty cartData.deliveryAddress.line1}">
                                <li id="reviewPayDeliveryAddress"><c:out value="${cartData.deliveryAddress.line1}"/><c:if test="${not empty cartData.deliveryAddress.line2}">&nbsp;<c:out value="${cartData.deliveryAddress.line2}"/></c:if></li>
                            </c:if>

                            <c:if test="${not empty cartData.deliveryAddress.postalCode}">
                                <li id="reviewPayDeliveryPostalCodeAndTown"><c:out value="${cartData.deliveryAddress.postalCode}"/>&nbsp;<c:out value="${cartData.deliveryAddress.town}"/>,</li>
                            </c:if>

                            <c:if test="${not empty cartData.deliveryAddress.country.name}">
                                <li id="reviewPayDeliveryRegionAndCountry"><c:if test="${not empty cartData.deliveryAddress.region.name}"><c:out value="${cartData.deliveryAddress.region.name}"/>,&nbsp;</c:if><c:out value="${cartData.deliveryAddress.country.name}"/></li>
                            </c:if>

                            <c:choose>
                                <c:when test="${not empty cartData.deliveryAddress.cellphone}">
                                    <li id="reviewPayDeliveryPhone"><c:out value="${cartData.deliveryAddress.cellphone}"/></li>
                                </c:when>
                                <c:otherwise>
                                    <li id="reviewPayDeliveryPhone"><c:out value="${cartData.deliveryAddress.phone1}"/></li>
                                </c:otherwise>
                            </c:choose>
                        </c:when>

                        <c:otherwise>
                            <li id="reviewPayDeliveryTitle"><c:out value="${cartData.deliveryAddress.title}"/>&nbsp;<c:out value="${cartData.deliveryAddress.firstName}"/>&nbsp;<c:out value="${cartData.deliveryAddress.lastName}"/></li>

                            <c:if test="${not empty cartData.deliveryAddress.line1}">
                                <li id="reviewPayDeliveryAddress"><c:out value="${cartData.deliveryAddress.line1}"/><c:if test="${not empty cartData.deliveryAddress.line2}">&nbsp;<c:out value="${cartData.deliveryAddress.line2}"/></c:if></li>
                            </c:if>

                            <c:if test="${not empty cartData.deliveryAddress.postalCode}">
                                <li id="reviewPayDeliveryPostalCodeAndTown"><c:out value="${cartData.deliveryAddress.postalCode}"/>&nbsp;<c:out value="${cartData.deliveryAddress.town}"/>,</li>
                            </c:if>

                            <c:if test="${not empty cartData.deliveryAddress.country.name}">
                                <li id="reviewPayDeliveryRegionAndCountry"><c:if test="${not empty cartData.deliveryAddress.region.name}"><c:out value="${cartData.deliveryAddress.region.name}"/>,&nbsp;</c:if><c:out value="${cartData.deliveryAddress.country.name}"/></li>
                            </c:if>

                            <c:if test="${customerType eq 'B2C' or customerType eq 'GUEST'}">
                                <c:choose>
                                    <c:when test="${not empty cartData.deliveryAddress.cellphone}">
                                        <li id="reviewPayDeliveryPhone"><c:out value="${cartData.deliveryAddress.cellphone}"/></li>
                                    </c:when>
                                    <c:otherwise>
                                        <li id="reviewPayDeliveryPhone"><c:out value="${cartData.deliveryAddress.phone1}"/></li>
                                    </c:otherwise>
                                </c:choose>
                            </c:if>

                            <c:if test="${customerType eq 'B2E'}">
                                <c:if test="${not empty cartData.deliveryAddress.phone1}">
                                    <li id="reviewPayDeliveryPhone"><c:out value="${cartData.deliveryAddress.phone1}"/></li>
                                </c:if>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </div>
        </c:if>
    </c:otherwise>
</c:choose>
