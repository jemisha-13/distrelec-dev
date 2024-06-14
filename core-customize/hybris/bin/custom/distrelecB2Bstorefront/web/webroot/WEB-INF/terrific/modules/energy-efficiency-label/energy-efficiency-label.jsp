<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%--

<spring:message code="product.energy.efficiency.class" text="Energy efficiency class" var="sEEC" />
<spring:eval expression="@configurationService.configuration.getString('factfinder.json.suggest.url')" var="ffSearchUrl" scope="application" />

<c:if test="${not empty product}">
	<c:set var="productEnergyEfficiency" value="${product.energyEfficiency}" />
	<c:set var="productCode" value="${product.code}" />
	<c:set var="productManufacturer" value="${product.manufacturer}" />
	<c:set var="productType" value="${product.typeName}" />
	<c:set var="productEnergyPower" value="${product.energyPower}" />
</c:if>

<div class="energy-label">
	<span class="ico-energy ${productEnergyEfficiency}" title="${sEEC}&nbsp;${productEnergyEfficiency}" data-product-code="${productCode}" data-ff-url="${ffSearchUrl}" data-ff-channel="${ffsearchChannel}"><i></i></span>
	<div class="energy-label-popover hidden">
		<div class="energy-label-big">
			<div class="energy-label-efficiency ${productEnergyEfficiency}"><i>${productEnergyEfficiency}</i></div>
			<div class="energy-label-manufacturer">${productManufacturer}</div>
			<div class="energy-label-type">${productType}</div>
			<div class="energy-label-power"><span>${productEnergyPower}</span> kWh / 1000 h</div>
		</div>
	</div>
</div>

--%>
