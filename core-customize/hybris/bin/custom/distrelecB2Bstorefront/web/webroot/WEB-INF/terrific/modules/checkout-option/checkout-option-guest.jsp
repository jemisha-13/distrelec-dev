<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<spring:message code="checkout.option.continue.as.guest" text="Continue as Guest" var="sContinueAsGuest"/>
<spring:message code="checkout.option.order.as.guest" text="Order as guest" var="sOrderAsGuest"/>


<div class="head">
    ${sContinueAsGuest}
</div>

<div class="box">
    <div class="center-wrap image-wrap">
        <img src="_ui/all/media/img-modules/mod-checkout-option-customer-b2c.png"/>
    </div>
    <div class="center-wrap text-wrap">
        <span>Lorem Ipsum sdjfbsj dsfkge ekgege erkher</span>
    </div>
    <div class="center-wrap button-wrap">
        <a href="${guestAction}" class="btn btn-primary btn-arrow-right">
            ${sOrderAsGuest}
            <i></i>
        </a>
    </div>
</div>

