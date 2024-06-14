<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<spring:theme code="product.list.no.result" text="Unfortunately, your query returned no relevant results:"
              var="sListNoResult" arguments="${request.getParameter('digitalDataLayerTerm')}"/>

<c:set var="redirectedFromFilteredSearch" value="${request.getParameter('RedirectedFromSearch')}"/>

<%-- set isOCI var --%>
<c:set var="isOCI" value="false"/>
<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
    <c:set var="isOCI" value="true"/>
</sec:authorize>

<views:page-default-md-full pageTitle="${pageTitle}"
                            bodyCSSClass="mod mod-layout skin-layout-responsive skin-layout-category skin-layout-wide category-page cmscontentpage">

    <div class="md-system page-wrapper page" data-isoci="${isOCI}">

        <div class="container">

            <c:if test="${empty eolMessage}">
                <mod:global-messages/>
            </c:if>
            <c:if test="${not empty eolMessage}">
                <mod:global-messages headline="" body="${eolMessage}" type="information" widthPercent="100%"
                                     displayIcon="true"/>
            </c:if>

        </div>

        <div id="breadcrumb" class="breadcrumb page__breadcrumb">
            <mod:breadcrumb template="product" skin="product"/>
        </div>

        <div class="container">

            <div class="plp-content__top">

                <fmt:formatNumber type="number" value="${searchPageData.pagination.totalNumberOfResults}"
                                  var="matchedProductsCount"/>

                <div class="row">
                    <c:if test="${redirectedFromFilteredSearch}">
                        <div id="zero-category-search" class="col-sm-12 banner-wrapper__noresult">
                                ${sListNoResult}
                        </div>
                    </c:if>
                </div>

                <c:choose>
                    <c:when test="${empty categoryPageData.sourceCategory.name}">
                        <h1 class="page-title--heading col page-title page-title--with-count">
                            <a href="#" class="page-title__back-navigation"><i class="fa fa-arrow-left"></i></a>
                                ${metaData.h1}
                        </h1>
                    </c:when>
                    <c:otherwise>
                        <h1 class="page-title--heading col page-title page-title--with-count">
                            <a href="#" class="page-title__back-navigation"><i class="fa fa-arrow-left"></i></a>
                                ${categoryPageData.sourceCategory.name}
                        </h1>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Category SEO data  -->
            <mod:seo template="category" skin="category"/>

            <c:if test="${!showCategoriesOnly}">
                <div class="row clear">
                    <div class="gu-12 full-width">
                        <mod:productlist-controllbar pageData="${searchPageData}"/>
                    </div>
                </div>
            </c:if>

            <c:set var="contentSecondCol">
                <c:choose>
                    <c:when test="${showCategoriesOnly}">
                        <cms:slot var="feature" contentSlot="${slots['Content']}">
                            <cms:component component="${feature}"/>
                        </cms:slot>
                    </c:when>
                    <c:otherwise>
                        <mod:productlist searchPageData="${searchPageData}"/>
                        <mod:productlist-pagination searchPageData="${searchPageData}"/>
                    </c:otherwise>
                </c:choose>
            </c:set>

            <div>
                    ${contentSecondCol}
            </div>

        </div>

        <div class="container">

            <mod:mesh-linking template="categorylinks" sourceCategoryName="${categoryPageData.sourceCategory.name}"/>

            <div>
                <c:if test="${not empty metaData.contentTitle}">
                    <h1 class="base page-title">${metaData.contentTitle}</h1>
                </c:if>
                <c:if test="${not empty metaData.contentDescription}">
                    <p class="fontsize13px">${metaData.contentDescription}</p>
                </c:if>
            </div>

            <cms:slot var="feature" contentSlot="${slots['CarouselTop']}">
                <cms:component component="${feature}"/>
            </cms:slot>
            <cms:slot var="feature" contentSlot="${slots['CampaignContent']}">
                <cms:component component="${feature}"/>
            </cms:slot>
            <cms:slot var="feature" contentSlot="${slots['CarouselBottom']}">
                <cms:component component="${feature}"/>
            </cms:slot>

        </div>

    </div>

</views:page-default-md-full>
