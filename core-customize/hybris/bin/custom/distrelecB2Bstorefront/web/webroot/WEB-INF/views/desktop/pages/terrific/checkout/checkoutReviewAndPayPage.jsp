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

    <mod:checkout-rebuild htmlClasses="js-cr" cartData="${cartData}">
        <div class="col-lg-6 offset-lg-1">
            <mod:checkout-rebuild-block template="review-pay-info"
                                        skin="review-pay-info"
                                        htmlClasses="js-rebuild-block-review-pay-info"
                                        cartData="${cartData}"/>

            <c:if test="${showCodiceDestinatario}">
                <mod:checkout-rebuild-block template="review-pay-codice"
                                            skin="review-pay-codice"
                                            htmlClasses="js-rebuild-block-review-pay-codice"
                                            cartData="${cartData}"/>
            </c:if>

            <mod:checkout-rebuild-block template="review-pay-method"
                                        skin="review-pay-method"
                                        htmlClasses="js-rebuild-block-review-pay-method"
                                        blockTitleKey="checkout.summary.paymentMethod.header"
                                        blockTitleClass="is-full"
                                        cartData="${cartData}"/>
        </div>

        <div class="col-lg-4">
            <div>
                <div class="js-sticky-sidebar-start"></div>
                <div class="mod-checkout-rebuild__sticky-sidebar js-sticky-sidebar">
                    <mod:checkout-rebuild-block template="review-pay-order-summary"
                                                skin="order-summary"
                                                blockTitleKey="checkout.rebuild.order.summary"
                                                blockTitleClass="is-full"
                                                cartData="${cartData}"/>
                </div>
            </div>
        </div>
    </mod:checkout-rebuild>
</views:page-checkout>
