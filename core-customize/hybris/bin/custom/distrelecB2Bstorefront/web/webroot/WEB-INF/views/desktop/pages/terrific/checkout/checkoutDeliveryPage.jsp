<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<views:page-checkout pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout mod-layout--checkout">
    <div class="container">
        <div class="row">
            <div class="col-lg-10 offset-lg-1">
                <mod:checkout-rebuild-global-messages />
            </div>
        </div>
    </div>

    <mod:checkout-rebuild htmlClasses="js-cr" cartData="${cartData}" attributes="data-scroll-to='${param.edit}'">
        <c:set var="pickupModeAvailable" value="FALSE"/>

        <c:forEach items="${deliveryModes}" var="deliveryMode">
            <c:if test="${deliveryMode.selected}">
                <c:set var="selectedDeliveryMode" value="${deliveryMode.code}"/>
            </c:if>
            <c:if test="${deliveryMode.code eq 'SAP_A1'}">
                <c:set var="pickupModeAvailable" value="TRUE"/>
            </c:if>
        </c:forEach>

        <div class="col-lg-6 offset-lg-1">
            <div id="deliveryOptionsBlock">
                <mod:checkout-rebuild-block template="delivery-options"
                                            skin="delivery-options"
                                            blockTitleKey="text.account.deliveryOptions"
                                            blockTitleClass="is-full"
                                            htmlClasses="js-rebuild-block-delivery-options"
                                            cartData="${cartData}"/>
            </div>

            <c:if test="${pickupModeAvailable}">
                <mod:checkout-rebuild-block template="pickup-location"
                                            skin="pickup-location"
                                            blockTitleKey="orderdetailsection.pickupLocation"
                                            blockTitleClass="is-full"
                                            htmlClasses="${selectedDeliveryMode ne 'SAP_A1' ? 'is-hidden' : ''} js-rebuild-block-pickup-location"
                                            cartData="${cartData}"/>
            </c:if>

            <div id="billingDetailsBlock">
                <c:set value="${isBillingAndShippingAddress or isGuestCheckout or isEShopGroup ? 'checkout.rebuild.billing.and.delivery' : 'checkout.rebuild.billing.details'}" var="billingTitleKey" />
                <c:if test="${selectedDeliveryMode eq 'SAP_A1'}">
                    <c:set value="checkout.rebuild.billing.details" var="billingTitleKey" />
                </c:if>

                <mod:checkout-rebuild-block template="billing-details"
                                            skin="billing-details"
                                            blockTitleKey="${billingTitleKey}"
                                            htmlClasses="${selectedDeliveryMode eq 'SAP_A1' ? 'is-pickup-selected' : ''} js-rebuild-block-billing-details"
                                            cartData="${cartData}"/>
            </div>

            <div id="deliveryDetailsBlock">
                <c:set var="deliveryDetailsHidden" value="${selectedDeliveryMode eq 'SAP_A1' or isBillingAndShippingAddress}"/>

                <c:if test="${not isBillingAddressShippable}">
                    <c:set var="deliveryDetailsHidden" value="false"/>
                </c:if>

                    <%-- Guest and B2E don't have delivery address (shipping address) --%>
                <c:if test="${not isEShopGroup and not isGuestCheckout}">
                    <mod:checkout-rebuild-block template="delivery-details"
                                                skin="delivery-details"
                                                htmlClasses="js-rebuild-block-delivery-details ${not showBillingInfoMode and not multipleBillingAddresses ? 'is-disabled' : ''} ${deliveryDetailsHidden ? 'is-hidden' : ''}"
                                                blockTitleKey="checkout.rebuild.delivery.details"
                                                blockTitleClass="is-full"
                                                cartData="${cartData}"/>
                </c:if>
            </div>
        </div>

        <div class="col-lg-4">
            <div>
                <div class="js-sticky-sidebar-start"></div>
                <div class="mod-checkout-rebuild__sticky-sidebar js-sticky-sidebar">
                    <mod:checkout-rebuild-block template="order-summary"
                                                skin="order-summary"
                                                blockTitleKey="checkout.rebuild.order.summary"
                                                blockTitleClass="is-full"
                                                cartData="${cartData}"/>
                </div>
            </div>
        </div>
    </mod:checkout-rebuild>
</views:page-checkout>
