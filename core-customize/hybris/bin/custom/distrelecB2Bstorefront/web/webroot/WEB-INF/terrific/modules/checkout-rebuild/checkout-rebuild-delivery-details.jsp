<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="isB2B" value="${customerType eq 'B2B' || customerType eq 'B2B_KEY_ACCOUNT'}"/>
<spring:message code="text.edit" var="sEdit" />
<spring:message code="text.remove" var="sRemove" />
<spring:message code="text.setDefault" var="sSetDefault" />

<input class="js-delivery-address-count" type="hidden" value="${fn:length(shippingAddresses)}">

<ul class="o-cr-radio-list is-address-list js-da-list" data-customer-type="${customerType}">
    <c:forEach items="${shippingAddresses}" var="currentShippingAddress">
        <c:set var="addressIsValid" value="${currentShippingAddress.isValid}"/>
        <c:set var="addressIsSelected" value="${currentShippingAddress.id eq shippingAddress.id}"/>

        <li class="o-cr-radio-list__item js-da-list-item ${addressIsSelected ? 'is-selected-address js-is-selected-address' : ''} ${currentShippingAddress.defaultShipping ? 'is-default-address' : ''}"
                data-shipping-id="${currentShippingAddress.id}">
            <div class="o-cr-radio-item">
                <input id="shippingAddressItem_${currentShippingAddress.id}"
                       value="${currentShippingAddress.id}"
                       data-address-type="shipping"
                       data-address-is-valid="${addressIsValid}"
                       class="o-cr-radio-item__radio js-multiple-address-radio js-da-input" type="radio"
                        ${addressIsSelected ? 'checked' : ''}
                       name="shippingAddressItem">
                <label class="o-cr-radio-item__radio-label"
                       for="shippingAddressItem_${currentShippingAddress.id}"></label>

                <div class="o-cr-radio-item__content">
                    <div class="o-cr-radio-item__content__main">
                        <div class="o-cr-radio-item__info">
                            <div class="o-cr-radio-item__radio-icon">
                                <i class="material-icons-round radio_button_unchecked is-radio-unchecked">radio_button_unchecked</i>
                                <i class="material-icons-round radio_button_unchecked is-radio-unchecked-hover">radio_button_unchecked</i>
                                <i class="material-icons-round radio_button_checked is-radio-checked">radio_button_checked</i>
                            </div>

                            <div class="o-cr-radio-item__status-icon">
                                <i class="material-icons-round location_on">location_on</i>
                            </div>

                            <div id="checkoutDeliveryAddressInfo${currentShippingAddress.id}" class="o-cr-radio-item__info-text fw-m js-da-list-item-info">
                                <c:choose><c:when test="${isB2B}"><c:out
                                        value="${currentShippingAddress.companyName}"/>, <c:if
                                        test="${not empty currentShippingAddress.companyName2}"><c:out
                                        value="${currentShippingAddress.companyName2}"/>, </c:if></c:when><c:otherwise><c:out
                                        value="${currentShippingAddress.title}"/> <c:out
                                        value="${currentShippingAddress.firstName}"/> <c:out
                                        value="${currentShippingAddress.lastName}"/>, </c:otherwise></c:choose><c:out
                                    value="${currentShippingAddress.line1}"/><c:if test="${not empty currentShippingAddress.line2}"> <c:out
                                    value="${currentShippingAddress.line2}"/></c:if>,<br>
                                <c:out value="${currentShippingAddress.postalCode}"/> <c:out
                                    value="${currentShippingAddress.town}"/>, <c:if
                                    test="${not empty currentShippingAddress.region}"><c:out
                                    value="${currentShippingAddress.region.name}"/> </c:if><c:out
                                    value="${currentShippingAddress.country.name}"/> <c:choose><c:when
                                    test="${not empty currentShippingAddress.cellphone}"><c:out
                                    value="${currentShippingAddress.cellphone}"/></c:when><c:otherwise><c:out
                                    value="${currentShippingAddress.phone1}"/></c:otherwise></c:choose>
                            </div>
                        </div>

                        <div class="o-cr-radio-item__cta">
                            <div>
                                <button id="checkoutDeliveryAddressSetDefault${currentShippingAddress.id}" class="ux-link ux-link--clean is-set-default js-da-set-default">${sSetDefault}</button>
                            </div>

                            <div>
                                <c:set var="thisAddressIsBilling" value="${multipleBillingAddresses and currentShippingAddress.billingAddress}"/>
                                <c:if test="${not thisAddressIsBilling}">
                                    <button id="checkoutDeliveryAddressEditButton${currentShippingAddress.id}" class="ux-link js-da-edit">${sEdit}</button>
                                    <button id="checkoutDeliveryAddressRemoveButton${currentShippingAddress.id}" class="ux-link js-da-remove">${sRemove}</button>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </li>

        <li class="o-cr-radio-list__item is-edit-form js-da-list-item-form ${not addressIsValid and addressIsSelected ? 'hide-edit' : ''}" data-shipping-id="${currentShippingAddress.id}">
            <mod:checkout-rebuild-delivery-form template="${customerType}"
                                                skin="${customerType}"
                                                shippingItem="${currentShippingAddress}"/>
        </li>
    </c:forEach>
</ul>

<div class="o-cr-editable-form is-delivery ${fn:length(shippingAddresses) gt 0 ? 'is-info-mode is-cancellable' : ''} js-cr-editableform">
    <div class="o-cr-editable-form__form js-cr-editableform-form">
        <mod:checkout-rebuild-delivery-form template="${customerType}" skin="${customerType}"/>
    </div>

    <div class="o-cr-editable-form__info js-cr-editableform-info">
        <div class="o-cr-editable-form__edit">
            <button id="deliveryAddressAddNewButton" class="ux-link js-cr-editableform-edit">
                <spring:message code="checkout.rebuild.delivery.address.new"/>
            </button>
        </div>
    </div>
</div>

<script id="tmpl-delivery-details-address-item-radio-info" type="text/x-template-dotjs">
    <c:choose><c:when test="${isB2B}">{{= it.companyName}}, {{? it.companyName2}}{{= it.companyName2}}, {{?}}</c:when><c:otherwise>{{= it.title}} {{= it.firstName}} {{= it.lastName}}, </c:otherwise></c:choose>{{= it.line1}}{{? it.line2}} {{= it.line2}}{{?}},<br>
    {{= it.postalCode}} {{= it.town}}, {{? it.region}}{{= it.region.name}}, {{?}}{{= it.country.name}} {{? it.cellphone}}{{= it.cellphone}}{{??}}{{= it.phone1}}{{?}}
</script>

<script id="tmpl-delivery-details-address-item" type="text/x-template-dotjs">
    <li class="o-cr-radio-list__item js-da-list-item" data-shipping-id="{{= it.id}}">
        <div class="o-cr-radio-item">
            <input id="shippingAddressItem_{{= it.id}}"
                   value="{{= it.id}}"
                   data-address-type="shipping"
                   data-address-is-valid="true"
                   class="o-cr-radio-item__radio js-multiple-address-radio js-da-input" type="radio"
                   name="shippingAddressItem">
            <label class="o-cr-radio-item__radio-label"
                   for="shippingAddressItem_{{= it.id}}"></label>

            <div class="o-cr-radio-item__content">
                <div class="o-cr-radio-item__content__main">
                    <div class="o-cr-radio-item__info">
                        <div class="o-cr-radio-item__radio-icon">
                            <i class="material-icons-round radio_button_unchecked is-radio-unchecked" style="opacity: 1;">radio_button_unchecked</i>
                            <i class="material-icons-round radio_button_unchecked is-radio-unchecked-hover" style="opacity: 1;">radio_button_unchecked</i>
                            <i class="material-icons-round radio_button_checked is-radio-checked" style="opacity: 1;">radio_button_checked</i>
                        </div>

                        <div class="o-cr-radio-item__status-icon">
                            <i class="material-icons-round location_on" style="opacity: 1;">location_on</i>
                        </div>

                        <div id="checkoutDeliveryAddressInfo{{= it.id}}" class="o-cr-radio-item__info-text fw-m js-da-list-item-info">
                            <c:choose><c:when test="${isB2B}">{{= it.companyName}}, {{? it.companyName2}}{{= it.companyName2}}, {{?}}</c:when><c:otherwise>{{= it.title}} {{= it.firstName}} {{= it.lastName}}, </c:otherwise></c:choose>{{= it.line1}}{{? it.line2}} {{= it.line2}}{{?}},<br>
                            {{= it.postalCode}} {{= it.town}}, {{? it.region}}{{= it.region.name}}, {{?}}{{= it.country.name}} {{? it.cellphone}}{{= it.cellphone}}{{??}}{{= it.phone1}}{{?}}
                        </div>
                    </div>

                    <div class="o-cr-radio-item__cta">
                        <div>
                            <button id="checkoutDeliveryAddressSetDefault{{= it.id}}" class="ux-link ux-link--clean is-set-default js-da-set-default">${sSetDefault}</button>
                        </div>

                        <div>
                            <button id="checkoutDeliveryAddressEditButton{{= it.id}}" class="ux-link js-da-edit">${sEdit}</button>
                            <button id="checkoutDeliveryAddressRemoveButton{{= it.id}}" class="ux-link js-da-remove">${sRemove}</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </li>

    <li class="o-cr-radio-list__item is-edit-form js-da-list-item-form" data-shipping-id="{{= it.id}}">
        <mod:checkout-rebuild-delivery-form template="${customerType}" skin="${customerType}"/>
    </li>
</script>

<script id="tmpl-delivery-details-address-item-remove" type="text/x-template-dotjs">
    <div class="o-cr-radio-item-remove js-da-list-item-remove" data-shipping-id="{{= it.id}}" style="display: none;">
        <mod:banner-ux type="danger" icon="fas fa-exclamation-triangle">
            <div id="checkoutDeliveryAddressDeleteAddressTxt">
                <spring:message code="checkout.rebuild.delivery.address.remove.info" />
            </div>

            <div class="o-cr-radio-item-remove__ctas">
                <button id="checkoutDeliveryAddressDeleteAddressConfirm" class="ux-link is-confirm js-da-list-item-remove-confirm" type="button">
                    <spring:message code="text.confirm" />
                </button>

                <button id="checkoutDeliveryAddressDeleteAddressClose" class="ux-link ux-link--clean is-cancel js-da-list-item-remove-cancel" type="button">
                    <i class="material-icons-round close" style="opacity: 1;">&#xe5cd;</i>
                </button>
            </div>
        </mod:banner-ux>
    </div>
</script>
