<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>

<section class="parallax ${htmlClasses} lazy-background" data-bg-src="${backgroundImage.url}">

    <div class="parallax__content">

        <div class="parallax__content__border"></div>

        <div class="container ${htmlClasses}__grouped">
            <div class="row ${htmlClasses}__grouped__row">
                <div class="col-12 ${htmlClasses}__heading">
                    <h2 class="heading">${title}

                        <c:if test="${not empty currentLogoImage.url}">
                            <span class="heading__logo">
                                <img src="${currentLogoImage.url}">
                            </span>
                        </c:if>

                        <c:choose>
                        <c:when test="${not empty topLinkUrl}">
                            <a href="${topLinkUrl}"
                        </c:when>
                        <c:otherwise>
                        <span
                        </c:otherwise>
                        </c:choose>

                                class="heading__link"
                                <c:forEach var="dataAttributeEntry" items="${topLinkDataAttributes}">
                                    data-${dataAttributeEntry.key}="${dataAttributeEntry.value}"
                                </c:forEach>
                        > ${topLinkText}

                        <c:choose>
                            <c:when test="${not empty topLinkUrl}">
                                </a>
                            </c:when>
                            <c:otherwise>
                                </span>
                        </c:otherwise>
                        </c:choose>

                        <span class="heading__subtitle">${subtitle}</span>

                    </h2>
                </div>

                <div class="col-12 ${htmlClasses}-container">
                    <div class="row ${htmlClasses}-container__row">
                        <c:forEach var="component" items="${components}">
                            <div class="${itemHtmlClasses} ${htmlClasses}-container__col ${htmlClasses}-container__row__grouped">
                                <cms:component component="${component}" evaluateRestriction="true"/>
                            </div>
                        </c:forEach>
                    </div>
                </div>

                <c:if test="${not empty bottomLinkText}">
                    <div class="col-12 bottom__link-container">
                        <a class="bottom__link" href="${bottomLinkUrl}"
                                <c:forEach var="dataAttributeEntry" items="${bottomLinkDataAttributes}">
                                    data-${dataAttributeEntry.key}="${dataAttributeEntry.value}"
                                </c:forEach>
                        >${bottomLinkText}</a>
                    </div>
                </c:if>
            </div>
        </div>

    </div>

</section>
