<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="formatArticle" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="terrificFormat" tagdir="/WEB-INF/tags/shared/terrific/format" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:if test="${completeDeliveryPossible}">
    <mod:banner-ux type="warning" icon="material-icons-round" iconCode="&#xe000;">
        <div>
            <strong id="checkoutCombineMessage1"><spring:message
                    code="checkout.rebuild.delivery.combine.message1"/></strong>
        </div>
        <div>
            <formUtil:ux-formCheckbox idKey="combineAllItemsIntoOne"
                                      inputCSS="js-combine-all-items"
                                      isChecked="${cartData.completeDelivery}"
                                      labelCSS="fw-r mt-1"
                                      labelKey="checkout.rebuild.delivery.combine.message2"
                                      name="${combineAllItemsIntoOne}"/>
        </div>
    </mod:banner-ux>
</c:if>

<ul class="o-cr-radio-list">
    <c:forEach items="${deliveryModes}" var="deliveryMode">
        <c:set var="validForSchedule" value="${!fn:contains(invalidModesForScheduleDelivery, deliveryMode.code)}" />
        <c:if test="${deliveryMode.selected}">
            <c:set var="disableSchedule" value="${not validForSchedule}"/>
        </c:if>

        <li class="o-cr-radio-list__item">
            <div class="o-cr-radio-item">
                <input id="deliveryOption_${deliveryMode.code}"
                       value="${deliveryMode.code}"
                       data-valid-schedule="${validForSchedule}"
                       data-warehouse-code="${deliveryMode.code eq 'SAP_A1' ? pickupWarehouses[0].code : ''}"
                       class="o-cr-radio-item__radio js-cr-delivery-option" type="radio"
                       name="deliveryOption" ${deliveryMode.selected ? 'checked' : ''}>
                <label class="o-cr-radio-item__radio-label"
                       for="deliveryOption_${deliveryMode.code}"></label>

                <div class="o-cr-radio-item__content">
                    <div class="o-cr-radio-item__content__main">
                        <div class="o-cr-radio-item__info">
                            <div class="o-cr-radio-item__radio-icon">
                                <i class="material-icons-round radio_button_unchecked is-radio-unchecked">radio_button_unchecked</i>
                                <i class="material-icons-round radio_button_unchecked is-radio-unchecked-hover">radio_button_unchecked</i>
                                <i class="material-icons-round radio_button_checked is-radio-checked">radio_button_checked</i>
                            </div>

                            <div class="o-cr-radio-item__status-icon">
                                <c:choose>
                                    <%-- Economy --%>
                                    <c:when test="${deliveryMode.code eq 'SAP_E1'}"><i class="material-icons-round local_shipping">&#xe558;</i></c:when>
                                    <%-- Standard, Normal --%>
                                    <c:when test="${deliveryMode.code eq 'SAP_N1'}"><i class="material-icons-round local_shipping">&#xe558;</i></c:when>
                                    <%-- Express --%>
                                    <c:when test="${deliveryMode.code eq 'SAP_X4'}"><i class="fa fa-shipping-fast"></i></c:when>
                                    <%-- Express on demand --%>
                                    <c:when test="${deliveryMode.code eq 'SAP_X1'}"><i class="fa fa-shipping-fast"></i></c:when>
                                    <%-- Economy to pick up place --%>
                                    <c:when test="${deliveryMode.code eq 'SAP_E2'}"><i class="material-icons-round store">&#xe8d1;</i></c:when>
                                    <%-- Normal to pick up place --%>
                                    <c:when test="${deliveryMode.code eq 'SAP_N2'}"><i class="material-icons-round store">&#xe8d1;</i></c:when>
                                    <%-- Pick up at store --%>
                                    <c:when test="${deliveryMode.code eq 'SAP_A1'}"><i class="material-icons-round store">&#xe8d1;</i></c:when>
                                </c:choose>
                            </div>

                            <div class="o-cr-radio-item__info-text">
                                <div id="deliveryOptionTitle${deliveryMode.code}" class="fw-b">${deliveryMode.translation}</div>
                                <c:choose>
                                    <c:when test="${deliveryMode.code eq 'SAP_A1'}">
                                        <small id="deliveryOptionInfo${deliveryMode.code}">
                                            <c:out value="${pickupWarehouses[0].streetName}"/>&nbsp;<c:out value="${pickupWarehouses[0].streetNumber}"/>,&nbsp;<c:out value="${pickupWarehouses[0].town}"/>
                                        </small>
                                    </c:when>
                                    <c:otherwise>
                                        <small id="deliveryOptionInfo${deliveryMode.code}">${deliveryMode.description}</small>
                                    </c:otherwise>
                                </c:choose>

                                <c:choose>
                                    <c:when test="${deliveryMode.code eq 'SAP_A1'}">
                                        <div class="o-cr-radio-item__content__special">
                                            <div class="row pb-2">
                                                <div id="deliveryOptionPickupPhoneLabel" class="col-md-6 fw-b">
                                                    <spring:message code="checkout.address.phone"/>:
                                                </div>

                                                <div id="deliveryOptionPickupPhone" class="col-md-6">${pickupWarehouses[0].phone}</div>
                                            </div>

                                            <div class="row">
                                                <div id="deliveryOptionPickupCollectionLabel" class="col-md-6 fw-b">
                                                    <spring:message code="checkout.rebuild.delivery.pickup.collection"/>
                                                </div>

                                                <div class="col-md-6">
                                                    <div id="deliveryOptionPickupMonFriLabel">
                                                        <spring:message code="checkout.rebuild.pickup.opening.hours.mon.fri"/>
                                                    </div>

                                                    <div id="deliveryOptionPickupMonFriHours">${pickupWarehouses[0].openingsHourMoFr}</div>

                                                    <c:if test="${not empty pickupWarehouses[0].openingsHourSa}">
                                                        <div id="deliveryOptionPickupSatLabel" class="mt-2">
                                                            <spring:message code="checkout.rebuild.pickup.opening.hours.sat"/>
                                                        </div>
                                                        <div id="deliveryOptionPickupSatHours">${pickupWarehouses[0].openingsHourSa}</div>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </div>
                                    </c:when>
                                </c:choose>
                            </div>
                        </div>

                        <c:if test="${showDeliveryModePrice}">
                            <div id="deliveryOptionPrice_${deliveryMode.code}" class="o-cr-radio-item__price fw-b">
                                <c:choose>
                                    <c:when test="${deliveryMode.shippingCost.value gt 0}">
                                        <terrificFormat:price format="simple"
                                                              priceData="${deliveryMode.shippingCost}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <spring:message code="checkout.summary.deliveryCost.free"/>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </c:if>
                    </div>

                    <c:choose>
                        <c:when test="${deliveryMode.code eq 'SAP_A1' and pickupWarehouses[0].code eq '7374'}">
                            <div class="o-cr-radio-item__content__special">
                                <mod:banner-ux type="info" icon="material-icons-round" iconCode="&#xe88e;">
                                    <c:choose>
                                        <c:when test="${pickupWarehouses[0].availableForImmediatePickup}">
                                            <div id="deliveryOptionPickupInfo_immediate">
                                                <spring:message code="checkout.rebuild.pickup.available.warehouse.info" arguments="${pickupWarehouses[0].pickupDate}"/>
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <div id="deliveryOptionPickupInfo_onDate">
                                                <spring:message code="checkout.rebuild.pickup.not.available.warehouse.info" arguments="${pickupWarehouses[0].pickupDate}"/>
                                            </div>

                                            <ul class="ul-list">
                                                <li id="deliveryOptionPickupInfo_onDateItem1">
                                                    <spring:message code="checkout.rebuild.pickup.not.available.warehouse.info2" arguments="${pickupWarehouses[0].pickupDate}"/>
                                                </li>

                                                <li id="deliveryOptionPickupInfo_onDateItem2">
                                                    <spring:message code="checkout.rebuild.pickup.not.available.warehouse.info3"/>
                                                </li>
                                            </ul>
                                        </c:otherwise>
                                    </c:choose>
                                </mod:banner-ux>
                            </div>
                        </c:when>

                        <c:when test="${deliveryMode.code eq 'SAP_N2'}">
                            <c:if test="${siteUid eq 'distrelec_FR'}">
                                <div class="o-cr-radio-item__content__special">
                                    <mod:banner-ux type="info" icon="material-icons-round" iconCode="&#xe88e;">
                                        <div id="deliveryOptionInfoBanner_${deliveryMode.code}"><spring:message code="checkoutdeliveryoptionslist.shipUsing.express.note"/></div>
                                    </mod:banner-ux>
                                </div>
                            </c:if>
                        </c:when>

                        <c:when test="${deliveryMode.code eq 'SAP_X4'}">
                            <c:if test="${siteUid eq 'distrelec_CH'}">
                                <div class="o-cr-radio-item__content__special">
                                    <mod:banner-ux type="info" icon="material-icons-round" iconCode="&#xe88e;">
                                        <div id="deliveryOptionInfoBanner_${deliveryMode.code}"><spring:message code="checkout.rebuild.delivery.express.note"/></div>
                                    </mod:banner-ux>
                                </div>
                            </c:if>
                        </c:when>
                    </c:choose>
                </div>
            </div>
        </li>
    </c:forEach>

    <c:if test="${showScheduleDelivery}">
        <li class="o-cr-radio-list__item">
            <div class="o-cr-radio-item is-schedule">
                <input id="deliveryOptionSchedule"
                       class="o-cr-radio-item__radio js-cr-delivery-option js-is-calendar"
                       <c:if test="${not empty deliveryDate}">checked</c:if>
                       <c:if test="${disableSchedule or selectedDeliveryMode eq null}">disabled</c:if>
                       type="radio" name="deliveryOptionOptional">
                <label class="o-cr-radio-item__radio-label" for="deliveryOptionSchedule"></label>

                <div class="o-cr-radio-item__content js-cr-schedule">
                    <div class="o-cr-radio-item__content__main">
                        <div class="o-cr-radio-item__info">
                            <div class="o-cr-radio-item__radio-icon">
                                <i class="material-icons-round check_box_outline_blank is-radio-unchecked">check_box_outline_blank</i>
                                <i class="material-icons-round check_box_outline_blank is-radio-unchecked-hover">check_box_outline_blank</i>
                                <i class="material-icons-round check_box is-radio-checked is-clickable js-cr-schedule-uncheck">check_box</i>
                            </div>

                            <div class="o-cr-radio-item__status-icon">
                                <i class="material-icons-round date_range">&#xe916;</i>
                            </div>

                            <div class="o-cr-radio-item__info-text">
                                <div id="deliveryOptionScheduleTitle" class="fw-b"><spring:message code="checkout.rebuild.delivery.schedule.later"/></div>
                                <small id="deliveryOptionScheduleSelect" class="js-cr-schedule-txt-select" ${not empty deliveryDate ? 'hidden' : ''}><spring:message code="checkout.rebuild.delivery.schedule.select.delivery"/></small>
                                <small class="js-cr-schedule-txt-date" ${not empty deliveryDate ? '' : 'hidden'}><span
                                        id="deliveryOptionScheduleDate"
                                        class="js-cr-schedule-txt-date-label">${deliveryDate}</span>.&nbsp;<button id="deliveryOptionScheduleChangeDate"
                                        class="js-cr-schedule-change" type="button"><spring:message code="checkout.rebuild.delivery.schedule.change.date"/></button></small>
                            </div>
                        </div>
                    </div>

                    <div class="o-cr-radio-item__content__special js-cr-schedule-datepicker-section ${not empty deliveryDate ? 'is-hidden-on-page-load' : ''}">
                        <div class="o-cr-inline-datepicker js-cr-schedule-datepicker"
                             data-delivery-date="${deliveryDate}"
                             data-format="${sDateFormat}"
                             data-date-min="${sDateMin}"
                             data-date-max="${sDateMax}"></div>

                        <input class="js-cr-schedule-date" type="hidden" value="${deliveryDate}">

                        <p id="deliveryOptionScheduleInfo" class="o-cr-inline-datepicker__info-txt">
                            <spring:message code="checkout.rebuild.delivery.schedule.info"/>
                        </p>
                    </div>
                </div>

                <div id="deliveryOptionScheduleErrorMsg" class="o-cr-radio-item__error-text js-cr-schedule-error-msg" hidden></div>
            </div>
        </li>
    </c:if>
</ul>
