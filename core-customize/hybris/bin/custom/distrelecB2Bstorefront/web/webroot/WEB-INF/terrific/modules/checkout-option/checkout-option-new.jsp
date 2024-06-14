<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>


<spring:message code="checkout.option.register.new.account" text="Register a new account" var="sRegisterNewAccount"/>
<spring:message code="checkout.option.private.customer" text="Private customer" var="sPrivateCustomer"/>
<spring:message code="checkout.option.business.customer" text="Business customer" var="sBusinessCustomer"/>

<div class="head">
    ${sRegisterNewAccount}: ${b2cAction}
</div>

<div class="box">
    <div class="center-wrap image-wrap">
        <div class="wrap">
            <img alt="Private Customer" src="/_ui/all/media/img-modules/mod-register-customer-private.png" />
        </div>
    </div>
    <div class="center-wrap text-wrap">
        <div class="wrap">
            <span>Lorem Ipsum sdjfbsj dsfkge ekgege erkher Lorem Ipsum sdjfbsj dsfkge ekgege erkher Lorem Ipsum sdjfbsj dsfkge ekgege erkher</span>
        </div>
    </div>
    <div class="center-wrap button-wrap">
        <a href="${b2cAction}" class="btn btn-primary btn-arrow-right">
            ${sPrivateCustomer}
            <i></i>
        </a>
    </div>
</div>

<div class="box">
    <div class="center-wrap image-wrap">
        <div class="wrap">
            <img alt="Business Customer" src="/_ui/all/media/img-modules/mod-register-customer-business.png" />
        </div>
    </div>
    <div class="center-wrap text-wrap">
        <div class="wrap">
            <span>Lorem Ipsum sdjfbsj dsfkge ekgege erkher Lorem Ipsum sdjfbsj dsfkge ekgege erkher</span>
        </div>
    </div>
    <div class="center-wrap button-wrap">
        <a href="${b2bAction}" class="btn btn-primary btn-arrow-right">
            ${sBusinessCustomer}
            <i></i>
        </a>
    </div>
</div>

