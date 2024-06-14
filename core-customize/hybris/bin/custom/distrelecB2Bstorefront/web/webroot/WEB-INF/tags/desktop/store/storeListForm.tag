<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="storeSearchPageData" required="false" type="de.hybris.platform.acceleratorservices.storefinder.data.StoreFinderSearchPageData" %>
<%@ attribute name="locationQuery" required="false" type="java.lang.String" %>
<%@ attribute name="seeMoreLimit" required="false" type="java.lang.String" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="store" tagdir="/WEB-INF/tags/desktop/store" %>

<c:if test="${storeSearchPageData ne null and !empty storeSearchPageData.results}">
	<table id="store_locator">
		<thead>
			<tr>
				<th id="header1"><spring:theme code="storeFinder.table.store" /></th>
				<th id="header2"><spring:theme code="storeFinder.table.distance" /></th>
				<th id="header3"><spring:theme code="storeFinder.table.address" /></th>
				<th id="header4"><spring:theme code="storeFinder.table.opening" /></th>
			</tr>
		</thead>
		<tbody>

			<c:forEach items="${storeSearchPageData.results}" var="pos">
				<c:url value="/store-finder/${locationQuery}/${pos.name}" var="posUrl"/>
				<tr>
					<td headers="header1">
						<a href="${posUrl}" class="left">
							<ycommerce:testId code="storeFinder_result_image">
								<store:storeImage store="${pos}" format="cartIcon" />
							</ycommerce:testId>
						</a>
						<a href="${posUrl}" class="right"><spring:theme code="storeFinder.table.view.map" /></a>
						<span class="store_details">
							<h2>
								<ycommerce:testId code="storeFinder_result_link">
									<a href="${posUrl}">${pos.name}</a>
								</ycommerce:testId>
							</h2>
							<p>${pos.address.phone}</p>
						</span>
					</td>
					
					<td headers="header2">
						<p>${pos.formattedDistance}</p>
					</td>
					<td headers="header3">
						<ul>
							<ycommerce:testId code="storeFinder_result_address_label">
								<c:if test="${not empty pos.address}">
									<li>${pos.address.line1}</li>
									<li>${pos.address.line2}</li>
									<li>${pos.address.town}</li>
									<li>${pos.address.postalCode}</li>
								</c:if>
							</ycommerce:testId>
						</ul>
					</td>
					<td headers="header4">
						<store:openingSchedule openingSchedule="${pos.openingHours}" />
					</td>
				</tr>
			</c:forEach>
			<tr>
				<td colspan="4">
					<c:if test="${showSeeMore}">
						<c:url value="/store-finder?q=${locationQuery}&amp;more=${seeMoreLimit}" var="showMoreLink"/>
						<a href="${showMoreLink}" class="right"><spring:theme code="storeFinder.see.more" /></a>
					</c:if>
				</td>
			</tr>
		</tbody>
	</table>
</c:if>
