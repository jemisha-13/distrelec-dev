<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="facetData" required="true" type="com.namics.hybris.ffsearch.data.facet.FactFinderFacetData" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>

<c:if test="${not empty facetData.values}">
	<div class="item">
		<div class="category">

			<spring:theme code="text.toggleFacet" var="toggleFacetText"/>
			<a href="#" onclick="$(this).closest('div.item').find('div.facetValues').slideToggle();$(this).toggleClass('toggleArrow');return false;" title="${toggleFacetText}">
				<h4><spring:theme code="search.nav.facetTitle" arguments="${facetData.name}"/></h4>							
			</a>
			<a href="#" onclick="$(this).closest('div.item').find('div.facetValues').slideToggle();$(this).toggleClass('toggleArrow');return false;">							
				<span class="dropdown">				
					<span class="dropdown-img" ></span>						
				</span>
			</a>
		</div>

		<ycommerce:testId code="facetNav_facet${facetData.name}_links">
			<div class="facetValues">
				<div class="allFacetValues" style="">
					<ul class="facet_block ${facetData.multiSelect ? '' : 'indent'}">
						<c:forEach items="${facetData.values}" var="facetValue">
							<li>
								<c:if test="${facetData.multiSelect}">
									<form action="#" method="get">
										<input type="hidden" name="q" value="${facetValue.query.query}"/>
										<input type="hidden" name="text" value="${searchPageData.freeTextSearch}"/>
										<label class="facet_block-label">
											<input type="checkbox" ${facetValue.selected ? 'checked="checked"' : ''} onchange="$(this).closest('form').submit()"/>
											${facetValue.name}
										</label>
										${facetValue.count}
									</form>
								</c:if>
								<c:if test="${not facetData.multiSelect}">
									<c:url value="${facetValue.query.url}" var="facetValueQueryUrl"/>
									<a href="${facetValueQueryUrl}">${facetValue.name}</a>
									${facetValue.count}
								</c:if>
							</li>
						</c:forEach>
					</ul>
				</div>
			</div>
		</ycommerce:testId>
	</div>
</c:if>
