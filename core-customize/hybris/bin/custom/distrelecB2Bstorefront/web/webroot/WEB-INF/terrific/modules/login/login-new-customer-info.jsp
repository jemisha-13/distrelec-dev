<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>

<div class="login__new__wrapper">
    <div class="login__card__title">
        <h2 id="newCustomerTitle"><spring:theme code="login.newcustomer.title"/></h2>

        <p id="newCustomerSubtitle"><spring:theme code="login.checkout.new.customer.subtitle"/></p>
        <ul id="newCustomerCheckListInfo" class="login__card__check-list">
            <li>
                <i class="fa fa-check"></i><span id="newCustomerCheckListInfoItem1"><spring:theme code="login.checkout.new.customer.info1"/></span>
            </li>

            <li>
                <i class="fa fa-check"></i><span id="newCustomerCheckListInfoItem2"><spring:theme code="login.checkout.new.customer.info2"/></span>
            </li>

            <li>
                <i class="fa fa-check"></i><span id="newCustomerCheckListInfoItem3"><spring:theme code="login.checkout.new.customer.info3"/></span>
            </li>
        </ul>
    </div>

    <a href="/registration/checkout">
        <button id="continueToCheckoutButtonNewCustomer" data-aainteraction="registration button" class="ux-btn ux-btn--brand-green" type="button"><spring:theme code="login.checkout.continue.to.checkout.button"/></button>
    </a>
</div>
