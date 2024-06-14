<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div class="sticky-level-1">
    <div class="sticky-level-1__container">
        <mod:logo htmlClasses="mod-logo--inter"/>
        <mod:metahd tag="nav" hasCompareProducts="${hasCompareProducts}" />
    </div>
</div>
<div class="sticky-level-2">
    <div class="sticky-level-2__container">
        <div class="ct__menu">
            <cms:slot var="feature" contentSlot="${slots['MainNav']}">
                <cms:component component="${feature}"/>
            </cms:slot>
            <mod:servicenav tag="nav" template="" attributes="data-auto-show-lightbox='${isInitialVisit}'" isInitialVisit="${isInitialVisit}" />
        </div>
        <mod:metahd-suggest htmlClasses="gu-12" />
    </div>
</div>
