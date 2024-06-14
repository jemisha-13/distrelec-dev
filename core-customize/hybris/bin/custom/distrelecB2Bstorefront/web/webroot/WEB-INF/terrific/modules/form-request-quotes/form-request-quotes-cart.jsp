<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formUtil" tagdir="/WEB-INF/tags/desktop/terrific/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<c:set var="isLoggedIn" value="false"/>

<sec:authorize access="hasRole('ROLE_CUSTOMERGROUP')">
    <c:set var="isLoggedIn" value="true"/>
</sec:authorize>

<button type="submit"
        class="mat-button mat-button--action-green requestQuotes
        <c:if test="${isLoggedIn eq false}">modalPopUp </c:if>"
        title="<spring:message code="quote.request.link"/>" data-aainteraction="request quotation"><spring:message code="quote.request.link"/></button>
