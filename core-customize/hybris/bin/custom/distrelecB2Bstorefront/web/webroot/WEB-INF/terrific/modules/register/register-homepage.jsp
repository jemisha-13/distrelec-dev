<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<spring:theme code="register.component.email.placeholder" text="" var="emailPlaceholder" />
<c:set var="customerType" value="${cartData.b2bCustomerData.customerType}" />

<sec:authorize access="!hasRole('ROLE_CUSTOMERGROUP')">
    <c:url var="register" value="/registration?registerFrom=homepage"/>

    <form:form action="${register}" method="get" modelAttribute="register">
        <div class="state__logout">
            <div class="welcome">
                <i class="fa fa-user"></i>
                <p class="register-text"><spring:message code="home.welcome.title"/></p>
                <div class="welcome__action">
                    <a href="/registration" data-aasectionpos="c3r1"
                       data-aasectiontitle="Welcome to Distrelec"
                       data-aabuttonpos="1"
                       data-aalinktext="Register"
                       data-aatype="homepage-interaction"><spring:message code="newcheckout.registerButton" /></a>
                    <a href="/login" data-aasectionpos="c3r1"
                       data-aasectiontitle="Welcome to Distrelec"
                       data-aabuttonpos="2"
                       data-aalinktext="Login"
                       data-aatype="homepage-interaction"
                       data-aainteraction="login button"
                    ><spring:message code="base.login" /></a>
                </div>
            </div>

            <%
                String emailToRegister = request.getParameter("emailToRegister");
                session.setAttribute("emailToRegister",emailToRegister);
            %>
        </div>
    </form:form>
</sec:authorize>

<sec:authorize access="hasRole('ROLE_CUSTOMERGROUP')">
    <div class="home-register__content state__login">
        <div class="col-12 home-register__content__headings">
            <h1 class="home-register__title">
                <spring:message code="homepage.welcome.title" arguments="${user.firstName}" />
            </h1>
            <p class="home-register__description">
                <spring:message code="home.register.component.logged.in" />
            </p>
        </div>

        <ul class="home-register__account-overview col-12">
            <li class="home-register__account-overview__item">
                <c:url value="/my-account/order-history" var="orderManagerUrl" />
                <a href="${orderManagerUrl}" title="<spring:message code="account.openOrders" />"
                   data-aasectionpos="c3r1"
                   data-aasectiontitle="Welcome to Distrelec"
                   data-aabuttonpos="1"
                   data-aalinktext="Open orders"
                   data-aatype="homepage-interaction"
                >
                <span class="home-register__account-overview__item__count">
                        ${openOrdersCount}
                </span>
                <span class="home-register__account-overview__item__title">
                    <spring:message code="account.openOrders" />
                </span>
                </a>
            </li>
            <li class="home-register__account-overview__item active">
                <c:url value="/my-account/invoice-history" var="invoiceManagerUrl" />
                <a href="${invoiceManagerUrl}" title="<spring:message code="account.recentInvoices" />"
                   data-aasectionpos="c3r1"
                   data-aasectiontitle="Welcome to Distrelec"
                   data-aabuttonpos="2"
                   data-aalinktext="Recent invoices"
                   data-aatype="homepage-interaction">
                <span class="home-register__account-overview__item__count">
                        ${newInvoicesCount}
                </span>
                <span class="home-register__account-overview__item__title active">
                    <spring:message code="account.recentInvoices" />
                </span>
                </a>
            </li>
            <c:if test="${currentBaseStore.quotationsEnabled and currentChannel.type ne 'B2C'}">
                <li class="home-register__account-overview__item">
                    <c:url value="/my-account/quote-history" var="quoteUrl" />
                    <a href="${quoteUrl}" title="<spring:message code="account.offeredQuotes" />"
                       data-aasectionpos="c3r1"
                       data-aasectiontitle="Welcome to Distrelec"
                       data-aabuttonpos="3"
                       data-aalinktext="Offered quotes"
                       data-aatype="homepage-interaction">
                    <span class="home-register__account-overview__item__count">
                        ${quoteCount}
                    </span>
                    <span class="home-register__account-overview__item__title">
                        <spring:message code="account.offeredQuotes" />
                    </span>
                    </a>
                </li>
            </c:if>
            <c:if test="${siteUid ne 'distrelec_FR'}">
                <sec:authorize access="hasRole('ROLE_B2BCUSTOMERGROUP')">
                    <li class="home-register__account-overview__item">
                        <c:url value="/my-account/order-approval" var="approvalManagerUrl" />
                        <a href="${approvalManagerUrl}" title="<spring:message code="account.approvalRequests" />"
                           data-aasectionpos="c3r1"
                           data-aasectiontitle="Welcome to Distrelec"
                           data-aabuttonpos="4"
                           data-aalinktext="Approval requests"
                           data-aatype="homepage-interaction">
                        <span class="home-register__account-overview__item__count">
                            ${appReqCount}
                        </span>
                        <span class="home-register__account-overview__item__title">
                            <spring:message code="account.approvalRequests" />
                        </span>
                        </a>
                    </li>
                </sec:authorize>
            </c:if>
        </ul>
    </div>
</sec:authorize>
