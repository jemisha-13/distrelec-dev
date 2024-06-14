<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<c:if test="${!product.catPlusItem}">

<c:if test="${not empty product.volumePricesMap}">
	<c:set var="yourPriceDiffers" value="${false}" />
	<c:set var="hasCustomPrices" value="${false}" />
	<c:set var="hasExpander" value="${false}" />
	<c:set var="stopAt" value="2" />
	<c:forEach items="${product.volumePricesMap}" var="volumePrice" begin="0" end="3" varStatus="varStatus">
		<c:if test="${not empty volumePrice.value.custom}">
			<c:set var="hasCustomPrices" value="${true}" />
			<c:set var="yourPriceDiffers" value="${volumePrice.value.list.value gt volumePrice.value.custom.value}" />
		</c:if>
		<%-- If second price row is zero then it's a PriceBurner item => only show first price line --%>
		<c:if test="${varStatus.count == 2 && volumePrice.value.list.value == '0.0'}">
			<c:set var="stopAt" value="0" />
		</c:if>

		<c:if test="${varStatus.count == 4 && stopAt != 0}">
			<c:set var="isBig" value="${true}" />
		</c:if>
	</c:forEach>
    <%-- Design accept only 3 rows --%>
    <c:forEach items="${product.volumePricesMap}" var="entry" end="${stopAt}" varStatus="varStatus">
    	<c:choose>
	    	<c:when test="${isBig && varStatus.count == 3}">
	    		<c:set var="hasExpander" value="${true}" />

	    	</c:when>
	    	<c:otherwise>
		        <div class="price-table__item">
		        	<div class="nth1">${entry.key}&nbsp;+</div>
					<c:choose>
						<c:when test="${yourPriceDiffers}">
							<div class="nth2">
								<format:price format="defaultOnlyNumbers" priceData="${entry.value.custom}" />
							</div>
						</c:when>
						<c:when test="${hasCustomPrices}">
							<div class="nth2">
								<format:price format="defaultOnlyNumbers" priceData="${entry.value.custom}" />
							</div>
						</c:when>
						<c:otherwise>
							<div class="nth2">
								<format:price format="defaultOnlyNumbers" priceData="${entry.value.list}" />
							</div>
						</c:otherwise>
					</c:choose>
		        </div>
	        </c:otherwise>
    	</c:choose>
    </c:forEach>
	
	<c:if test="${isBig or (allowBulk and not product.catPlusItem)}">
		<div class="price-table price-table-overlay">
			<c:forEach items="${product.volumePricesMap}" var="entry" varStatus="varStatus">
				<c:if test="${entry.value.list.value != '0.0'}">
					<div class="price-table__item">
						<div class="nth1">${entry.key}&nbsp;+</div>
						<c:choose>
							<c:when test="${yourPriceDiffers}">
								<div class="nth2">
									<format:price format="defaultOnlyNumbers" priceData="${entry.value.custom}" />
								</div>
							</c:when>
							<c:when test="${hasCustomPrices}">
								<div class="nth2">
									<format:price format="defaultOnlyNumbers" priceData="${entry.value.custom}" />
								</div>
							</c:when>
							<c:otherwise>
								<div class="nth2">
									<format:price format="defaultOnlyNumbers" priceData="${entry.value.list}" />
								</div>
							</c:otherwise>
						</c:choose>
					</div>
				</c:if>
			</c:forEach>
		</div>
		<c:if test="${stopAt != 0 and allowBulk and not product.catPlusItem}">
			<c:set var="stopAt" value="1" />
		</c:if>
	</c:if>

    <c:if test="${not hasExpander and allowBulk and not product.catPlusItem}">
   		<div class="row expander">
   			<a href="#" class="lnk-more-prices"><spring:message code="cart.directorder.add.more" text="More" /><i></i></a>
   		</div>
    </c:if>
</c:if>

</c:if>