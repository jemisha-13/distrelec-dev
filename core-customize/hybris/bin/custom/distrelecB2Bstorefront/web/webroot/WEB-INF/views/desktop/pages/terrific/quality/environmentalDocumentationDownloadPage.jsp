<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<views:page-default pageTitle="${pageTitle}" bodyCSSClass="mod mod-layout skin-layout-wide skin-quality-page skin-layout-nonavigation skin-layout-full-width" >

    <div class="ct">
        <div class="row">
            <div id="breadcrumb" class="breadcrumb">
                <mod:breadcrumb/>
            </div>
            

                <cms:slot var="feature" contentSlot="${slots.TopContent}">
                    <cms:component component="${feature}"/>
                </cms:slot>

                   <mod:quality-page />

                <cms:slot var="feature" contentSlot="${slots.BottomContent}">
                    <cms:component component="${feature}"/>
                </cms:slot>
            </div>
        </div>
    </div>
</views:page-default>