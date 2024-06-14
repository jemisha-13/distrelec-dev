<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="oscache" uri="http://www.opensymphony.com/oscache" %>
<%@ taglib prefix="nam" uri="/WEB-INF/tld/namicscommercetags.tld" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="isOCI" value="false" />
<sec:authorize access="hasRole('ROLE_EPROCUREMENTGROUP')">
    <c:set var="isOCI" value="true" />
</sec:authorize>

<views:page-default-home pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-home skin-layout-wide cmscontentpage">

    <h1>Distrelec - Only</h1>

    <div class="page-wrapper">
        <div class="container">
            <div id="breadcrumb" class="breadcrumb">
                <div class="spacer"></div>
            </div>
            <mod:global-messages/>
        </div>
        <div class="container">
            <div class="col-md-12">
                <cms:slot var="feature" contentSlot="${slots['TitleContent']}">
                    <cms:component component="${feature}" />
                </cms:slot>
            </div>
        </div>

        <c:set var="cachekey" value="content-${cmsPage.pk}-${cachingKeyHomepage}-${user.uid}" />
        <c:if test="${param.flush eq 'homepage'}">
            <oscache:flush key="${cachekey}" scope="application" language="${currentLanguage.isocode}" />
        </c:if>
        <oscache:cache key="${cachekey}" time="${cachingTimeHomepage}" scope="application" language="${currentLanguage.isocode}">
            <!-- Render time (Homepage): ${currentDateTime}, cached for ${cachingTimeHomepage} seconds with key ${cachekey} -->
            <div class="container">
                <c:if test="${isOCI eq false}">
                    <section class="row">
                            <div class="col-xl-3 home-register-component">
                                <mod:register template="homepage"/>
                            </div>
                            <div class="col-xl-9 home-slider">
                                <cms:slot var="feature" contentSlot="${slots['BannerContentSlot']}">
                                    <cms:component component="${feature}" />
                                </cms:slot>
                            </div>
                    </section>
                </c:if>
                <section class="row">
                    <div class="col-xl-7">
                        <cms:slot var="feature" contentSlot="${slots['CategoriesContentSlot']}">
                            <cms:component component="${feature}" />
                        </cms:slot>
                    </div>
                    <c:if test="${isOCI == false}">
                        <div class="col-xl-5 home-quickorder">
                            <mod:cart-directorder template="component" skin="component"/>
                        </div>
                    </c:if>
                </section>
                <c:if test="${isOCI == false}">
                    <section class="row">
                        <cms:slot var="feature" contentSlot="${slots['MainContentSlot']}">
                            <cms:component component="${feature}" />
                        </cms:slot>
                    </section>
                </c:if>
            </div>
            <div class="common-component">
                <cms:slot var="feature" contentSlot="${slots['BlogContentSlot']}">
                    <cms:component component="${feature}" />
                </cms:slot>
            </div>
            <div class="container">
                <cms:slot var="feature" contentSlot="${slots['BottomContentSlot']}">
                    <cms:component component="${feature}" />
                </cms:slot>
            </div>
        </oscache:cache>
    </div>
</views:page-default-home>
