<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>

<article class="row bannner-article">
    <div class="bannner-article__info">

        <c:choose>
            <c:when test="${not empty localizedUrlLink}">
                <a href="${localizedUrlLink}"
            </c:when>
            <c:otherwise>
                <span
            </c:otherwise>
        </c:choose>

        class="bannner-article__info__heading"
                <c:if test="${not empty localizedUrlLink}">
                    <c:forEach var="dataAttributeEntry" items="${dataAttributes}">
                        <c:choose>
                            <c:when test="${dataAttributeEntry.key == 'aaLinkText'}">
                                data-aaLinkText="Title"
                            </c:when>
                            <c:otherwise>
                                data-${dataAttributeEntry.key}="${dataAttributeEntry.value}"
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </c:if> >
            ${headline}

        <c:choose>
            <c:when test="${not empty localizedUrlLink}">
                </a>
            </c:when>
            <c:otherwise>
                </span>
            </c:otherwise>
        </c:choose>

        <c:choose>
            <c:when test="${not empty localizedUrlLink}">
                <a href="${localizedUrlLink}"
            </c:when>
            <c:otherwise>
                <span
            </c:otherwise>
        </c:choose>

            class="bannner-article__info__description"
			
            <c:if test="${not empty localizedUrlLink}">
            <c:forEach var="dataAttributeEntry" items="${dataAttributes}">
                <c:choose>
                    <c:when test="${dataAttributeEntry.key == 'aaLinkText'}">
                        data-aaLinkText="Text"
                    </c:when>
                    <c:otherwise>
                        data-${dataAttributeEntry.key}="${dataAttributeEntry.value}"
                    </c:otherwise>
                </c:choose>
            </c:forEach>
            </c:if> >
            ${content}

        <c:choose>
            <c:when test="${not empty localizedUrlLink}">
                </a>
            </c:when>
            <c:otherwise>
                </span>
            </c:otherwise>
        </c:choose>

        <c:choose>
            <c:when test="${not empty localizedUrlLink}">
                <a href="${localizedUrlLink}"
            </c:when>
            <c:otherwise>
                <span
            </c:otherwise>
        </c:choose>

            class="bannner-article__info__cta mat-button mat-button__normal--action-blue"
                <c:if test="${not empty localizedUrlLink}">
                <c:forEach var="dataAttributeEntry" items="${dataAttributes}">
                    data-${dataAttributeEntry.key}="${dataAttributeEntry.value}"
                </c:forEach>
                </c:if> >
            ${localizedUrlText} <i class="fas fa-angle-right"></i>

        <c:choose>
            <c:when test="${not empty localizedUrlLink}">
                </a>
            </c:when>
            <c:otherwise>
                </span>
            </c:otherwise>
        </c:choose>

    </div>

    <c:choose>
        <c:when test="${not empty localizedUrlLink}">
            <a href="${localizedUrlLink}"
        </c:when>
        <c:otherwise>
            <span
        </c:otherwise>
    </c:choose>
    class="bannner-article__img-wrapper lazy-background" data-bg-src="${media.url}"
            <c:if test="${not empty localizedUrlLink}">
                <c:forEach var="dataAttributeEntry" items="${dataAttributes}">
                    <c:choose>
                        <c:when test="${dataAttributeEntry.key == 'aaLinkText'}">
                            data-aaLinkText="Image"
                        </c:when>
                        <c:otherwise>
                            data-${dataAttributeEntry.key}="${dataAttributeEntry.value}"
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </c:if> >

    <c:choose>
        <c:when test="${not empty localizedUrlLink}">
            </a>
        </c:when>
        <c:otherwise>
            </span>
        </c:otherwise>
    </c:choose>

</article>
