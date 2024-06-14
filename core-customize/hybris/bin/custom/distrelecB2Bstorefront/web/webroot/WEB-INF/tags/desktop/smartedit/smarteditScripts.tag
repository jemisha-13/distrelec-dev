<%@ tag body-content="scriptless" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<spring:eval expression="T(com.namics.distrelec.b2b.core.util.DistUtils).isAdminNode()" var="adminNode" />

<c:if test="${adminNode}">
    <script src="/_ui/desktop/common/js/Imager.min.js"></script>
    <script src="/_ui/addons/smarteditaddon/shared/common/js/webApplicationInjector.js"></script>
    <script src="/_ui/addons/smarteditaddon/shared/common/js/reprocessPage.js"></script>
    <script src="/_ui/addons/smarteditaddon/shared/common/js/adjustComponentRenderingToSE.js"></script>
</c:if>