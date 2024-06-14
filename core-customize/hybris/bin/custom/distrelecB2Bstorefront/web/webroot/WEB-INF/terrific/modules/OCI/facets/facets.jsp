<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>

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

	<div class="xmod-filter__title">
			<span><i class="fa fa-filter" aria-hidden="true"></i> ${sTitle}</span>
	</div>
	<div class="xmod-filter">
		<div class="hd">
			<h2 class="list-title"><spring:theme code="search.nav.title" text="Filter" /></h2>
			<a href="${searchPageData.removeFiltersURL}" class="clear-link ${empty activeFilter ? ' hidden' : ''}"><spring:theme code="search.nav.removeAllFilter" text="Remove" /> <span class="clear-sign">&#215;</span></a>
		</div>
		<spring:theme code="search.nav.facet.loadProducts.error.boxTitle" var="loadProductsForFacetErrorTitle"/>
		<spring:theme code="search.nav.facet.loadProducts.error.boxMessage" var="loadProductsForFacetErrorMessage"/>
		<div class="bd" data-load-products-error-title="${loadProductsForFacetErrorTitle}" data-load-products-error-message="${loadProductsForFacetErrorMessage}">
			<c:if test="${not empty searchPageData.filters || not empty searchPageData.freeTextSearch}">
				<ul>
					<c:set var="prevFilter" value="" />
					<c:set var="allFilterParameters" value="" />
					<c:forEach items="${searchPageData.filters}" var="filter">
						<c:set var="allFilterParameters" value="${allFilterParameters}&${filter.filterString}" />
						<c:if test="${!filter.categoryFilter && !fn:contains(filter.facetCode,'categoryCodePathROOT')}"> <%-- DISTRELEC-6414 - Do not display categories in filter box --%>
							<c:set var="codeWithDelim" value="^${filter.facetCode}^" />
							<c:set var="isMultiSelected" value="${fn:contains(multiSelectedFacets,codeWithDelim)}" />
							<c:if test="${!(isMultiSelected && filter.facetCode == prevFilter)}"> 
								<c:set var="prevFilter" value="${filter.facetCode}" />
								<li class="facet-item">
									<c:url value="${filter.removeQuery.url}" var="removeQueryUrl"/>
									<c:if test="${isMultiSelected}">
										<c:set var="removeQueryUrl" value="${fn:replace(removeQueryUrl,fn:substringBefore(filter.filterString,'='),'_dummy')}" />
									</c:if>
									<a class="facet-link filterBoxElement " href="${removeQueryUrl}"
										data-facet-code="${filter.facetCode}"
										data-facet-value-name="${filter.facetValueCode}"
										data-filter-string="${filter.filterString}"
										>
										<span>
											<c:choose>
												<c:when test="${not empty filter.facetValuePropertyName}">
													<spring:theme code="${filter.facetValuePropertyName}" arguments="${filter.facetValuePropertyNameArguments}" argumentSeparator="${filter.facetValuePropertyNameArgumentSeparator}" var="facetValueName" />
												</c:when>
												<c:otherwise>
													<c:set value="${filter.facetValueName}" var="facetValueName" />
												</c:otherwise>
											</c:choose>
											<span class="hidden facetValueName">${facetValueName}</span>
											<c:choose>
												<c:when test="${(filter.type.value eq 'checkbox' || filter.type.value eq 'slider') && !isMultiSelected}">
													<spring:theme code="search.nav.appliedFacet" arguments="${filter.facetName}^${facetValueName}" argumentSeparator="^"/>
												</c:when>
												<c:otherwise>
													<spring:theme code="search.nav.appliedMultiSelectFacet" arguments="${filter.facetName}" />
												</c:otherwise>
											</c:choose>
										</span>
										<i></i>
									</a>
								</li>
							</c:if>
						</c:if>
					</c:forEach>
				</ul>
			</c:if>
			<span class="empty-filter-msg${activeFilter eq 'true' ? ' hidden' : ''}"><spring:theme code="search.nav.noFilter" text="No Filter" /></span>
		</div>
	</div>
</c:if>

<div class="xmod-facets">
	<spring:theme code="search.nav.facet.toggleText" var="toggleFacetText"/>
	<ul class="other-facets" data-open-facets-count="${numberOfOpenFacets}" data-non-filter-parameters="${nonFilterParameters}" data-all-filter-parameters="${allFilterParameters}">
		<c:forEach items="${searchPageData.otherFacets}" var="facet" varStatus="facetCounter">
			<c:if test="${not empty facet.values and facet.isViable}">
				<c:set var="codeWithDelim" value="^${facet.code}^" />
				<li class="facet-group type-${facet.type.value} is-collapsed${facet.hasSelectedElements ? ' is-filtered' : ''}${facet.hasSelectedElements && fn:contains(allSelectedFacets,codeWithDelim) ? ' is-selected' : ''}" data-facet-group-name="${facet.name}">
					<a href="#" class="show-more-link" title="${toggleFacetText}">
						<span class="facetName">
							${facet.name}
						</span>
						<i></i>
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
							<div class="facet-list facet-list-type-slider">
								<div class="facet-item -type-slider" 
									data-facet-url-path="${searchPageData.currentQuery.url}" 
									data-facet-name="${facet.code}" 
									data-facet-name-visible="${facet.name}" 
									data-facet-type="${facet.type.value}"
								>
									<div class="bd">
										<div class="slider" data-abs-min="${facetValue.absoluteMinValue}" data-abs-max="${facetValue.absoluteMaxValue}" data-curr-min="${facetValue.selectedMinValue}" data-curr-max="${facetValue.selectedMaxValue}"></div>
									</div>
									<div class="ft base">
										<div class="cf">
											<div class="-left">
												<input type="text" class="slider-value curr-min" />
												<small class="currency">${facet.unit}</small>
											</div>
											<div class="-right">
												<input type="text" class="slider-value curr-max" />
												<small class="currency">${facet.unit}</small>
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
							</div>
						</c:when>
						<c:otherwise>
							<c:set var="valuesCount" value="${fn:length(facet.values)}" />
							<c:set var="remainingValues" value="${valuesCount}" />
							<c:set var="valuesPerColumn" value="${valuesCount / FACET_VALUE_COLUMNS_COUNT}" />
							<c:set var="columnNo" value="1" />
							<div class="facet-list facet-list-type-${facet.type.value}" data-facet-url-path="${searchPageData.currentQuery.url}">
								<div class="facet-header">
									<h4>${facet.name}</h4>
									<a href="#" class="btn btn-close" title="${sClose}"></a>
								</div>
								<div class="facet-value-list-container border-top">
									<ul class="facet-value-list column-${columnNo}">
										<c:forEach items="${facet.values}" var="facetValue" varStatus="facetValueStatus">
											<c:choose>
												<c:when test="${not empty facetValue.propertyName}">
													<spring:theme code="${facetValue.propertyName}" arguments="${facetValue.propertyNameArguments}" argumentSeparator="${facetValue.propertyNameArgumentSeparator}" var="facetValueName" />
												</c:when>
												<c:otherwise>
													<c:set value="${facetValue.name}" var="facetValueName" />
												</c:otherwise>
											</c:choose>
				
											<c:choose>
												<c:when test="${(facet.type.value eq 'boolean_checkbox')}">
													<li class="facet-item -type-radio-button ${facetValue.selected ? 'active' : ''}"
														data-facet-type="${facet.type.value}" 
														data-facet-name="${facet.name}" 
														data-facet-value-name="${facetValueName}"
														data-facet-number="${facetValueStatus.count}"
														data-filter-string="${facetValue.filterString}"
													>
														
														<c:set var="facetValueNameWt" value="${fn:replace(facetValueName, ' ', '-')}" />
														<c:set var="facetValueNameWtLower" value="${fn:toLowerCase(facetValueNameWt)}" />
														<c:set var="facetValueNameDisplay" value="${fn:replace(facetValueName, '<br/>', ', ')}" />
														<c:if test="${fn:length(facetValueNameDisplay) > 70}">
															<c:set var="facetValueNameDisplay" value="${fn:substring(facetValueNameDisplay, 0, 65)}..." />
														</c:if>
														
														<c:url value="${facetValue.query.url}" var="facetValueQueryUrl"/>
														<a href="${facetValueQueryUrl}" class="${facetValue.selected ? 'active' : ''}" name="${wtTeaserTrackingIdFaf}.f-${facetValueNameWtLower}.-" title="${facetValueName}">${facetValueNameDisplay}&nbsp;<span class="product-count"><spring:theme code="search.nav.facetValueCount" arguments="${facetValue.count}"/></span></a>
													</li>
												</c:when>
												<c:when test="${(facet.type.value eq 'checkbox')}">
													<li class="facet-item -type-checkbox ${facetValue.selected ? 'active' : ''}"
														data-facet-type="${facet.type.value}" 
														data-facet-name="${facetValueName}" 
														data-facet-value-name="${facetValueName}"
														data-facet-number="${facetValueStatus.count}"
														data-filter-string="${facetValue.filterString}"
													>
													
														<c:set var="facetValueNameWt" value="${fn:replace(facetValueName, ' ', '-')}" />
														<c:set var="facetValueNameWtLower" value="${fn:toLowerCase(facetValueNameWt)}" />
														<c:set var="facetValueNameDisplay" value="${fn:replace(facetValueName, '<br/>', ', ')}" />
														<c:if test="${fn:length(facetValueNameDisplay) > 70}">
															<c:set var="facetValueNameDisplay" value="${fn:substring(facetValueNameDisplay, 0, 65)}..." />
														</c:if>
													
														<c:url value="${facetValue.query.url}" var="facetValueQueryUrl"/>
														<a href="${facetValueQueryUrl}" class="${facetValue.selected ? 'active' : ''}" name="${wtTeaserTrackingIdFaf}.f-${facetValueNameWtLower}.-" title="${facetValueName}">${facetValueNameDisplay}&nbsp;<span class="product-count"><spring:theme code="search.nav.facetValueCount" arguments="${facetValue.count}"/></span></a>
													</li>
												</c:when>
											</c:choose>
											<c:if test="${(facetValueStatus.count - valuesCount + remainingValues) % valuesPerColumn < 1 }" >
												<c:set var="remainingValues" value="${valuesCount-facetValueStatus.count}" />
												<c:set var="valuesPerColumn" value="${remainingValues / (FACET_VALUE_COLUMNS_COUNT - columnNo)}" />
												<c:set var="columnNo" value="${columnNo + 1}" />
												</ul>
												<ul class="facet-value-list column-${columnNo}">
											</c:if>
										</c:forEach>
									</ul>
								</div>
								<div class="button-container border-top">
									<div class="min-max-wrapper">
										<span class="min-max-label">${sMin}</span>
										<select id="select-min-value-${facet.type.value}" class="selectpicker select-min-value selectboxit-meta" name="min-value-${facet.type.value}">
											<option class="option option-default disabled" value="0" disabled="disabled" selected="selected">${sValue}</option>
											<c:forEach items="${facet.values}" var="minFacetValue" varStatus="minValueStatus">
												<option class="facet-min-max-option" value="${minValueStatus.count}">${minFacetValue.name}</option>
											</c:forEach>
										</select>
									
										<span class="min-max-label">${sMax}</span>
										<select id="select-max-value-${facet.type.value}" class="selectpicker select-max-value selectboxit-meta" name="max-value-${facet.type.value}">
											<option class="option option-default disabled" value="0" disabled="disabled" selected="selected">${sValue}</option>
											<c:forEach items="${facet.values}" var="maxFacetValue" varStatus="maxValueStatus">
												<option class="facet-min-max-option" value="${maxValueStatus.count}">${maxFacetValue.name}</option>
											</c:forEach>
										</select>
									</div>
									<div class="filtering-tips">
										<button class="btn-info"><i></i></button>
									</div>
									<div class="button-wrapper">
										<button type="submit" class="btn btn-primary btn-apply-filter disabled" disabled="disabled">${sApply}<i></i></button>
										<button type="button" class="btn btn-secondary btn-reset${facet.hasSelectedElements ? '"' : ' disabled" disabled="disabled"'}>${sReset}</button>
									</div>
								</div>
							</div>
						</c:otherwise>
					</c:choose>
				</li>
			</c:if>
		</c:forEach>
	</ul>
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

<div class="ajax-action-overlay hidden">
	<div class="background-overlay"></div>
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