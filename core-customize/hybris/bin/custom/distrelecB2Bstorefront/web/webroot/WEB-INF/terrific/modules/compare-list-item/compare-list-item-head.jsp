<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>

<c:set var="phaseOut" value="false" />

<spring:message code="product.tabs.technical.attributes" var="sProductDetails" />
<spring:message code="product.scaledPrices.your.price" var="sPrice" />
<spring:message code="product.available" var="sAvailability" />
<spring:message code="compare.item.data.sheet" var="sDataSheet" />

<div class="tableGrid__product__sidebar__additional">
    <ul>
        <li class="blank"></li>
        <li class="compare-title__seperator compare-title__seperator__show">${sProductDetails}</li>
        <li class="items items__price">${sPrice}</li>
        <li class="items items__stock">${sAvailability}</li>
        <li class="items items__dataSheet">${sDataSheet}</li>
    </ul>
</div>
<mod:compare-list-attributes product="${product}"/>




