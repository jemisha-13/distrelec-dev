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

<views:page-default-md-full pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-wide dist-cmspage dist-cmspage--brand-one">

    <div class="page-wrapper">
        <cms:slot var="feature" contentSlot="${slots['brand1BannerContentSlot']}">
            <cms:component component="${feature}" />
        </cms:slot>

        <cms:slot var="feature" contentSlot="${slots['brand1MainContentSlot']}">
            <cms:component component="${feature}" />
        </cms:slot>

        <div class="container">
            <div class="row">
                <cms:slot var="feature" contentSlot="${slots['brand1Component11ContentSlot1']}">
                    <cms:component component="${feature}" />
                </cms:slot>
            </div>
        </div>

        <div class="container">
            <div class="row">
                <cms:slot var="feature" contentSlot="${slots['brand1Component11ContentSlot2']}">
                    <cms:component component="${feature}" />
                </cms:slot>
            </div>
        </div>
                
        <cms:slot var="feature" contentSlot="${slots['brand1BottomContentSlot']}">
            <cms:component component="${feature}" />
        </cms:slot>
    </div>


</views:page-default-md-full>



