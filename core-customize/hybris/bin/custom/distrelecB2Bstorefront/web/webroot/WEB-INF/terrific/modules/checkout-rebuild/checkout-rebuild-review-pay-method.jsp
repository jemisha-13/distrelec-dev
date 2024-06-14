<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<spring:message code="text.remove" var="sRemove" />
<spring:message code="text.setDefault" var="sSetDefault" />

<div>
    <div class="mod-checkout-rebuild-block__content__item">
        <ul class="o-cr-radio-list is-payment-method-list">
            <c:forEach items="${paymentOptions}" var="paymentOption">
                <li class="o-cr-radio-list__item">
                    <div class="o-cr-radio-item">
                        <input id="paymentOption_${paymentOption.code}"
                               value="${paymentOption.code}"
                               class="o-cr-radio-item__radio js-payment-option" type="radio"
                               ${paymentOption.code eq selectedPaymentOption.code ? 'checked' : ''}
                               name="paymentOption">
                        <label class="o-cr-radio-item__radio-label"
                               for="paymentOption_${paymentOption.code}"></label>

                        <div class="o-cr-radio-item__content">
                            <div class="o-cr-radio-item__content__main">
                                <div class="o-cr-radio-item__info">
                                    <div class="o-cr-radio-item__radio-icon">
                                        <i class="material-icons-round radio_button_unchecked is-radio-unchecked">radio_button_unchecked</i>
                                        <i class="material-icons-round radio_button_unchecked is-radio-unchecked-hover">radio_button_unchecked</i>
                                        <i class="material-icons-round radio_button_checked is-radio-checked">radio_button_checked</i>
                                    </div>

                                    <div class="o-cr-radio-item__info-text">
                                        <div id="paymentOptionName_${paymentOption.code}" class="fw-b">${paymentOption.name}</div>
                                    </div>
                                </div>

                                <div id="paymentOptionIcon_${paymentOption.code}" class="o-cr-radio-item__logo fw-b">
                                    <c:choose>
                                        <c:when test="${paymentOption.code eq 'CreditCard'}">
                                            <c:if test="${siteUid eq 'distrelec_FR'}">
                                                <img class="is-cb" src="/_ui/all/media/img-modules/mod-checkout-rebuild-CartesBancaires-border.svg" alt="Cartes Bancaires">
                                            </c:if>
                                            <img class="is-mastercard" src="/_ui/all/media/img-modules/mod-checkout-rebuild-mastercard-border.svg" alt="Master card">
                                            <img class="is-visa" src="/_ui/all/media/img-modules/mod-checkout-rebuild-visa-border.svg" alt="Visa">
                                        </c:when>
                                        <c:when test="${paymentOption.code eq 'PayPal'}">
                                            <img class="is-paypal" src="/_ui/all/media/img-modules/mod-checkout-rebuild-paypal-border.svg" alt="Paypal">
                                        </c:when>
                                        <c:otherwise>
                                            <i class="material-icons-round assignment">assignment</i>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>

                            <c:if test="${paymentOption.code eq 'CreditCard'}">
                                <div class="o-cr-radio-item__content__special">
                                    <div class="row no-gutters">
                                        <c:forEach items="${ccPaymentInfos}" var="ccPaymentInfo">
                                            <c:if test="${ccPaymentInfo.saved}">
                                                <div class="col-sm-6 js-cc-item-col">
                                                    <c:set var="isCreditCardValid" value="${ccPaymentInfo.isValid}"/>

                                                    <div class="o-cr-radio-item__cc js-cc-item ${ccPaymentInfo.defaultPaymentInfo ? 'is-default-address' : ''} ${isCreditCardValid ? '' : 'is-expired'}">
                                                        <input id="creditCard_${ccPaymentInfo.id}"
                                                               value="${ccPaymentInfo.id}"
                                                               class="o-cr-radio-item__radio js-cc-selection" type="radio"
                                                            ${ccPaymentInfo.id eq selectedCcPaymentInfo.id ? 'checked="true"' : ''}
                                                               name="creditCard">
                                                        <c:choose>
                                                            <c:when test="${isCreditCardValid}">
                                                                <label class="o-cr-radio-item__radio-label"
                                                                       for="creditCard_${ccPaymentInfo.id}"></label>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <div id="creditCardExpired_${ccPaymentInfo.id}" class="o-cr-radio-item__cc__expired-label">
                                                                    <spring:message code="checkout.rebuild.review.pay.expired"/>
                                                                </div>
                                                            </c:otherwise>
                                                        </c:choose>

                                                        <div class="o-cr-radio-item__content">
                                                            <div class="o-cr-radio-item__cc__content">
                                                                <div id="creditCardLogo_${ccPaymentInfo.id}" class="o-cr-radio-item__cc__logo">
                                                                    <img src="/_ui/all/media/img-modules/mod-checkout-rebuild-${ccPaymentInfo.cardTypeData.code}-border.svg" alt="${ccPaymentInfo.cardTypeData.name}">
                                                                </div>

                                                                <c:set var="cardNumLength" value="${fn:length(ccPaymentInfo.cardNumber)}"/>
                                                                <div id="creditCardInfo_${ccPaymentInfo.id}" class="o-cr-radio-item__cc__info">
                                                                    &#x000B7;&#x000B7;&#x000B7;&#x000B7;&nbsp;${fn:substring(ccPaymentInfo.cardNumber, cardNumLength - 3, cardNumLength)}&nbsp;&nbsp;&nbsp;<spring:message
                                                                        code="checkout.rebuild.review.pay.expired.short"/>&nbsp;${ccPaymentInfo.expiryMonth}/${ccPaymentInfo.expiryYear}
                                                                </div>

                                                                <div class="o-cr-radio-item__cc__cta">
                                                                    <c:if test="${isCreditCardValid}">
                                                                        <button id="creditCardSetDefault_${ccPaymentInfo.id}" class="ux-link is-set-default js-cc-set-as-default" type="button">${sSetDefault}</button>
                                                                    </c:if>
                                                                    <button id="creditCardRemove_${ccPaymentInfo.id}" class="ux-link js-cc-remove" type="button">${sRemove}</button>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </c:if>
                                        </c:forEach>

                                        <div class="col-sm-6 js-cc-item-col">
                                            <div class="o-cr-radio-item__cc is-add-new-card js-cc-item">
                                                <input id="creditCard_new-card"
                                                       value="NEW_CARD"
                                                       ${selectedCcPaymentInfo eq null ? 'checked="true"' : ''}
                                                       class="o-cr-radio-item__radio js-cc-selection" type="radio"
                                                       name="creditCard">
                                                <label class="o-cr-radio-item__radio-label"
                                                       for="creditCard_new-card"></label>

                                                <div class="o-cr-radio-item__content">
                                                    <div class="o-cr-radio-item__cc__content">
                                                        <div>
                                                            <i class="material-icons-round credit_card">credit_card</i>
                                                        </div>

                                                        <div id="creditCardAddNewCard"><spring:message code="checkoutpaymentoptionslist.creditCard.new"/></div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </li>
            </c:forEach>

            <c:if test="${canRequestInvoicePaymentMode}">
                <li class="o-cr-radio-list__item">
                    <div class="js-future-invoicing">
                        <c:if test="${not invoicePaymentModeRequested}">
                            <div class="js-future-invoicing-request">
                                <mod:banner-ux type="info" icon="material-icons-round" iconCode="&#xe88e;">
                                    <strong id="requestInvoicePaymentModeInfo1"><spring:message code="checkout.rebuild.review.pay.did.you.know"/></strong>

                                    <div id="requestInvoicePaymentModeInfo2">
                                        <spring:message code="checkout.rebuild.review.pay.setup.future.orders"/>&nbsp;<button
                                                    id="requestInvoicePaymentModeRequestButton"
                                                    class="ux-link ux-link--raw js-future-invoicing-request-link" type="button"><spring:message code="checkout.rebuild.review.pay.request.now"/></button>
                                    </div>
                                </mod:banner-ux>
                            </div>
                        </c:if>

                        <div class="js-future-invoicing-requested" style="${not invoicePaymentModeRequested ? 'display: none;' : ''}">
                            <mod:banner-ux type="success" icon="material-icons-round" iconCode="&#xe88e;">
                                <strong id="requestInvoicePaymentModePendingInfo1"><spring:message code="checkoutpaymentoptionslist.requesttopaywith.pending.after"/></strong>

                                <div id="requestInvoicePaymentModePendingInfo2">
                                    <spring:message code="checkoutpaymentoptionslist.requesttopaywith.pending.review"/>
                                </div>
                            </mod:banner-ux>
                        </div>
                    </div>
                </li>
            </c:if>
        </ul>
    </div>

    <c:if test="${showOrderReference}">
        <div class="mod-checkout-rebuild-block__content__item">
            <div class="row">
                <div class="col-12">
                    <formUtil:ux-formInput inputId="orderReferenceField"
                                           inputName="orderReference"
                                           groupClass="mb-0"
                                           inputClass="js-order-reference js-optional-field"
                                           labelKey="checkoutordersummarycostcenterbox.projectNumber"
                                           groupInfoKey="checkoutordersummarycostcenterbox.help"
                                           optionalLabel="true"
                                           inputValue="${cartData.projectNumber}"/>
                </div>
            </div>
        </div>
    </c:if>
</div>
