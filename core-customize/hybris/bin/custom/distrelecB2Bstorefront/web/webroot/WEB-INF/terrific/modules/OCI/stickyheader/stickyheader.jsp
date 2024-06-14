<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div class="sticky-level-1">
    <div class="sticky-level-1__container">
        <mod:logo/>
        <mod:metahd tag="nav" hasCompareProducts="${hasCompareProducts}" />
    </div>
</div>
<div class="sticky-level-2">
    <div class="sticky-level-2__container">
        <div class="ct__menu">
            <li id="js-products-dropdown" class="e1 products-dropdown count_e1_0">
                <a class="e1-button" title="${level1node.title}" data-aainteraction='navigation' data-location='top nav' data-parent-link='${fn:toLowerCase(component.rootNavigationNode.getTitle(enLocale))}' data-link-value='${fn:toLowerCase(level1node.getTitle(enLocale))}'>
                    <div class="level1nodeTitle" data-entitle="${enLevel1nodeTitle}"><i class="fa fa-bars" aria-hidden="true"></i> <spring:message code="metahd.compare.products" /> </div>
                </a>
        
                <cms:slot var="feature" contentSlot="${slots['MainCategoryNav']}">
                    <cms:component component="${feature}"/>
                </cms:slot>
            </li>
            <cms:slot var="feature" contentSlot="${slots['MainManufacturerNav']}">
                <cms:component component="${feature}"/>
            </cms:slot>
            <cms:slot var="feature" contentSlot="${slots['MainNav']}">
				<cms:component component="${feature}"/>
            </cms:slot>
            <mod:servicenav tag="nav" template="" attributes="data-auto-show-lightbox='${isInitialVisit}'" isInitialVisit="${isInitialVisit}" />
        </div>
        <mod:metahd-suggest htmlClasses="gu-12" />
    </div>
</div>
