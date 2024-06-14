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
            <!-- Render time (Homepage): ${currentDateTime}, cached for ${cachingTimeHomepage} seconds with key ${cachekey} -->

            <c:if test="${isOCI eq false}">
                <div class="container welcome-mat">
                    <section class="row welcome-mat__row">
                        <cms:slot var="feature" contentSlot="${slots['WelcomeMatSlot']}">
                            <cms:component component="${feature}" />
                        </cms:slot>
                    </section>
                </div>
            </c:if>

            <c:choose>
                <c:when test="${isOCI eq false}">
                    <div class="container home-container">
                        <section class="row home-container__row">
                            <div class="home-container__banner">
                                <cms:slot var="feature" contentSlot="${slots['BannerContentSlot']}">
                                    <cms:component component="${feature}" />
                                </cms:slot>                            
                            </div>
                            <div class="col-xl-4 home-container__extra-banners">
                                <div class="sub-banner upper">
                                    <cms:slot var="feature" contentSlot="${slots['BannerContentSlot-Hero1']}">
                                        <cms:component component="${feature}" />
                                    </cms:slot>    
                                </div>

                                <div class="sub-banner">
                                    <cms:slot var="feature" contentSlot="${slots['BannerContentSlot-Hero2']}">
                                        <cms:component component="${feature}" />
                                    </cms:slot>
                                </div>
                            </div>
                            <div class="col-xl-3 home-container__rnl">
                                <mod:register template="homepage"/>
                            <div class="home-quickorder">
                                <mod:cart-directorder template="component" skin="component"/>
                            </div>
                            </div>
                        </section>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="container">
                        <section class="row">
                            <div class="col-xl-7">
                                <cms:slot var="feature" contentSlot="${slots['CategoriesContentSlot']}">
                                    <cms:component component="${feature}" />
                                </cms:slot>
                            </div>
                        </section>
                    </div>
                </c:otherwise>
            </c:choose>
            <c:if test="${isOCI == false}">
                <div class="container feature-container">
                    <section class="row">
                        <cms:slot var="feature" contentSlot="${slots['MainContentSlot']}">
                            <cms:component component="${feature}" />
                        </cms:slot>
                    </section>
                </div>
            </c:if>
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
        <c:if test="${isOCI eq false}">
            <mod:lightbox-login-favorites />
        </c:if>

        <c:if test="${isReevooBrandVisible && isReevooActivatedForWebshop}">
            <div class="container">
            <reevoo-badge name="g_brand_percent_6" trkref=${trkrefid}></reevoo-badge>
            </div>
        </c:if>
    </div>
</views:page-default-home>
