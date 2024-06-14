<%@ tag body-content="scriptless" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="header" tagdir="/WEB-INF/tags/desktop/common/header" %>
<%@ taglib prefix="footer" tagdir="/WEB-INF/tags/desktop/common/footer" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/desktop/cart" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="namics" uri="/WEB-INF/tld/namicscommercetags.tld"%>
<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags"%>
<%@ taglib prefix="views" tagdir="/WEB-INF/tags/terrific/views"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="nam" uri="/WEB-INF/tld/namicscommercetags.tld" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@ attribute name="pageTitle" required="false" rtexprvalue="true"%>
<%@ attribute name="bodyCSSClass" required="false" rtexprvalue="true"%>

<views:document-default pageTitle="${pageTitle}" bodyCSSClass="${bodyCSSClass}">

    <c:set var="isInitialVisit" value="false" />
    <c:if test="${shopSettingsCookie.cookieMessageConfirmed == false || empty shopSettingsCookie}">
        <c:set var="isInitialVisit" value="true" />
    </c:if>

    <header id="header">
        <div class="ct" id="wt_refpoint">
            <mod:stickyheader />
            <mod:warning-component />
        </div>
    </header>

    <div class="md-system">
        <div class="md-content locale-${currentCountry.isocode}-${currentLanguage.isocode}">
            <jsp:doBody />
        </div>
    </div>

    <footer class="footer" id="footer">
        <cms:slot var="distFooterComponent" contentSlot="${slots.FooterLinks}">
            <cms:component component="${distFooterComponent}"/>
        </cms:slot>
    </footer>
</views:document-default>
