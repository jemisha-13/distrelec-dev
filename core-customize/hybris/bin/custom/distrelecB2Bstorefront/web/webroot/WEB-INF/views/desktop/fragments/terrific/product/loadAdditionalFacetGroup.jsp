<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>

<c:set var="phaseOut" value="false" />
<spring:message code="product.image.missing" text="Image not found" var="sImageMissing"/>

<json:object>
	<json:property name="code">
		${facet.code}
	</json:property>
	<json:property name="name">
		${facet.name}
	</json:property>
	<json:property name="typeLowerCase">
		${facet.type.value}
	</json:property>
	<json:property name="type">
		${fn:toUpperCase(facet.type.value)}
	</json:property>
	<c:choose>
		<c:when test="${facet.hasSelectedElements}">
			<json:property name="expansionStatus" value="is-expanded" />
		</c:when>
		<c:otherwise>
			<json:property name="expansionStatus" value="" />
		</c:otherwise>
	</c:choose>
	<json:array name="values" items="${facet.values}" var="facetValue">
		<json:object>
			<json:object name="query">
				<json:property name="url">
					<c:url value="${facetValue.query.url}" />
				</json:property>
			</json:object>
			<json:property name="name" value="${facetValue.name}" />
			<json:property name="facetValueName">
				<c:choose>
					<c:when test="${not empty facetValue.propertyName}">
						<spring:theme code="${facetValue.propertyName}" arguments="${facetValue.propertyNameArguments}" argumentSeparator="${facetValue.propertyNameArgumentSeparator}" />
					</c:when>
					<c:otherwise>
						${facetValue.name}
					</c:otherwise>
				</c:choose>
			</json:property>
			<json:property name="filterString" value="${facetValue.queryFilter}" />
			<json:property name="count" value="${facetValue.count}" />
			<json:property name="isSelected" >
				${facetValue.selected}
			</json:property>
			<c:if test="${facet.type.value eq 'slider'}">
				<json:property name="absoluteMinValue" value="${facetValue.absoluteMinValue}" />
				<json:property name="absoluteMaxValue" value="${facetValue.absoluteMaxValue}" />
				<json:property name="selectedMinValue" value="${facetValue.selectedMinValue}" />
				<json:property name="selectedMaxValue" value="${facetValue.selectedMaxValue}" />
			</c:if>
		</json:object>
	</json:array>
</json:object>
