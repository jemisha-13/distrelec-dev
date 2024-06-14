<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>

<div class="modal js-doubleOptinModal" tabindex="-1">
    <h2><span class="info-icon">i</span><spring:message code="text.preferences.modal.confirm"/></h2>
    <p><spring:message code="text.preferences.email.sentTo" arguments="${user.email}"/></p>
    <p><spring:message code="text.preferences.incorrect.notice"/></p>
    <button class="btn btn-primary" data-dismiss="modal" aria-hidden="true"><spring:message code="text.preferences.updated.ok"/></button>
</div>
