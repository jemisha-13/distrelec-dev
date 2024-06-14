<%@ tag body-content="scriptless" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<spring:eval expression="T(com.namics.distrelec.b2b.core.util.DistUtils).isAdminNode()" var="adminNode" />

<c:if test="${adminNode}">
    <link rel="stylesheet" href="/_ui/desktop/common/css/smarteditoverrides.css" />
</c:if>