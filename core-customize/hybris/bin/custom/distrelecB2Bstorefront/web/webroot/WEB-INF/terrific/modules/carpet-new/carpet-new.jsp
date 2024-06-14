<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>

<!--

Variant 1: Query Products

!-->
<c:set var="itemCount" value="${fn:length(carpetData)}" />

<%-- A row is considered to be 4 elements --%>
<c:set var="itemsPerRow" value="4" />

<c:if test="${itemCount >= itemsPerRow}">
	<h2 class="base">${title}</h2>
	<div class="carpet-items">
		<c:set var="activeCounter" value="" />
		<%-- All rows need to have 4 items --%>
		<c:forEach items="${carpetData}" var="item" varStatus="status" end="${itemCount-1 - itemCount % 4}">

			<mod:carpet-new-item productItemData="${item}" template="product" skin="query-carpet" htmlClasses="padding-top ${(status.index + 1) % 4 == 0 ? 'last' : '' }" position="${status.index + 1}" attributes="data-product-id='${item.code}'"/>

		</c:forEach>
	</div>
</c:if>


<%--

Variant 2: Teaser Columns

--%>

<c:set var="itemCountCol1" value="${fn:length(column1Items)}" />
<c:set var="itemCountCol2" value="${fn:length(column2Items)}" />
<c:set var="itemCountCol3" value="${fn:length(column3Items)}" />
<c:set var="itemCountCol4" value="${fn:length(column4Items)}" />

<c:set var="position" value="1" />

<c:if test="${itemCountCol1 >= 1 && itemCountCol2 >= 1 && itemCountCol3 >= 1 && itemCountCol4 >= 1}">

	<h2 class="base">${title}</h2>
	<div class="carpet-items">

		<div class="items-colum-1">
			<c:forEach items="${column1Items}" var="item" varStatus="status">
				<c:choose>

					<%--Teaser --%>
					<c:when test="${item.isTeaser}">
						<mod:carpet-new-item template="teaser" teaserItemData="${item.contentTeaser}" youTubeID="${item.youTubeID}" additionalText="${item.showOriginalPrice}" htmlClasses="${item.contentTeaser.fullsize ? ' fullsize' : '' }" position="${position}"/>
					</c:when>
					
					<%--Product --%>
					<c:otherwise>
						<mod:carpet-new-item productItemData="${item.product}" youTubeID="${item.youTubeID}" 
							additionalText="${item.showOriginalPrice}" template="product" originalPrice="${item.originalPrice}" discount="${item.price.saving}"
							showOriginalPrice = "${item.showOriginalPrice}" trackingcode="c1r${status.index + 1}"
							htmlClasses="padding-top" position="${position}" attributes="data-product-id='${productData.code}'"/>
					</c:otherwise>

				</c:choose>

				<c:set var="position" value="${position + 1}" />
			</c:forEach>
		</div>

		<div class="items-colum-2">
			<c:forEach items="${column2Items}" var="item" varStatus="status">

				<c:choose>

					<%--Teaser --%>
					<c:when test="${item.isTeaser}">
						<mod:carpet-new-item template="teaser" youTubeID="${item.youTubeID}" teaserItemData="${item.contentTeaser}" additionalText="${item.showOriginalPrice}" htmlClasses="${item.contentTeaser.fullsize ? ' fullsize' : '' }" position="${position}"/>
					</c:when>

					<%--Product --%>
					<c:otherwise>
						<mod:carpet-new-item productItemData="${item.product}" youTubeID="${item.youTubeID}" 
							additionalText="${item.showOriginalPrice}" template="product" originalPrice="${item.originalPrice}" discount="${item.price.saving}"
							showOriginalPrice = "${item.showOriginalPrice}" trackingcode="c2r${status.index + 1}"
							htmlClasses="padding-top" position="${position}" attributes="data-product-id='${productData.code}'"/>
					</c:otherwise>

				</c:choose>

				<c:set var="position" value="${position + 1}" />
			</c:forEach>
		</div>

		<div class="items-colum-3">
			<c:forEach items="${column3Items}" var="item" varStatus="status">
			
				<c:choose>
					<%--Teaser --%>
					<c:when test="${item.isTeaser}">
						<mod:carpet-new-item template="teaser" youTubeID="${item.youTubeID}" teaserItemData="${item.contentTeaser}" additionalText="${item.showOriginalPrice}" htmlClasses="${item.contentTeaser.fullsize ? ' fullsize' : '' }" position="${position}"/>
					</c:when>

					<%--Product --%>
					<c:otherwise>
						<mod:carpet-new-item productItemData="${item.product}" youTubeID="${item.youTubeID}" 
							additionalText="${item.showOriginalPrice}" template="product" originalPrice="${item.originalPrice}" discount="${item.price.saving}"
							showOriginalPrice = "${item.showOriginalPrice}" trackingcode="c3r${status.index + 1}"
							htmlClasses="padding-top" position="${position}" attributes="data-product-id='${productData.code}'"/>
					</c:otherwise>

				</c:choose>

				<c:set var="position" value="${position + 1}" />
			</c:forEach>
		</div>


<script type="text/javascript">

	var teaserProductsDataLayer = ${teaserProductsDataLayer}
	
</script>


		<div class="items-colum-4">
			<c:forEach items="${column4Items}" var="item" varStatus="status">

				<c:choose>
					<%--Teaser --%>
					<c:when test="${item.isTeaser}">
						<mod:carpet-new-item template="teaser" youTubeID="${item.youTubeID}" teaserItemData="${item.contentTeaser}" additionalText="${item.showOriginalPrice}" htmlClasses="${item.contentTeaser.fullsize ? ' fullsize' : '' }" position="${position}"/>
					</c:when>

					<%--Product --%>
					<c:otherwise>
						<mod:carpet-new-item productItemData="${item.product}" youTubeID="${item.youTubeID}" 
							additionalText="${item.showOriginalPrice}" template="product" originalPrice="${item.originalPrice}" discount="${item.price.saving}"
							showOriginalPrice = "${item.showOriginalPrice}" trackingcode="c4r${status.index + 1}"
							htmlClasses="padding-top" position="${position}" attributes="data-product-id='${productData.code}'"/>
					</c:otherwise>

				</c:choose>

				<c:set var="position" value="${position + 1}" />
			</c:forEach>
		</div>

	</div>
</c:if>


