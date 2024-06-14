<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<%-- List of Attributes --%>
<c:set var="headline" value="${headline}" />
<c:set var="body" value="${body}"/>
<c:set var="type" value="${type}" />
<c:set var="widthPercent" value="${widthPercent}" />
<c:set var="displayIcon" value="${displayIcon}" />



    <p class="messages-component ${type}">
        <span class="messages-component__icon">
            <c:if test = "${type eq 'info'}">
                <i class="fas fa-info-circle"></i>
            </c:if>
            <c:if test = "${type eq 'error'}">
                <i class="fas fa-times-circle"></i>
            </c:if>
            <c:if test = "${type eq 'success'}">
                <i class="fas fa-check-circle"></i>
            </c:if>
        </span>
        <span class="messages-component__body">
            <span class="messages-component__label">${headline}</span>
            <span class="messages-component__description">${body}</span>
        </span>
    </p>
