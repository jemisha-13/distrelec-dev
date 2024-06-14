<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:if test="${!product.catPlusItem && not empty product.volumePricesMap}">

	<c:if test="${not empty product.promotionEndDate}">
		<spring:message code="text.store.dateformat" var="datePattern" />
		<c:set var="promotionEndDate">
			<fmt:formatDate value="${product.promotionEndDate}" dateStyle="short" timeStyle="short" type="date" pattern="${datePattern}" />
		</c:set>
	</c:if>

	<c:set var="yourPriceDiffers" value="${false}" />
	<c:set var="hasCustomPrices" value="${false}" />
	<c:set var="hasExpander" value="${false}" />
	<c:set var="stopAt" value="6" />
	<c:forEach items="${product.volumePricesMap}" var="volumePrice" begin="0" end="3" varStatus="varStatus">
		<c:if test="${not empty volumePrice.value.custom}">
			<c:set var="hasCustomPrices" value="${true}" />
			<c:set var="yourPriceDiffers" value="${yourPriceDiffers or volumePrice.value.list.value gt volumePrice.value.custom.value}" />
		</c:if>
		<%-- If second price row is zero then it's a PriceBurner item => only show first price line --%>
		<c:if test="${varStatus.count == 2 && volumePrice.value.list.value == '0.0'}">
			<c:set var="stopAt" value="0" />
		</c:if>

		<c:if test="${varStatus.count == 4 && stopAt != 0}">
			<c:set var="isBig" value="${true}" />
		</c:if>
	</c:forEach>
	<c:choose>
		<c:when test="${not empty product.listPrice and product.listPrice.value gt product.price.value}">
			<%-- Case List Price with smaller your Price than list price --%>
			<div class="price-list has-small-price">
				<h2><format:price format="default" priceData="${product.price}" /></h2>
				<span class="price-list-small"><spring:message code="buyingSection.list.price"/>: <format:price format="default" priceData="${product.listPrice}" /></span>
			</div>
		</c:when>
		<c:when test="${not empty product.price}">
			<div class="price"><h2 class="odometer-price"><format:price format="default" priceData="${product.price}" /></h2> </div>
		</c:when>
	</c:choose>
	<c:choose>
		<c:when test="${yourPriceDiffers}">
			<%-- Case has your price, your price is smaller than list price --%>
			<div class="row head">
				<div class="td nth1 ellipsis"></div>
				<div class="td nth2 ellipsis" title="<spring:message code="product.scaledPrices.your.price" />">
					<spring:message code="product.scaledPrices.your.price" />
				</div>
				<div class="td nth2 ellipsis" title="<spring:message code="product.scaledPrices.list.price" />">
					<spring:message code="product.scaledPrices.list.price" />
				</div>
				<div class="td nth3" title="<spring:message code="product.scaledPrices.yoursavings" />">
					<spring:message code="product.scaledPrices.yoursavings" />
					<c:if test="${not empty promotionEndDate}">
						*
					</c:if>
				</div>
			</div>
		</c:when>

		<c:when test="${hasCustomPrices}">
			<%-- Case has your price, but your price equals list price --%>
			<div class="row head hasCustomPrices">
				<div class="td nth1"></div>
				<div class="td nth2 ellipsis" title="<spring:message code="product.scaledPrices.your.price" />">
					<spring:message code="product.scaledPrices.your.price" />
					<c:if test="${not empty promotionEndDate}">
						*
					</c:if>
				</div>
			</div>
		</c:when>

		<c:otherwise>
			<div class="row head hasntCustomPrices">
				<div class="td nth1"></div>
				<div class="td nth2 ellipsis" title="<spring:message code="product.scaledPrices.list.price" />"><spring:message code="product.scaledPrices.list.price" /></div>
				<c:if test="${product.withSavings}">
					<div class="td nth3 ellipsis" title="<spring:message code="product.scaledPrices.yoursavings" />">
						<spring:message code="product.scaledPrices.yoursavings" />
						<c:if test="${not empty promotionEndDate}">
							*
						</c:if>
					</div>
				</c:if>
			</div>
		</c:otherwise>
	</c:choose>

	<div class="row body hidden">
		<div class="td nth1">
			0&nbsp;+
		</div>
		<div class="td nth2">
			<format:price format="default" priceData="${product.price}" />
		</div>
	</div>

	<c:forEach items="${product.volumePricesMap}" var="entry" end="6" varStatus="varStatus">
		<div class="row body">
			<div class="td nth1">${entry.key}&nbsp;+</div>
			<c:choose>
				<c:when test="${yourPriceDiffers}">
					<div class="td nth2">
						<format:price format="default" priceData="${entry.value.custom}" />
					</div>
					<div class="td nth3 list-price">
						<format:price format="default" priceData="${entry.value.list}" />
					</div>
					<c:choose>
						<c:when test="${product.withSavings and entry.value.custom.saving > 0}">
							<div class="td nth3 savingsCell">
								&nbsp; ${entry.value.custom.saving} %
							</div>
						</c:when>
						<c:otherwise>
							<div class="td nth3 savingsCell"> </div>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:when test="${hasCustomPrices}">
					<div class="td nth2">
						<format:price format="default" priceData="${entry.value.custom}" />
					</div>
					<div class="td nth3 6"></div>
				</c:when>
				<c:otherwise>
					<div class="td nth2">
						<format:price format="default" priceData="${entry.value.list}" />
					</div>

					<c:choose>
						<c:when test="${product.withSavings and entry.value.list.saving > 0}">
							<div class="td nth3 7">
								${entry.value.list.saving} %
							</div>
						</c:when>
						<c:otherwise>
							<div class="td nth3 8"></div>
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
		</div>
	</c:forEach>

	<div class="footer row">
		<c:if test="${allowBulk}">
			<a href="#" class="btn btn-bulk-discount"><i></i><span><spring:message code="product.scaledPrices.bulk.discount" /></span></a>
		</c:if>
	</div>


	<c:if test="${not empty promotionEndDate}">
		<div class="promotion-valid-until">
			* <spring:message code="product.pricelist.promotion.valid.until" arguments="${promotionEndDate}" argumentSeparator="@" text="* Promotion valid until ${promotionEndDate}" />
		</div>
	</c:if>
</c:if>
