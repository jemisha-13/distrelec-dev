<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<div class="modal js-doubleOptinReminderModal" tabindex="-1" data-show-reminder="${showDoubleOptInPopup}">
    <h2><span class="info-icon">i</span><spring:message code="text.preferences.updated.dont.forget"/></h2>
    <p><spring:message code="text.preferences.updated.below" /></p>
    <p><spring:message code="text.preferences.updated.email.sent" arguments="<span class='js-reminderPopupEmail'>${user.email}</span>"/></p>
    <button class="btn btn-primary" data-dismiss="modal" aria-hidden="true"><spring:message code="text.preferences.updated.ok"/></button>
</div>
