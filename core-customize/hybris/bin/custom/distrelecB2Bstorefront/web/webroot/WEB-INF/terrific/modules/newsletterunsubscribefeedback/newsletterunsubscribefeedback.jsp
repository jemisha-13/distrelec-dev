<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<c:url value="/newsletter/resubscribe" var="resubscribeLink">
    <c:param name="email" value="${newsletterunsubscribeFeedbackForm.email}" />
    <c:param name="category" value="${newsletterunsubscribeFeedbackForm.category}" />
</c:url>
<c:url value="/newsletter/unsubscribeall" var="unsubscribeAllLink">
    <c:param name="email" value="${newsletterunsubscribeFeedbackForm.email}" />
</c:url>
<c:url value="/newsletter/unsubscribeFeedback" var="feedbackFormSubmitUrl" />
<c:set var="categoryID" value="${param.category}" />

<c:set var="knowhowId" value="${knowhowId}" />
<c:set var="salesAndClearanceId" value="${salesAndClearanceId}" />
<c:set var="personaliseRecommendationId" value="${personaliseRecommendationId}" />
<c:set var="customerSurveyId" value="${customerSurveyId}" />

<c:choose>
    <c:when test="${categoryID eq customerSurveyId}">
        <c:set var="categoryKey" value="unsubscribe.feedback.infoText.customersurvey.id56" />
    </c:when>
    <c:when test="${categoryID eq knowhowId}">
        <c:set var="categoryKey" value="unsubscribe.feedback.infoText.knowhow.id81" />
    </c:when>
    <c:when test="${categoryID eq salesAndClearanceId}">
        <c:set var="categoryKey" value="unsubscribe.feedback.infoText.salesandclearance.id82" />
    </c:when>
    <c:when test="${categoryID eq personaliseRecommendationId}">
        <c:set var="categoryKey" value="unsubscribe.feedback.infoText.personaliserecommendation.id86" />
    </c:when>
    <c:otherwise>
        <c:set var="categoryKey" value="unsubscribe.feedback.infoText.default" />
    </c:otherwise>
</c:choose>

<spring:theme code="validate.error.email" var="emailErrorMessage"/>

<form:form method="POST" modelAttribute="newsletterunsubscribeFeedbackForm" action="${feedbackFormSubmitUrl}"
           data-email="${newsletterunsubscribeFeedbackForm.email}"
           data-category="${newsletterunsubscribeFeedbackForm.category}"
           cssClass="js-unsubscribeFeedback-form">
    <div class="newsletter-unsubscribe-feedback">
        <h2 id="newsletUnsubFeedbackTitle" class="newsletter-unsubscribe-feedback__title">
            <i class="fa fa-check-circle"></i><spring:theme code="unsubscribe.feedback.title"/>
        </h2>

        <div class="newsletter-unsubscribe-feedback__inner">
            <p id="newsletUnsubFeedbackInfoText">
                <spring:theme code="${categoryKey}" arguments="${newsletterunsubscribeFeedbackForm.email}"/>
            </p>

            <a id="newsletUnsubFeedbackResubscribeLink" href="${resubscribeLink}">
                <strong><spring:theme code="unsubscribe.feedback.resubscribeLink"/></strong>
            </a>

            <div class="newsletter-unsubscribe-feedback__feedback js-unsubscribeFeedback-reasons">
                <p>
                    <strong id="newsletUnsubFeedbackReasonLabel">
                        <spring:theme code="unsubscribe.feedback.reasonsLabel"/>
                    </strong>
                </p>

                <div id="newsletUnsubFeedbackReason1" class="newsletter-unsubscribe-feedback__feedback-reason js-unsubscribeFeedback-reason">
                    <formUtil:ux-formCheckbox idKey="unsubscribeFeedbackFrequently1"
                                              inputCSS="js-reason-input"
                                              value="1"
                                              name="reason"
                                              labelKey="unsubscribe.feedback.reason1"
                                              path="reason"/>
                </div>

                <div id="newsletUnsubFeedbackReason2" class="newsletter-unsubscribe-feedback__feedback-reason js-unsubscribeFeedback-reason">
                    <formUtil:ux-formCheckbox idKey="unsubscribeFeedbackFrequently2"
                                              inputCSS="js-reason-input"
                                              value="2"
                                              name="reason"
                                              labelKey="unsubscribe.feedback.reason2"
                                              path="reason"/>
                </div>

                <div id="newsletUnsubFeedbackReason3" class="newsletter-unsubscribe-feedback__feedback-reason js-unsubscribeFeedback-reason"
                     data-email-error-msg="${emailErrorMessage}">
                    <formUtil:ux-formCheckbox idKey="unsubscribeFeedbackFrequently3"
                                              inputCSS="js-reason-input js-is-new-email"
                                              value="3"
                                              name="reason"
                                              labelKey="unsubscribe.feedback.reason3"
                                              labelArguments="${newsletterunsubscribeFeedbackForm.email}"
                                              path="reason"/>

                    <div class="newsletter-unsubscribe-feedback__new-email js-unsubscribeFeedback-reason-additional">
                        <p id="newsletUnsubFeedbackReason3optionalLabel">
                            <spring:theme code="unsubscribe.feedback.reason3.newsletterNewEmail"/>&nbsp;<span class="is-opt"><spring:theme code="unsubscribe.feedback.reason3.optional"/></span>
                        </p>
						<formUtil:formInputBox idKey="reasonAdditionalEmail" placeHolderKey="subscribe.popup.email.address"
                                               inputType="email"
                                               path="alternateEmail" inputCSS="js-unsubscribeFeedback-reason-additional-formElement validate-email" />
						<input type="hidden" value="${newsletterunsubscribeFeedbackForm.email}" name="email" id="email" />
						<input type="hidden" value="${newsletterunsubscribeFeedbackForm.category}" name="category" id="category" />
                    </div>
                </div>
					
                <div id="newsletUnsubFeedbackReason4" class="newsletter-unsubscribe-feedback__feedback-reason js-unsubscribeFeedback-reason">
                    <formUtil:ux-formCheckbox idKey="unsubscribeFeedbackFrequently4"
                                              inputCSS="js-reason-input"
                                              value="4"
                                              name="reason"
                                              labelKey="unsubscribe.feedback.reason4"
                                              path="reason"/>
                </div>

                <div id="newsletUnsubFeedbackReason5" class="newsletter-unsubscribe-feedback__feedback-reason js-unsubscribeFeedback-reason">
                    <formUtil:ux-formCheckbox idKey="unsubscribeFeedbackFrequentlyOther"
                                              inputCSS="js-is-other js-reason-input"
                                              value="5"
                                              name="reason"
                                              labelKey="unsubscribe.feedback.reasonOther"
                                              path="reason"/>

                    <div class="newsletter-unsubscribe-feedback__other-msg js-unsubscribeFeedback-reason-additional js-is-mandatory">
                        <spring:theme code="unsubscribe.feedback.reasonOther.placeholder" var="sReasonOtherPlaceholder"/>
                        <textarea class="js-on-type-check js-unsubscribeFeedback-reason-additional-formElement"
                                  name="reasonOtherMessage"
                                  placeholder="${sReasonOtherPlaceholder}"></textarea>
                    </div>
                </div>
            </div>

            <button id="newsletUnsubFeedbackReasonSubmitButton" class="ux-btn ux-btn--submit js-unsubscribeFeedback-submit" type="button" disabled>
                <spring:theme code="unsubscribe.feedback.submit"/>
            </button>

            <div class="text-center">
                <a href="${unsubscribeAllLink}">
                    <strong id="newsletUnsubFeedbackUnsubscribeAllLink"><spring:theme code="unsubscribe.feedback.unsubscribeAll"/></strong>
                </a>
            </div>
        </div>
    </div>
</form:form>
