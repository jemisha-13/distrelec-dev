<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<spring:eval expression="new java.util.Locale('en')" var="enLocale" />

<div class="what-looking-today what-looking-today-OCI">
    <hr class="divider"/>
    <h2 class="heading"> ${title} </h2>

    <ul class="categories__list categories__list-OCI">
        <c:forEach var="category" items="${categories}" varStatus="status">
            <li class="categories__list__item">
                <a href="${category.nameSeo}/c/${category.code}"
                   class="categories__list__item__link mat-button mat-button__normal--action-dark-grey"
                   data-aabuttonpos="${status.count}" data-aalinktext="${category.getName(enLocale)}"

                        <c:forEach var="dataAttributeEntry" items="${dataAttributes}">
                            data-${dataAttributeEntry.key}="${dataAttributeEntry.value}"
                        </c:forEach>

                >
                        ${category.name}
                </a>
            </li>
        </c:forEach>

        <li class="categories__list__item home__categories__list__item--borderless  home__categories__list-OCI">
            <a href="/categories"
               class="categories__list__item__link mat-button mat-button__normal--action-dark-grey categories__list__item__link-more"
               data-aaSectionPos="c1r2" data-aaSectionTitle="What are you looking for today" data-aaLinkText="More" aaType="homepage-interaction">
                <span class="">${moreLinkText} <i class="fas fa-angle-right"></i></span>
            </a>
        </li>
    </ul>

</div>
