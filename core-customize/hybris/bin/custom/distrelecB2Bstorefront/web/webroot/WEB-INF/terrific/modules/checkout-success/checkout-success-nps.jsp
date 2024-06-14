<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!-- NPS Section -->
<div class="gu-12 message-success hidden">
	<p><spring:message code="feedback.nps.success" text="success" /></p>
	<a href="/" title=""><spring:message code="feedback.nps.returnlink" text="Return link" /></a>
</div>
<div class="gu-12 message-error hidden"> <spring:message code="feedback.nps.error" text="error" /> </div>

<div class="nps-section inner">
	<div class="nps-section__item">
		<div class="nps-section__item__container score-main-holder">
			<h3><spring:message code="feedback.nps.score.title" text="title" /></h3>
			<p class="nps_score"></p>
			<span class="nps-score-toggle"><spring:message code="feedback.nps.score.change" text="change score" /></span>
		</div>
		<div class="nps-section__item__container">
			<form:form method="post" modelAttribute="npsForm" action="/feedback/nps">
				<input type="hidden" name="type" value="OrderConfirmation" />
				<form:hidden path="id" />
				<form:hidden path="email" />
				<form:hidden path="order" />
				<form:hidden path="fname" />
				<form:hidden path="namn" />
				<form:hidden path="company" />
				<form:hidden path="cnumber" />
				<form:hidden path="contactnum" />
				<form:hidden path="delivery" />
				<form:hidden path="id" />

				<div class="gu-12 score-rating">
					<p class="sub-title"> <b><spring:message code="feedback.nps.nps" text="nps" /></b> </p>
					<div class="table-radio">
						<div class="unlikely"> <spring:message code="feedback.nps.unlikely" text="unlikely" /> </div>
						<%-- For loop from 0 to 10 to generate the radio buttons --%>
						<c:forEach begin="0" end="10" var="value">
							<div class="list-item">
								<form:radiobutton id="value${value}" path="value" name="value-radio-button" value="${value}" cssClass="radio" disabled="${hasErrors}"/>
								<form:label for="value${value}" path="value" name="value-radio-button" class="">${value}</form:label>
							</div>
						</c:forEach>
						
						<div class="verylikely"> <spring:message code="feedback.nps.verylikely" text="verylikely" /> </div>
					</div>
				</div>

				<div class="gu-12 reason-select">
					<p><spring:message code="feedback.nps.score.reason" /></p>
					<select id="select-nps-reasons" name="reason" class="selectpicker-reason selectboxit-meta">
						<option value=""><spring:message code="import-tool.matching.options.select" /></option>
						<c:forEach items="${npsFormReasons}" var="reason">
							<option value="${reason}"><spring:message code="nps.reason.${reason}" /></option>
						</c:forEach>
					</select>
				</div>

				<%-- Add sub reasons logic here --%>
				<div class="gu-12 reason-select sub-reasons hidden">
					<spring:message code="nps.reason.topic.sub" text="Sub topic" />: <br />
					<select id="select-nps-sub-reasons" name="subreason" class="selectpicker-subreason selectboxit-meta">
						<option value=""><spring:message code="import-tool.matching.options.select" /></option>
						<c:forEach items="${npsFormSubReasons}" var="subReason">
							<option value="${subReason}">
								<spring:message code="nps.reason.${fn:replace(subReason, '_', '.')}" />
							</option>
						</c:forEach>
					</select>
				</div>
				<div class="gu-12">
					<p> <spring:message code="feedback.nps.score.comment" text="nps comment" /> </p>
					<form:textarea rows="3" class="feedback-textarea" path="feedback" data-sample="the text sample" disabled="${hasErrors}" />
				</div>
				<div class="gu-12">
					<button type="submit2" class="btn btn-primary btn-send" ${hasErrors ? 'disabled' : ''}><spring:message code="feedback.nps.score.submit" text="Send" />  </button>
				</div>
			</form:form>
		</div>
	</div>
</div>
<!-- End of NPS Section -->