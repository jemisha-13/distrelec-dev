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

<views:page-default-md-full pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-wide dist-cmspage dist-cmspage--cms-three">

    <div class="container page-wrapper">

        <div class="row">

            <div class="col-md-8 left-content">
                <cms:slot var="feature" contentSlot="${slots['cms3_BannerContentSlot']}">
                    <cms:component component="${feature}" />
                </cms:slot>

                <cms:slot var="feature" contentSlot="${slots['cms3_MainContentSlot']}">
                    <cms:component component="${feature}" />
                </cms:slot>

                <cms:slot var="feature" contentSlot="${slots['cms3_ImageVideoContentSlot']}">
                    <cms:component component="${feature}" />
                </cms:slot>
                <cms:slot var="feature" contentSlot="${slots['cms3_BottomContentSlot']}">
                    <cms:component component="${feature}" />
                </cms:slot>
            </div>

            <div class="col-md-4 right-content">

                <cms:slot var="feature" contentSlot="${slots['cms3_NavTextContentSlot']}">
                    <cms:component component="${feature}" />
                </cms:slot>

            </div>

        </div>

    </div>

</views:page-default-md-full>


