<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<spring:message code="freight-cost.link.text" var="sFreightLinkTitle" />
<spring:message code="freight-cost.ariaText" var="sAriaText" />

<h3 class="skin-freight-cost-pdp__title">
    <i class="fa fa-truck" aria-hidden="true"></i> <spring:message code="freight-cost.text" arguments="${freeShippingValue.formattedValue}" argumentSeparator=";"/>
</h3>

<div class="skin-freight-cost-pdp__options skin-freight-cost-pdp__options--${currentCountry.isocode}">

    <span class="skin-freight-cost-pdp__text"><spring:message code="freight-cost.text.options" /></span>


    <div class="freight-popover">
        <div class="skin-freight-cost-pdp__pricing skin-freight-cost-pdp__pricing--${currentCountry.isocode}">
            <div class="pricing-close">
                <span class="close"><i class="fa fa-times" aria-hidden="true"></i></span>
            </div>
            <div class="pricing-border">&nbsp;</div>
            <div class="pricing-content">&nbsp;</div>
        </div>
        <a title="${sFreightLinkTitle}" aria-label="${sAriaText}" class="skin-freight-cost-pdp__options__link" href="/cms/ordering"><i class="fa fa-info-circle" aria-hidden="true"></i></a>
    </div>

</div>
