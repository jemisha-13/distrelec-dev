<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<c:url value="/" var="shopRoot" />

<c:choose>
    <c:when test="${pagesource == 'resubscribe'}">
        <spring:theme code="unsubscribe.feedback.resubscription.title" var="sPageTitle"/>
    </c:when>

    <c:otherwise>
        <spring:theme code="unsubscribe.feedback.title" var="sPageTitle"/>
    </c:otherwise>
</c:choose>

<div class="newsletter-unsubscribe-feedback">
    <h2 id="newsletUnsubFeedbackTitle" class="newsletter-unsubscribe-feedback__title"><i class="fa fa-check-circle"></i>${sPageTitle}</h2>

    <div class="newsletter-unsubscribe-feedback__inner">
        <c:if test="${pagesource == 'unsubscribeFeedback' }">
            <div class="newsletter-unsubscribe-feedback__success">
                <div id="newsletUnsubFeedbackThankYouMsg">
                    <spring:theme code="unsubscribe.feedback.success.thankYou"/>
                </div>
            </div>
        </c:if>

        <a id="newsletUnsubFeedbackContinueButton" class="ux-btn ux-btn--submit js-unsubscribeFeedback-submit" href="${shopRoot}">
            <spring:theme code="unsubscribe.feedback.success.continue"/>
        </a>
    </div>
</div>
