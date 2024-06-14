<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<spring:message code="user.toggle.business" text="Business" var="sBusinessText" />
<spring:message code="user.toggle.private" text="Private" var="sPersonalText" />

<div class="js-toggle active container hidden">
    <div class="js-indicator__left indicator"></div>

    <div class="js-label label" data-business="${sBusinessText}" data-personal="${sPersonalText}" data-aainteraction="User Toggle">
        ${ sBusinessText }
    </div>

    <div class="js-indicator__right indicator hidden"></div>
</div>