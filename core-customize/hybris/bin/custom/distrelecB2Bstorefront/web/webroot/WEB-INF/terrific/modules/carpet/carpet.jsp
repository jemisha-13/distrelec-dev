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

<%-- A singleExtendedRow is considered to be 5 elements --%>
<c:set var="itemsPerSingleExtendedRow" value="5" />

<c:choose>
	<c:when test="${itemCount >= itemsPerSingleExtendedRow}">

		<%-- A unit is considered to be 4 rows, 2 of them containing 5 elements, two of them containing 6 elements --%>
		<c:set var="itemsPerUnit" value="22" />
		<%-- A doubleRow is considered to be 2 rows, 1 of them containing 5 elements, one of them containing 6 elements --%>
		<c:set var="itemsPerDoubleRow" value="11" />
		<%-- A singleFullRow is considered to be 6 elements --%>
		<c:set var="itemsPerSingleFullRow" value="6" />

		<%-- Normal items after a right extended item, given by Design Templates --%>
		<c:set var="nextLeftExtendedItem" value="8" />
		<%-- Normal items after a left extended item, given by Design Templates --%>
		<c:set var="nextRightExtendedItem" value="12" />
		<%-- First Right Extended Item --%>
		<c:set var="firstRightExtendedItem" value="3" />

		<c:set var="maxDisplayedUnits" value="${fn:substringBefore(itemCount div itemsPerUnit, '.')}"/>
		<%-- Can maximally be 21 --%>
		<c:set var="maxUnitsRemainder" value="${itemCount % itemsPerUnit}" />

		<c:set var="maxDisplayedDoubleRows" value="${fn:substringBefore(maxUnitsRemainder div itemsPerDoubleRow, '.')}"/>
		<%-- Can maximally be 10 --%>
		<c:set var="maxDoubleRowsRemainder" value="${maxUnitsRemainder % itemsPerDoubleRow}" />

		<c:set var="maxDisplayedItems" value="${(maxDisplayedUnits * itemsPerUnit) + (maxDisplayedDoubleRows * itemsPerDoubleRow)}" />

		<%-- Add additional items if Remainder is > itemsPerSingleExtendedRow --%>
		<c:choose>
			<c:when test="${maxUnitsRemainder < itemsPerDoubleRow and maxUnitsRemainder >= itemsPerSingleExtendedRow}">
				<c:set var="maxDisplayedItems" value="${maxDisplayedItems + itemsPerSingleExtendedRow}" />
			</c:when>
			<c:when test="${maxDoubleRowsRemainder >= itemsPerSingleExtendedRow}">
				<c:set var="maxDisplayedItems" value="${maxDisplayedItems + itemsPerSingleExtendedRow}" />
			</c:when>
		</c:choose>

		<h2 class="base padding-left">${title}</h2>
		<div class="carpet-items">
			<c:set var="activeCounter" value="" />
			<div class="item-template-1">
				<c:forEach items="${carpetData}" var="item" varStatus="status" end="${maxDisplayedItems - 1}">
						<c:choose>
							<c:when test="${leftItemCounter == nextLeftExtendedItem}">
								<%-- Call Carpet Item Template Left --%>
								<mod:carpet-item productItemData="${item}" template="product-large" skin="large" position="${status.index + 1}" attributes="data-product-id='${item.code}'"/>
								<c:set var="leftItemCounter" value="0" />
								<c:set var="activeCounter" value="right" />
							</c:when>
							<c:when test="${rightItemCounter == nextRightExtendedItem or status.count == firstRightExtendedItem}">
								<%-- Call Carpet Item Template Right --%>
								<mod:carpet-item productItemData="${item}" template="product-large" skin="large skin-carpet-item-product-right" position="${status.index + 1}" attributes="data-product-id='${item.code}'" />
								<c:set var="rightItemCounter" value="0" />
								<c:set var="activeCounter" value="left" />
							</c:when>
							<c:otherwise>
								<%-- Call Carpet Item Standard --%>
								<mod:carpet-item productItemData="${item}" template="product" position="${status.index + 1}" attributes="data-product-id='${item.code}'"/>
								<c:choose>
									<c:when test="${activeCounter == 'left'}">
										<c:set var="leftItemCounter" value="${leftItemCounter + 1}" />
									</c:when>
									<c:when test="${activeCounter == 'right'}">
										<c:set var="rightItemCounter" value="${rightItemCounter + 1}" />
									</c:when>
								</c:choose>
							</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when test="${status.count % itemsPerUnit == 0}">
								<%-- Unit completed --%>
								<div class="item-template-1"></div>
							</c:when>
							<c:when test="${status.count % itemsPerDoubleRow == 0}">
								<%-- DoubleRow completed --%>
								<div class="item-template-3"></div>
							</c:when>
							<c:when test="${status.count % itemsPerDoubleRow == itemsPerSingleExtendedRow}">
								<%-- Single Row completed --%>
								<div class="item-template-2"></div>
							</c:when>
						</c:choose>
				</c:forEach>
			</div>
		</div>
	</c:when>
</c:choose>

<!--

	Variant 2: Teaser Columns

!-->

<c:set var="itemCountCol1" value="${fn:length(column1Items)}" />
<c:set var="itemCountCol2" value="${fn:length(column2Items)}" />
<c:set var="itemCountCol3" value="${fn:length(column3Items)}" />

<c:set var="position" value="1" />

<c:choose>
	<c:when test="${itemCountCol1 >= 2 && itemCountCol2 >= 2 && itemCountCol3 >= 2}">
<%----%>
		<h2 class="base padding-left">${title}</h2>
		<div class="carpet-items">
<%----%>
			<div class="items-colum-1">
				<c:forEach items="${column1Items}" var="item" varStatus="status">

					<c:choose>

						<%--Teaser --%>
						<c:when test="${item.isTeaser}">
							<c:choose>
								<c:when test="${item.size == 'SMALL'}">
									<mod:carpet-item template="teaser" teaserItemData="${item.contentTeaser}" htmlClasses="${item.contentTeaser.fullsize ? ' fullsize' : '' }" position="${position}"/>
								</c:when>
								<c:otherwise>
									<mod:carpet-item template="teaser-large" skin="large" teaserItemData="${item.contentTeaser}" htmlClasses="${item.contentTeaser.fullsize ? ' fullsize' : '' }" position="${position}"/>
								</c:otherwise>
							</c:choose>
						</c:when>

						<%--Product --%>
						<c:otherwise>
								<c:choose>
									<c:when test="${item.size == 'SMALL'}">
										<mod:carpet-item productItemData="${item.product}" template="product" position="${position}" attributes="data-product-id='${productData.code}'"/>
									</c:when>
									<c:otherwise>
										<mod:carpet-item productItemData="${item.product}" template="product-large" skin="large" position="${position}" attributes="data-product-id='${productData.code}'" />
									</c:otherwise>
								</c:choose>
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
							<c:choose>
								<c:when test="${item.size == 'SMALL'}">
									<mod:carpet-item template="teaser" teaserItemData="${item.contentTeaser}" htmlClasses="${item.contentTeaser.fullsize ? ' fullsize' : '' }" position="${position}"/>
								</c:when>
								<c:otherwise>
									<mod:carpet-item template="teaser-large" skin="large" teaserItemData="${item.contentTeaser}" htmlClasses="${item.contentTeaser.fullsize ? ' fullsize' : '' }" position="${position}"/>
								</c:otherwise>
							</c:choose>
						</c:when>

						<%--Product --%>
						<c:otherwise>
							<c:choose>
								<c:when test="${item.size == 'SMALL'}">
									<mod:carpet-item productItemData="${item.product}" template="product" position="${position}" attributes="data-product-id='${item.product.code}'"/>
								</c:when>
								<c:otherwise>
									<mod:carpet-item productItemData="${item.product}" template="product-large" skin="large" position="${position}" attributes="data-product-id='${item.product.code}'" />
								</c:otherwise>
							</c:choose>
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
							<c:choose>
								<c:when test="${item.size == 'SMALL'}">
									<mod:carpet-item template="teaser" teaserItemData="${item.contentTeaser}" htmlClasses="${item.contentTeaser.fullsize ? ' fullsize' : '' }" position="${position}"/>
								</c:when>
								<c:otherwise>
									<mod:carpet-item template="teaser-large" skin="large" teaserItemData="${item.contentTeaser}" htmlClasses="${item.contentTeaser.fullsize ? ' fullsize' : '' }" position="${position}"/>
								</c:otherwise>
							</c:choose>
						</c:when>

						<%--Product --%>
						<c:otherwise>
								<c:choose>
									<c:when test="${item.size == 'SMALL'}">
										<mod:carpet-item productItemData="${item.product}" template="product" position="${position}" attributes="data-product-id='${item.product.code}'"/>
									</c:when>
									<c:otherwise>
										<mod:carpet-item productItemData="${item.product}" template="product-large" skin="large" position="${position}" attributes="data-product-id='${item.product.code}'" />
									</c:otherwise>
								</c:choose>
						</c:otherwise>

					</c:choose>

					<c:set var="position" value="${position + 1}" />
				</c:forEach>
			</div>

		</div>
	</c:when>
</c:choose>