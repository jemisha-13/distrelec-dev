<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%-- read from PropertiesFactoryBean named 'propertiesFile' --%>
<spring:eval expression="@configurationService.configuration.getString('search.facets.open.count')" var="openFacetsCount" scope="application"/>
<fmt:parseNumber var="numberOfOpenFacets" type="number" value="${openFacetsCount}" />
<c:set var="FACET_VALUE_COLUMNS_COUNT" value="5" /> <%-- TODO: This should maybe be moved to properties file --%>
<spring:message code="base.close" var="sClose" text="Close" />
<spring:message code="search.nav.facet.value.min" var="sMin" text="Min" />
<spring:message code="search.nav.facet.value.max" var="sMax" text="Max" />
<spring:message code="search.nav.facet.value" var="sValue" text="value" />
<spring:message code="search.nav.facet.button.apply" var="sApply" text="Apply" />
<spring:message code="search.nav.facet.button.reset" var="sReset" text="Reset" />
<spring:message code="search.nav.facet.selection.invalid" var="sInvalid" text="Invalid selection"/>
<spring:message code="search.nav.facet.title" var="sTitle" text="Refine title"/>
<spring:url value="/_ui/all/media/img/spinner.gif" var="spinnerUrl" />
<c:if test="${!disableFilter}">

	<%-- Check for existence of non-category filters and multi-selection values --%>
	<c:set var="activeFilter" value="" />
	<c:set var="multiSelectedFacets" value="^" /> <%-- String of multi-selected facets, separated by "^" --%>
	<c:set var="allSelectedFacets" value="^" />
	<c:set var="thisFacetCode" value="" />
	<c:if test="${not empty searchPageData.filters}">
		<c:forEach items="${searchPageData.filters}" var="filter">
			<c:if test="${!filter.categoryFilter && !fn:contains(filter.facetCode,'categoryCodePathROOT')}">
				<c:set var="activeFilter" value="true" />
				<c:set var="allSelectedFacets" value="${allSelectedFacets}${filter.facetCode}^" />
				<c:if test="${filter.facetCode eq thisFacetCode}">
					<c:set var="multiSelectedFacets" value="${multiSelectedFacets}${filter.facetCode}^" />
				</c:if>
				<c:set var="thisFacetCode" value="${filter.facetCode}" />
			</c:if>
		</c:forEach>
	</c:if>

</c:if>
<span class="facet-view__title"> <spring:message code="text.plp.filter.menu" /> </span>
<div class="xmod-facets facet-view">
	<spring:theme code="search.nav.facet.toggleText" var="toggleFacetText"/>
	<ul class="other-facets facet-view__list" data-open-facets-count="${numberOfOpenFacets}" data-non-filter-parameters="${nonFilterParameters}" data-all-filter-parameters="${allFilterParameters}">
		<c:forEach items="${searchPageData.otherFacets}" var="facet" varStatus="facetCounter">
			<c:if test="${not empty facet.values and facet.isViable}">
				<c:set var="codeWithDelim" value="^${facet.code}^" />
				<li data-facet-index="${facetCounter.index}" class="facet-group type-${facet.type.value} is-collapsed${facet.hasSelectedElements ? ' is-filtered' : ''}${facet.hasSelectedElements && fn:contains(allSelectedFacets,codeWithDelim) ? ' is-selected' : ''}" data-facet-group-name="${facet.name}">
					<a href="#" class="facet-view__link" title="${facet.name}">
						<span class="facetName">
							${facet.name}
						</span>
						<i class="fa fa-angle-right"></i>
					</a>
					<c:choose>
						<c:when test="${facet.type.value eq 'slider'}">
							<c:set var="facetValue" value="${facet.values[0]}" />
							<c:choose>
								<c:when test="${not empty facetValue.propertyName}">
									<spring:theme code="${facetValue.propertyName}" arguments="${facetValue.propertyNameArguments}" argumentSeparator="${facetValue.propertyNameArgumentSeparator}" var="facetValueName" />
								</c:when>
								<c:otherwise>
									<c:set value="${facetValue.name}" var="facetValueName" />
								</c:otherwise>
							</c:choose>

						</c:when>

					</c:choose>
				</li>
			</c:if>
		</c:forEach>
	</ul>

	<div class="facet-view__more-scroll-wrapper hidden">
		<a href="#" class="facet-view__more-scroll-link" data-aalinktext="more filters" data-aainteraction="show filters">
			<i class="fas fa-chevron-circle-down"></i>
			<span class="title"> <spring:message code="cart.directorder.add.more" /> </span>
		</a>
	</div>

	<%-- We load the additional facet dropdown anyway, since we might need it later during ajax interaction --%>
	<div class="additional-facets${fn:length(searchPageData.lazyFacets) == 0 ? ' hidden' : ''}">
		<spring:theme code="search.nav.additionalFilter.title" text="Add additionalFilter" var="sAdditionalFilterTitle" />
		<spring:theme code="search.nav.facet.additional.error.boxTitle" text="Error!" var="sAdditionalFilterErrorBoxTitle" />
		<spring:theme code="search.nav.facet.additional.error.boxMessage" text="The Facet could not be retrieved!" var="sAdditionalFilterErrorBoxMessage" />
		<div class="hd">
			<h2 class="list-title additional-filter">${sAdditionalFilterTitle}</h2>
		</div>
		<select id="select-additional-facets" class="selectpicker select-additional-facets selectboxit-meta" name="additional-facets" 
			data-pretext='<spring:message code="search.nav.facet.addAdditional" />' 
			data-error-title="${sAdditionalFilterTitle}" 
			data-error-box-title="${sAdditionalFilterErrorBoxTitle}" 
			data-error-box-message="${sAdditionalFilterErrorBoxMessage}"
		>
			<option class="option option-default" value="default" ><spring:message code="search.nav.facet.addAdditional" /></option>
			<c:forEach items="${searchPageData.lazyFacets}" var="lazyFacet" varStatus="status">
				<option class="additional-facet-option" data-facet-name="${lazyFacet.name}" value="${lazyFacet.query.url}" data-index="${status.index}">${lazyFacet.name}</option>
			</c:forEach>
		</select>
	</div>
</div>

<div class="tooltip-hover hidden">
	<div class="tooltip-information">
		<spring:message code="search.nav.facet.tips.text" text="Use the SHIFT key to select a range of options" />
	</div>
	<div class="arrow-down"></div>
</div>	




<script id="tmpl-facet-active-filter" type="text/x-template-dotjs">

		<span>
			<span class="hidden facetValueName">{{= it.facetValueName }}</span>
			{{= it.name }}
		</span>
		<i></i>

</script>
<script id="tmpl-facet-group" type="text/x-template-dotjs">
<li class="facet-group type-{{=it.typeLowerCase}} {{= it.expansionStatus }}" data-facet-group-name="{{= it.name }}">
	<spring:theme code="search.nav.facet.toggleText" var="toggleFacetText" arguments="{{= it.name }}" text="{{=it.name}}" />
	<a href="#" class="show-more-link " title="${toggleFacetText}">
		<i></i>
	</a>
	<div class="facet-list facet-list-type-{{=it.typeLowerCase}}"
		data-facet-url-path="{{= it.currentQueryUrl }}" 
	>
		{{? it.type == 'CHECKBOX' || it.type == 'BOOLEAN_CHECKBOX'}}
			{{ var valuesCount = it.values.length; }}
			{{ var remainingValues = valuesCount; }}
			{{ var valuesPerColumn = valuesCount / ${FACET_VALUE_COLUMNS_COUNT}; }}
			{{ var columnNo = 1; }}
			<div class="facet-header">
				<h4>{{= it.name }}</h4>
				<a href="#" class="btn btn-close" title="${sClose}"></a>
			</div>
			<div class="facet-value-list-container border-top">
				<ul class="facet-value-list column-{{= columnNo }}">
		{{?}}

					{{~it.values :item:id}}
					{{? it.type == 'SLIDER'}}
					<div class="facet-item -type-slider"
						 data-facet-url-path="{{= it.currentQueryUrl }}"
						 data-facet-name="{{= it.code }}"
						 data-facet-name-visible="{{= it.name }}"
						 data-facet-type="{{= it.type }}"
					>
						<div class="bd">
							<div class="slider" data-abs-min="{{= item.absoluteMinValue }}" data-abs-max="{{= item.absoluteMaxValue }}" data-curr-min="{{= item.selectedMinValue }}" data-curr-max="{{= item.selectedMaxValue }}"></div>
						</div>
						<div class="ft base">
							<div class="cf">
								<div class="-left">
									<input type="text" class="slider-value curr-min" />
									<small class="currency"></small>
								</div>
								<div class="-right">
									<input type="text" class="slider-value curr-max" />
									<small class="currency"></small>
								</div>
							</div>
							<div class="field-msgs hidden">
								<div class="error"
									 data-error-1="<spring:theme code="search.nav.facet.slider.error.1" text="The entered value is not valid"/>"
									 data-error-2="<spring:theme code="search.nav.facet.slider.error.2" text="The entered value is not valid"/>"
								>
									<span class="message"></span>
									<i></i>
								</div>
							</div>
						</div>
					</div>
					{{?}}
					{{? it.type == 'BOOLEAN_CHECKBOX'}}
					<li class="facet-item -type-radio-button" data-facet-type="radio-button" data-facet-name="{{= it.name }}" data-facet-value-name="{{= item.facetValueName }}" data-filter-string="{{= item.filterString}}" data-facet-number="{{= id + 1}}">
						<a {{? item.isSelected == 'true'}} class="active"{{?}} href="{{= item.query.url }}">{{= item.facetValueName }}&nbsp;<span class="product-count"></span></a>
					</li>
					{{?}}
					{{? it.type == 'CHECKBOX'}}
					<li class="facet-item -type-checkbox" data-facet-type="checkbox" data-facet-name="{{= it.name }}" data-facet-value-name="{{= item.facetValueName }}" data-filter-string="{{= item.filterString}}" data-facet-number="{{= id + 1}}">
						<a {{? item.isSelected == 'true'}} class="active"{{?}}  href="{{= item.query.url }}">{{= item.facetValueName }}&nbsp;<span class="product-count"></span></a>
					</li>
					{{?}}
					{{? (id + 1 - valuesCount + remainingValues) % valuesPerColumn < 1 }}
					{{ remainingValues = valuesCount - id - 1; }}
					{{ valuesPerColumn = remainingValues / (${FACET_VALUE_COLUMNS_COUNT} - columnNo); }}
					{{ columnNo++; }}
				</ul>
				<ul class="facet-value-list column-{{= columnNo }}">
			{{?}} 
		{{~}}
		{{? it.type == 'CHECKBOX' || it.type == 'BOOLEAN_CHECKBOX'}}
				</ul>
			</div>
			<div class="button-container border-top">
				<div class="min-max-wrapper">
					<span class="min-max-label">${sMin}</span>
					<select id="select-min-value-${facet.type.value}" class="selectpicker select-min-value selectboxit-meta" name="min-value-${facet.type.value}">
						<option class="option option-default disabled" value="0" disabled="disabled" selected="selected">${sValue}</option>
						{{~it.values :item:id}}
							<option class="facet-min-max-option" value="{{= id + 1}}">{{= item.facetValueName}}</option>
						{{~}}
					</select>
					<span class="min-max-label">${sMax}</span>
					<select id="select-max-value-${facet.type.value}" class="selectpicker select-max-value selectboxit-meta" name="max-value-${facet.type.value}">
						<option class="option option-default disabled" value="0" disabled="disabled" selected="selected">${sValue}</option>
						{{~it.values :item:id}}
							<option class="facet-min-max-option" value="{{= id + 1}}">{{= item.facetValueName}}</option>
						{{~}}
					</select>
				</div>
				<div class="button-wrapper">
					<button type="submit" class="btn btn-primary btn-apply-filter disabled" disabled="disabled">${sApply}<i></i></button>
					<button type="button" class="btn btn-secondary btn-reset${facet.hasSelectedElements ? '"' : ' disabled" disabled="disabled"'}>${sReset}</button>
				</div>
			</div>
		{{?}}
	</div>
</li>
</script>

<script id="tmpl-additional-facet-dropdown-option" type="text/x-template-dotjs">
	<option value="{{= it.lazyFacetUrl }}" data-index="{{= it.index }}">{{= it.lazyFacetName }}</option>
</script>

<script id="additional-facet-strings" type="text/x-i18n-stringbundle">
	{
		FACET_SHOW_MORE_LINK:	    '<spring:theme code="search.nav.facetTitle" arguments="{{= it.name }}" text="{{=it.name}}" />'
		,FACET_VALUE_COUNT:	        '<spring:theme code="search.nav.facetValueCount" arguments="{{= it.count }}" text="{{= it.count }}" />'
	}
</script>
