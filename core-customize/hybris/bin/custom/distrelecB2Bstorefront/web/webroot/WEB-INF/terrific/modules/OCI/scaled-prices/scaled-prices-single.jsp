<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>
<%--<%@ attribute name="priceData" type="de.hybris.platform.commercefacades.product.data.PriceData" %>--%>
<c:set var="priceData" value="${product.price}" />

<c:set var="priceVal" value="${empty displayValue ? priceData.value : displayValue}" />

<c:set var="salesUnitLowerCase" value="${product.salesUnit}" />
<c:if test="${salesUnitLowerCase eq '1 pieces'}">
	<c:set var="salesUnitLowerCase" value="1 piece" />
</c:if>

<c:set var="isB2E" value="false" />
<sec:authorize access="hasRole('ROLE_B2BEESHOPGROUP')">
	<c:set var="isB2E" value="true" />
</sec:authorize>

<c:set var="bulkDiscountHidden" value="" />
<c:set var="bulkDiscountHidden" value="hidden" />
<c:if test="${isB2E eq true}">
	<c:set var="bulkDiscountHidden" value="hidden" />
</c:if>

<spring:message code="product.scaledPrices.text" var="sPriceEach" />
<spring:message code="product.scaledPrices.pricePer" var="sPricePer" />
<spring:message code="product.scaledPrices.saveText" var="sSaveText" />
<spring:message code="product.scaledPrices.excVat" var="sExcVat" />
<spring:message code="product.scaledPrices.incVat" var="sIncVat" />

<div class="skin-scaled-prices-single__holder">
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
                <span class="price-each">
					<spring:message code="buyingSection.per.salesUnit.excl.vat.short" argumentSeparator=";"  arguments="${salesUnitLowerCase}" />
				</span>
				<div class="price"><h2 class="odometer-price"><format:price format="default" priceData="${product.price}" /></h2> </div>
				<c:choose>
					<c:when test="${currentChannel.net}">
					<span class="vat-text">
						(${sExcVat})
					</span>
					</c:when>
					<c:otherwise>
					<span class="vat-text">
						(${sIncVat})
					</span>
					</c:otherwise>
				</c:choose>

				<c:if test="${not empty priceData.pricePerX and not empty priceData.pricePerXUOMQty and not empty priceData.pricePerXUOM }">
					<span class="price-per price-per--quantity">
						<span>${priceData.currencyIso}</span>
						<span><fmt:formatNumber value="${priceData.pricePerX}" pattern="0.00"/> /</span>
						<span>${priceData.pricePerXUOMQty}</span>
						<span>${priceData.pricePerXUOM}</span>
					</span>
				</c:if>

			</c:when>
		</c:choose>

		<div class="body hidden">
			<div class="td nth1">
				0&nbsp;+
			</div>
			<div class="td nth2">
				<format:price format="default" priceData="${product.price}" />
			</div>
		</div>

		<div class="body-holder">
			<c:forEach items="${product.volumePricesMap}" var="entry" end="6" varStatus="varStatus">

				<c:if test="${product.withSavings and entry.value.custom.saving > 0}">
					<c:set var="withSaving" value="with-saving" />
				</c:if>

				<div class="body body--${withSaving}">
					<div class="td nth1">${entry.key}&nbsp;+</div>
					<c:choose>
						<c:when test="${yourPriceDiffers}">
							<div class="td nth3 list-price">
								<format:price format="default" priceData="${entry.value.list}" />
							</div>
							<div class="td nth2">
								<format:price format="default" priceData="${entry.value.custom}" />
							</div>
							<c:choose>
								<c:when test="${product.withSavings and entry.value.custom.saving > 0}">
									<c:set var="withSaving" value="with-saving" />
									<div class="td nth3 savingsCell">
										<c:choose>
											<c:when test="${currentCountry.isocode eq 'HU'}">
												(${entry.value.custom.saving}%&nbsp;${sSaveText})
											</c:when>
											<c:otherwise>
												(${sSaveText}&nbsp;${entry.value.custom.saving}%)
											</c:otherwise>
										</c:choose>
									</div>
									<div class="nth2-price-per hidden">

										<c:set var="priceData3" value="${entry.value.custom}" />

										<c:if test="${not empty priceData3.pricePerX and not empty priceData3.pricePerXUOMQty and not empty priceData3.pricePerXUOM }">
											<span class="price-per">
												<span>${priceData3.currencyIso}</span>
												<span><fmt:formatNumber value="${priceData3.pricePerX}" pattern="0.00"/> /</span>
												<span>${priceData3.pricePerXUOMQty}</span>
												<span>${priceData3.pricePerXUOM}</span>
											</span>
										</c:if>

									</div>

								</c:when>
								<c:otherwise>
									<div class="td nth3 savingsCell"> </div>
									<div class="nth2-price-per hidden">

										<c:set var="priceData3" value="${entry.value.custom}" />

										<c:if test="${not empty priceData3.pricePerX and not empty priceData3.pricePerXUOMQty and not empty priceData3.pricePerXUOM }">
											<span class="price-per">
												<span>${priceData3.currencyIso}</span>
												<span><fmt:formatNumber value="${priceData3.pricePerX}" pattern="0.00"/> /</span>
												<span>${priceData3.pricePerXUOMQty}</span>
												<span>${priceData3.pricePerXUOM}</span>
											</span>
										</c:if>

									</div>

								</c:otherwise>
							</c:choose>
						</c:when>
						<c:when test="${hasCustomPrices}">
							<div class="td nth2">
								<format:price format="default" priceData="${entry.value.custom}" />
							</div>
							<div class="td nth3 6"></div>
							<div class="nth2-price-per hidden">

								<c:set var="priceData2" value="${entry.value.custom}" />

								<c:if test="${not empty priceData2.pricePerX and not empty priceData2.pricePerXUOMQty and not empty priceData2.pricePerXUOM }">
									<span class="price-per">
										<span>${priceData2.currencyIso}</span>
										<span><fmt:formatNumber value="${priceData2.pricePerX}" pattern="0.00"/> /</span>
										<span>${priceData2.pricePerXUOMQty}</span>
										<span>${priceData2.pricePerXUOM}</span>
									</span>
								</c:if>

							</div>
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
		</div>

		<c:if test="${allowBulk and currentBaseStore.quotationsEnabled}">
			<div class="footer ${bulkDiscountHidden}">
				<a href="#" class="btn btn-bulk-discount">
					<i class="fas fa-chart-line" aria-hidden="true"></i>
					<span><spring:message code="product.scaledPrices.bulk.discount" /></span>
				</a>
			</div>
		</c:if>

		<c:if test="${not empty promotionEndDate}">
			<div class="promotion-valid-until">
				<spring:message code="product.pricelist.promotion.valid.until" arguments="${promotionEndDate}" argumentSeparator="@" text="Promotion valid until ${promotionEndDate}" />
			</div>
		</c:if>
	</c:if>
</div>
