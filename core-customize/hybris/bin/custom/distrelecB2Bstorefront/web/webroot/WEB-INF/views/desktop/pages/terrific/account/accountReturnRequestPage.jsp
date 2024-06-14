<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<c:set var="sContactLink" value="/cms/contact" />
<spring:message code="rma.success.title" var="sReturnRequestCreateTitle" />
<spring:message code="rma.success.textSecond" arguments="${email}" var="sReturnRequestCreateParagraphSecond" />
<spring:message code="rma.success.text" arguments="${createRMAResponseData.rmaNumber}" var="sReturnRequestCreateParagraph" />
<spring:message code="rma.success.no.Title" var="sNoTitle" />
<spring:message code="rma.success.no.Text" var="sNoText" />
<spring:message code="rma.guest.returnPage.confirmation" var="sConfirmationText" />
<spring:message code="formOfflineAddressChange.failed" var="sConfirmationFailed" />
<spring:message code="text.back" var="sBackLink" />

<views:page-default-md-full pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-default-full skin-layout-account skin-layout-return-success skin-layout-wide">
    <div id="breadcrumb" class="breadcrumb">
        <div class="container">
            <mod:breadcrumb />
        </div>
    </div>

    <div class="return-request-page-holder js-ensighten-return-request-success" data-return-request-success="${isRmaCreated ? isRmaCreated : 'false'}">
        <div class="return-request-page-holder__json d-none">
            <p>${createRMARequestJson}</p>
        </div>
        <div class="container">
            <div class="row">
                <div class="col-12">
                    <mod:global-messages />
                    <div class="customer-assistance-section customer-assistance-section--success d-none">
                        <p>${sConfirmationText}</p>
                    </div>
                    <div class="customer-assistance-section customer-assistance-section--false d-none">
                        <p>${sConfirmationFailed}</p>
                    </div>
                </div>
                <div class="col-12 col-lg-9">
                    <div class="item-holder">
                        <div class="item-holder__item">
                            <h1 class="base page-title">${sReturnRequestCreateTitle}</h1>
                            <p class="page-tagline">${sReturnRequestCreateParagraph}</p>
                            <p>${sReturnRequestCreateParagraphSecond}</p>
                        </div>
                        <c:choose>
                            <c:when test="${currentCountry.isocode == 'NO'}">
                                <div class="item-holder__item">
                                    <h2>${sNoTitle}</h2>
                                    <p>${sNoText}</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="item-holder__item">
                                    <p>${sConfirmationText}</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                        <div class="item-holder__item">
                            <c:url value="/my-account/order-history/order-details/${orderData.code}/return-items" var="backToReturnFormUrl" />
                            <a class="item-holder__link" href="${backToReturnFormUrl}">${sBackLink}</a>
                        </div>
                    </div>
                </div>
                <div class="col-12 col-lg-3 d-print-none">
                    <mod:nav-content template="myaccount" skin="myaccount" expandedNav="OrderManager" activeLink="OrderHistory" />
                </div>
            </div>
        </div>
    </div>
</views:page-default-md-full>
