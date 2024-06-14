<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="home-popular-categories__content">
    <h1 class="home-popular-categories__title"><spring:message code="home.title.popular.categories" /></h1>
    <ul class="category-parent">
        <c:set var="seeAllIndex" value="0"/>
        <c:forEach items="${recommendedCategories}" var="category" varStatus="index">
            <li class="category-parent__item">
                <a class="category-parent__item__moblink moblink" href="${category.url}" title="${category.name}"
                   data-aabuttonpos="${index.index + 1}"
                   data-aalinktext="${category.nameEN}"
                   data-aasectiontitle="What are you looking for today?"
                   data-aasectionpos="c1r1"
                   data-aatype="homepage-interaction"
                >
                    <img class="img-responsive" data-src="${category.image.url}" alt="${category.name}" />
                </a>
                <a class="home-popular-categories__item" href="${category.url}" title="${category.name}"
                  data-aabuttonpos="${index.index + 1}"
                  data-aalinktext="${category.nameEN}"
                  data-aasectiontitle="What are you looking for today?"
                  data-aasectionpos="c1r1"
                  data-aatype="homepage-interaction"
                >${category.name} <i class="fa fa-angle-right"></i></a>
            </li>
            <c:if test="${index.last}">
                <c:set var="seeAllIndex" value="${index.index + 2}" />
            </c:if>
        </c:forEach>
        <li>
            <a class="home-popular-categories__item__see-all" href="/categories"
               data-aabuttonpos="${seeAllIndex}"
               data-aalinktext="See all"
               data-aasectiontitle="What are you looking for today?"
               data-aasectionpos="c1r1"
               data-aatype="homepage-interaction">
            <spring:message code="home.title.see.all" /></a></li>
    </ul>
</div>
