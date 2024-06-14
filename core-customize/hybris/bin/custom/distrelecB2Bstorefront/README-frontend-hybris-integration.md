
#
# Frontend Hybris Integration Overview
#


Create Terrific Module & Skins
--------------------------------------------------------
Add Module, run:
tc-module.sh Mysample

Creates Module files and tag in:
web/webroot/WEB-INF/terrific/modules/mysample/mysample.jsp
web/webroot/WEB-INF/terrific/modules/mysample/css/mysample.less
web/webroot/WEB-INF/terrific/modules/mysample/js/mysample.js
web/webroot/WEB-INF/tags/terrific/modules/mysample.tag

Add Skin into Module, run:
tc-module.sh Mysample Myskin // you need to change the name inside the module javascript file, if you use - inside module name


I18N - Translations
--------------------------------------------------------
Markup: Key/Value Pair in:
web/webroot/WEB-INF/messages/base.properties

JSP - Echo:
<spring:theme code="autosuggest.error.QUANTITY_NUMBER_EXPECTED" text="Please enter a valid number." />

JSP - Store in Variable:
(Prefix Variable with "s")
<spring:theme code="base.products" text="Products" var="sProducts" />

JSP - with Arguments
basket.page.totals.grossTax = Your {1} order includes {0} tax.
<spring:theme code="basket.page.totals.grossTax" arguments="${cartData.totalTax.formattedValue}|Hello" argumentSeparator="|" />

JavaScript:
<script id="additional-facet-strings" type="text/x-i18n-stringbundle">
	{
		FACET_SHOW_MORE_LINK:	    '<spring:theme code="search.nav.facetTitle" arguments="{{= it.name }}" text="Shop by {{=it.name}}" />'
		,FACET_VALUE_COUNT:	        '<spring:theme code="search.nav.facetValueCount" arguments="{{= it.count }}" text="{{= it.count }}" />'
	}
</script>

see web/webroot/WEB-INF/terrific/modules/aa_i18n/readme.md


Module Tag
--------------------------------------------------------

### Default Module Attributes

	<mod:header-top
		tag=""
		htmlClasses=""
		skin=""
		dataConnectors=""
		attributes="id='backenddata'"
		template=""
	/>

### Module Tag Example

	<%-- Common module settings --%>
	<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
	<%@ attribute name="tag" description="The tag name used for the module context (default=div)" type="java.lang.String" rtexprvalue="false" %>
	<%@ attribute name="htmlClasses" description="The HTML class values to add to the Terrific module element" type="java.lang.String" rtexprvalue="false" %>
	<%@ attribute name="skin" description="The skin used for the module" type="java.lang.String" rtexprvalue="false" %>
	<%@ attribute name="dataConnectors" description="The data-connectors used for the module" type="java.lang.String" rtexprvalue="false" %>
	<%@ attribute name="attributes" description="Additional attributes as comma-separated list, e.g. id='123',role='banner'" type="java.lang.String" rtexprvalue="false" %>
	<%@ attribute name="template" description="The template used for rendering of the module" type="java.lang.String" rtexprvalue="false" %>

	<%-- Required tag libraries --%>
	<%@ taglib prefix="terrific" uri="http://www.namics.com/spring/terrific/tags" %>
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

	<%-- Specific module settings --%>
	<%@ tag description="Teaser Module - Templates: testimonial, category - Skins: no skins" %>

	<%-- Module template selection --%>
	<terrific:mod name="teaser-content" tag="${tag}" htmlClasses="${htmlClasses}" skin="${skin}" dataConnectors="${dataConnectors}" attributes="${attributes}">
		<c:choose>
			<c:when test="${not empty template && template == 'testimonial'}">
				<%@ include file="/WEB-INF/terrific/modules/teaser/teaser-testimonial.jsp" %>
			</c:when>
			<c:when test="${not empty template && template == 'category'}">
				<%@ include file="/WEB-INF/terrific/modules/teaser/teaser-category.jsp" %>
			</c:when>
			<c:otherwise>
				<%@ include file="/WEB-INF/terrific/modules/teaser/teaser.jsp" %>
			</c:otherwise>
		</c:choose>
	</terrific:mod>


CMS Component
--------------------------------------------------------

Modues Path:
web/webroot/WEB-INF/views/desktop/cms/terrific/

	<%-- Define Array "gridCols" with elements: 8, 7, 10 --%>
	<c:set var="gridCols" value="${fn:split('8,7,10', ',')}" scope="application" />

	<%-- Iterate over slot "FooterBottom" --%>
	<cms:slot var="feature" contentSlot="${slots['FooterBottom']}">
		<%-- avaiable variables here:
			 - contentSlot
			 - elementPos
			 - isFirstElement
			 - isLastElement
			 - numberOfElements

			 properties of "feature"
			 - feature.uid       --> component name
			 - feature.itemtype  --> component type
		--%>

		<div class="cols-${gridCols[elementPos]}">
			<cms:component component="${feature}" />
		</div>
	</cms:slot>


Modules in Components only
--------------------------------------------------------

<mod:mainnav />
