<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="stepDone" value="true"/>

<ul class="mod-checkout-progressbar__list">
    <c:forEach var="step" items="${processSteps}" varStatus="status">
        <c:if test="${step.active}">
            <c:set var="stepDone" value="false"/>
        </c:if>

        <li class="mod-checkout-progressbar__list-item ${step.active ? 'is-active' : ''} ${stepDone ? 'is-done' : ''}">
            <a class="mod-checkout-progressbar__list-item-link" href="${step.url}">
                <strong id="processStepNo${status.index + 1}" class="mod-checkout-progressbar__list-item-link__no">
                    <c:choose>
                        <c:when test="${stepDone}">
                            <i class="material-icons-round task_alt">&#xe2e6;</i>
                        </c:when>
                        <c:otherwise>
                            ${status.index + 1}
                        </c:otherwise>
                    </c:choose>
                </strong>

                <span id="processStepInfo${status.index + 1}" class="mod-checkout-progressbar__list-item-link__info hidden-md-down">
                    <spring:message code="${step.messageCode}"/>
                </span>
            </a>
        </li>
    </c:forEach>
</ul>
