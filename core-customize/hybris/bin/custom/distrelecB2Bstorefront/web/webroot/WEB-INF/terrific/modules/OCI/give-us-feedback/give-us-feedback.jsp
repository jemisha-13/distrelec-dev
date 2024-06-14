<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<spring:message code="giveusfeedback.supportButton" var="sSupportButton" />
<spring:message code="giveusfeedback.supportLink" var="sSupportLink" />
<spring:message code="giveusfeedback.feedbackButton" var="sFeedbackButton" />
<spring:message code="giveusfeedback.feedbackLink" var="sFeedbackLink" />

<div class="row">
	<div class="ct">
		<ul class="list">
			<li class="list-item -buttons">
				<a class="btn btn-support" href="${sSupportLink}">${sSupportButton}</a>
				<a class="btn btn-feedback" href="${sFeedbackLink}">${sFeedbackButton}</a>
			</li>
		</ul>
	</div>
</div>
