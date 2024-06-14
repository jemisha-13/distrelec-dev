<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

<spring:eval expression="new java.util.Locale('en')" var="enLocale" />

<c:if test="${not empty localizedUrlLink}">
    <c:set var="urlLink" value="${localizedUrlLink}"/>
</c:if>

<article class="bannner-hero row bannner-hero-OCI">
    <div class="bannner-hero__img-wrapper col-12 col-lg-6">
        <c:choose>
            <c:when test="${not empty urlLink}">
                <a href="${urlLink}"
            </c:when>
            <c:otherwise>
                <span
            </c:otherwise>
        </c:choose>

            <c:forEach var="dataAttributeEntry" items="${dataAttributes}">
                <c:choose>
                    <c:when test="${dataAttributeEntry.key == 'aaLinkText'}">
                        data-aaLinkText="Image"
                    </c:when>
                    <c:otherwise>
                        data-${dataAttributeEntry.key}="${dataAttributeEntry.value}"
                    </c:otherwise>
                </c:choose>
            </c:forEach> >
            <img alt="${headline}" width="265" height="148" src="${media.url}">

        <c:choose>
            <c:when test="${not empty urlLink}">
                </a>
            </c:when>
            <c:otherwise>
                </span>
            </c:otherwise>
        </c:choose>
    </div>
    <div class="col-12 col-lg-6">
        <h4 class="bannner-hero__heading">

            <c:choose>
                <c:when test="${not empty urlLink}">
                    <a href="${urlLink}"
                </c:when>
                <c:otherwise>
                    <span
                </c:otherwise>
            </c:choose>

            <c:forEach var="dataAttributeEntry" items="${dataAttributes}">
                <c:choose>
                    <c:when test="${dataAttributeEntry.key == 'aaLinkText'}">
                        data-aaLinkText="Title"
                    </c:when>
                    <c:otherwise>
                        data-${dataAttributeEntry.key}="${dataAttributeEntry.value}"
                    </c:otherwise>
                </c:choose>
            </c:forEach> >
                ${headline}

            <c:choose>
                <c:when test="${not empty urlLink}">
                    </a>
                </c:when>
                <c:otherwise>
                    </span>
                </c:otherwise>
            </c:choose>

        </h4>
        <p class="bannner-hero__description">${content}</p>
        <c:if test="${not empty urlLink}">
            <a class="bannner-hero__cta mat-button mat-button__solid--action-green" href="${urlLink}"
                    <c:forEach var="dataAttributeEntry" items="${dataAttributes}">
                        data-${dataAttributeEntry.key}="${dataAttributeEntry.value}"
                    </c:forEach> >
                    ${localizedUrlText} <i class="fas fa-angle-right"></i>
            </a>
        </c:if>
    </div>
</article>
