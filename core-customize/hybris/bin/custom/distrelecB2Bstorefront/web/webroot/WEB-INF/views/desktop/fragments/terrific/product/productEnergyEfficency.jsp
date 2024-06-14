<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>

<json:object>
	<json:property name="efficency" value="${productEnergyEfficency.efficency}" />
	<json:property name="power" value="${productEnergyEfficency.power}" />
	<json:property name="type" value="${productEnergyEfficency.type}" />
	<json:property name="manufacturer" value="${productEnergyEfficency.manufacturer}" />
</json:object>

