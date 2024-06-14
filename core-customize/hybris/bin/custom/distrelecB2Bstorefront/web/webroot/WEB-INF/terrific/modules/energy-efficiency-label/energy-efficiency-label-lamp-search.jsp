<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<spring:message code="product.energy.efficiency.info" text="Energy efficiency information" var="sEEC" />
<spring:eval expression="@configurationService.configuration.getString('factfinder.json.suggest.url')" var="ffSearchUrl" scope="application" />

<c:if test="${not empty product}">
	<c:set var="energyClassesBuiltInLed" value="${product.energyClassesBuiltInLed}" />
	<c:set var="energyClassesFitting" value="${product.energyClassesFitting}" />
	<c:set var="energyClassesIncludedBulb" value="${product.energyClassesIncludedBulb}" />
</c:if>

<div class="energy-label">
	<span class="ico-energy L" 
		  title="${sEEC}" data-energy-classes-led="${energyClassesBuiltInLed}" data-energy-classes-bulb="${energyClassesFitting}" data-product-code="${productCode}" data-ff-url="${ffSearchUrl}" data-ff-channel="${ffsearchChannel}" data-eel-top-text="${energyTopText}" data-eel-bottom-text="${energyBottomText}" data-energy-classes-includedbulb="${energyClassesIncludedBulb}"><i></i></span>
	<div class="energy-label-popover hidden">
		<div class="energy-label-big lamp">
			<div class="energy-label-text-top">

				<span class="eel-top-text eel-top-text--ledbulb hidden">
					<spring:message code='energy.label.top.led.and.sockets'/>
				</span>
				<span class="eel-top-text eel-top-text--led hidden">
					<spring:message code='energy.label.top.led.built-in.lamp'/>
				</span>
				<span class="eel-top-text eel-top-text--bulb hidden">
					<spring:message code='energy.label.top.bulb.energy.class'/>
				</span>

			</div>
			<div class="energy-label-manufacturer" title="${productManufacturer}">${productManufacturer}</div>
			<div class="energy-label-type" title="${productType}">${productType}</div>
			<div class="energy-label-text-bottom">

				<span class="energy-class-label-wrapper">
                    <span class="energy-class-text">

						<span class="eel-bottom-text eel-bottom-text--includedbulb hidden">
							<spring:message code='energy.label.bottom.bulb.energy.class' arguments="${productEnergyEfficiency}" />
						</span>
						<span class="eel-bottom-text eel-bottom-text--lednotincludedbulb hidden">
							<spring:message code='energy.label.bottom.led.cannot.change'/>
						</span>

                    </span>
					<span class="eel-bottom-text eel-bottom-text--arrow hidden">
						<span class="energy-class-arrow">
							<span class="energy-class-arrow-text hidden">${energyClassesIncludedBulb}</span>
							<svg width="57" height="23" xmlns="http://www.w3.org/2000/svg">
							 <defs>
							  <style type="text/css">
							   <![CDATA[
								.status-arrow {fill:#fff;fill-rule:nonzero}
							   ]]>
							  </style>
							 </defs>
								<path id="svg_6" d="m0.39706,0.38059c0,0 44.74708,0 44.74708,0c0,0 10.89494,10.89494 10.89494,10.89494c0,0 -10.50584,11.28405 -10.50584,11.28405c0,0 -45.13619,0.1297 -45.13619,0.1297c0,0 0,-22.30869 0,-22.30869l0.00001,0z" class="status-arrow"/>
								<text class="svg-text" x="10" y="17">${energyClassesIncludedBulb}</text>
							</svg>
						</span>
					</span>
                </span>

			</div>
		</div>
	</div>
</div>