<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>
<%@ attribute name="galleryImages" required="true" type="java.util.List" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="/cms2lib/cmstags/cmstags.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="ycommerce" uri="/WEB-INF/tld/ycommercetags.tld" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>

<spring:theme code="text.addToCart" var="addToCartText"/>

<c:set var="counter" value="0" />
<c:forEach items="${promotionLabels}" var="promoLabel">
	<c:if test="${counter le 1}">
		<h3>${promoLabel.label}</h3><br />
		<c:set var="counter" value="${counter + 1 }" />
	</c:if>
</c:forEach>

<c:set var="showAddToCart" value="${true}" />
<div class="span-4">
	<div class="scroller">
		<ul id="carousel_alternate" class="jcarousel-skin alt">
			<c:forEach items="${galleryImages}" var="container" varStatus="varStatus">
				<li>
					<span class="thumb">
						<a href="#">
							<img src="${container.thumbnail.url}" primaryimagesrc="${container.product.url}" galleryposition="${varStatus.index}" alt="${product.name}" title="${product.name}" />
						</a>
					</span>
				</li>
			</c:forEach>
		</ul>
	</div>
</div>

<div class="span-8">
	<div class="prod_image_main" id="primary_image">
		<c:if test="${fn:contains(product.url, '?sku=')}">
			<c:url value="${fn:substringBefore(product.url, '?sku=')}/zoomImages" var="productZoomImagesUrl"/>
		</c:if>
		<c:if test="${not fn:contains(product.url, '?sku=')}">
			<c:url value="${product.url}/zoomImages" var="productZoomImagesUrl"/>
		</c:if>
		<a class="modal" id="imageLink" href="${productZoomImagesUrl}">
			<product:productPrimaryImage product="${product}" format="product"/>
		</a>
		<ycommerce:testId code="productDetails_zoomImage_button">
			<a class="modal" id="zoomLink" href="${productZoomImagesUrl}" target="_blank">
				<span class="details" style="display: block; position: absolute; bottom: 10px; right: 10px; width: 18px; height: 18px;" title="<spring:theme code="general.zoom"/>"></span>
			</a>
		</ycommerce:testId>
	</div>
</div>

<div class="span-8 last">
	<div class="prod">
		<ycommerce:testId code="productDetails_productNamePrice_label_${product.code}">
			<h1>
				${product.name}
			</h1>

				<product:productPricePanel product="${product}"/>

		</ycommerce:testId>
		<p>
			${product.summary}
		</p>
		<div class="bundle">
			<ycommerce:testId code="productDetails_promotion_label">
				<c:if test="${not empty product.potentialPromotions}">
					<c:choose>
						<c:when test="${not empty product.potentialPromotions[0].couldFireMessages}">
							<p>${product.potentialPromotions[0].couldFireMessages[0]}</p>
						</c:when>
						<c:otherwise>
							<p>${product.potentialPromotions[0].description}</p>
						</c:otherwise>
					</c:choose>
				</c:if>
			</ycommerce:testId>
		</div>
		<div class="prod_review">
			<c:if test="${not empty product.reviews}">
				<span class="stars large" style="display: inherit;">
					<span style="width: <fmt:formatNumber maxFractionDigits="0" value="${product.averageRating * 24}" />px;"></span>
				</span>
				<p><fmt:formatNumber maxFractionDigits="1" value="${product.averageRating}" />/5</p>
			</c:if>
			<p class="prod_review-info">
				<c:if test="${not empty product.reviews}">
					<c:choose>
						<c:when test="${fn:length(product.reviews) > 1}">
							<a href="#" id="based_on_reviews"><spring:theme code="review.based.on" arguments="${fn:length(product.reviews)}"/></a>
						</c:when>
						<c:otherwise>
							<a href="#" id="based_on_reviews"><spring:theme code="review.based.on.one" arguments="${fn:length(product.reviews)}"/></a>
						</c:otherwise>
					</c:choose>
				</c:if>
			</p>
			<p class="prod_review-new">
				<a href="#" id="write_review_action_main"><spring:theme code="review.write.title" /></a>
			</p>
		</div>

		<%-- Determine if product is one of apparel style or size variant --%>
		<c:if test="${product.variantType eq 'ApparelStyleVariantProduct'}">
			<c:set var="variantStyles" value="${product.variantOptions}" />
		</c:if>
		<c:if test="${(not empty product.baseOptions[0].options) and (product.baseOptions[0].variantType eq 'ApparelStyleVariantProduct')}">
			<c:set var="variantStyles" value="${product.baseOptions[0].options}" />
			<c:set var="variantSizes" value="${product.variantOptions}" />
			<c:set var="currentStyleUrl" value="${product.url}" />
		</c:if>
		<c:if test="${(not empty product.baseOptions[1].options) and (product.baseOptions[0].variantType eq 'ApparelSizeVariantProduct')}">
			<c:set var="variantStyles" value="${product.baseOptions[1].options}" />
			<c:set var="variantSizes" value="${product.baseOptions[0].options}" />
			<c:set var="currentStyleUrl" value="${product.baseOptions[1].selected.url}" />
		</c:if>
		
		<%-- Determine if product is other variant --%>
		<c:if test="${empty variantStyles}">
			<c:if test="${not empty product.variantOptions}">
				<c:set var="variantOptions" value="${product.variantOptions}" />
			</c:if>
			<c:if test="${not empty product.baseOptions[0].options}">
				<c:set  var="variantOptions" value="${product.baseOptions[0].options}" />
			</c:if>
		</c:if>

		<c:if test="${not empty variantStyles}">
			<c:set var="showAddToCart" value="${false}" />
			<div class="variant_options">
				<div class="colour">
					<p><spring:theme code="product.variants.colour"/></p>
					<ul>
						<c:forEach items="${variantStyles}" var="variantStyle">
							<c:forEach items="${variantStyle.variantOptionQualifiers}" var="variantOptionQualifier">
								<c:if test="${variantOptionQualifier.qualifier eq 'style'}">
									<c:set var="styleValue" value="${variantOptionQualifier.value}" />
									<c:set var="imageData" value="${variantOptionQualifier.image}" />
								</c:if>
							</c:forEach>
							<li <c:if test="${variantStyle.url eq currentStyleUrl}">class="selected"</c:if>>
								<c:url value="${variantStyle.url}" var="productStyleUrl"/>
								<a href="${productStyleUrl}" class="colorVariant" name="${variantStyle.url}">
									<c:if test="${not empty imageData}">
										<img src="${imageData.url}" title="${styleValue}" alt="${styleValue}"/>
									</c:if>
									<c:if test="${empty imageData}">
										<span class="swatch_colour_a" title="${styleValue}"></span>
									</c:if>
								</a>
							</li>
						</c:forEach>
					</ul>
				</div>
				<div class="size">
					<form>
						<dl>
							<dt class="left"><label for="Size"><spring:theme code="product.variants.size"/></label></dt>
							<dd class="left">
								<select id="Size">
									<c:if test="${empty variantSizes}">
										<option selected="selected"><spring:theme code="product.variants.select.style"/></option>
									</c:if>
									<c:if test="${not empty variantSizes}">
										<option <c:if test="${empty variantParams['size']}">selected="selected"</c:if>><spring:theme code="product.variants.select.size"/></option>
										<c:forEach items="${variantSizes}" var="variantSize">

											<c:set var="optionsString" value="" />
											<c:forEach items="${variantSize.variantOptionQualifiers}" var="variantOptionQualifier">
												<c:if test="${variantOptionQualifier.qualifier eq 'size'}">
													<c:set var="optionsString">${optionsString} ${variantOptionQualifier.name} ${variantOptionQualifier.value}, </c:set>
												</c:if>
											</c:forEach>

											<c:if test="${(variantSize.stockLevel gt 0) and (variantSize.stockLevelStatus ne 'outOfStock')}">
												<c:set var="stockLevel">${variantSize.stockLevel} <spring:theme code="product.variants.in.stock"/></c:set>
											</c:if>
											<c:if test="${(variantSize.stockLevel le 0) and (variantSize.stockLevelStatus eq 'inStock')}">
												<c:set var="stockLevel"><spring:theme code="product.variants.available"/></c:set>
											</c:if>
											<c:if test="${(variantSize.stockLevel le 0) and (variantSize.stockLevelStatus ne 'inStock')}">
												<c:set var="stockLevel"><spring:theme code="product.variants.out.of.stock"/></c:set>
											</c:if>

											<c:if test="${(variantSize.url eq product.url)}">
												<c:set var="showAddToCart" value="${true}" />
											</c:if>

											<option value="${variantSize.url}" ${(variantSize.url eq product.url) ? 'selected="selected"' : ''}>
												${optionsString} <format:price priceData="${variantSize.priceData}"/>&nbsp;&nbsp;${stockLevel}
											</option>
										</c:forEach>
									</c:if>
								</select>
							</dd>
						</dl>
					</form>
					<a href="#"><spring:theme code="product.variants.size.guide"/></a>
				</div>
			</div>
		</c:if>
		
		<c:if test="${not empty variantOptions}">
			<c:set var="showAddToCart" value="${false}" />
			<div class="variant_options">
				<div class="size">
					<select id="variant">
						<option selected="selected"><spring:theme code="product.variants.select.variant"/></option>
						<c:forEach items="${variantOptions}" var="variantOption">
	
							<c:set var="optionsString" value="" />
							<c:forEach items="${variantOption.variantOptionQualifiers}" var="variantOptionQualifier">
								<c:set var="optionsString">${optionsString} ${variantOptionQualifier.name} ${variantOptionQualifier.value}, </c:set>
							</c:forEach>
	
							<c:if test="${(variantOption.stockLevel gt 0) and (variantSize.stockLevelStatus ne 'outOfStock')}">
								<c:set var="stockLevel">${variantOption.stockLevel} <spring:theme code="product.variants.in.stock"/></c:set>
							</c:if>
							<c:if test="${(variantOption.stockLevel le 0) and (variantSize.stockLevelStatus eq 'inStock')}">
								<c:set var="stockLevel"><spring:theme code="product.variants.available"/></c:set>
							</c:if>
							<c:if test="${(variantOption.stockLevel le 0) and (variantSize.stockLevelStatus ne 'inStock')}">
								<c:set var="stockLevel"><spring:theme code="product.variants.out.of.stock"/></c:set>
							</c:if>
	
										
							<c:if test="${variantOption.url eq product.url}">
								<c:set var="showAddToCart" value="${true}" />
							</c:if>
			
							<option value="${variantOption.url}" ${(variantOption.url eq product.url) ? 'selected="selected"' : ''}>
								${optionsString} <format:price priceData="${variantOption.priceData}"/>&nbsp;&nbsp;${stockLevel}
							</option>
						</c:forEach>
					</select>
				</div>
			</div>
		</c:if>

		<product:productAddToCartPanel product="${product}" allowAddToCart="${showAddToCart}"/>

		<%-- AddThis Button BEGIN --%>
		<a	style="width:135px"
			href="//www.addthis.com/bookmark.php?v=250&amp;pub=xa-4afd47c25121d881"
			class="addthis_button"> <img width="135" height="18"
			style="border: 0pt none;" alt="<spring:theme code="product.bookmark.and.share"/>"
			title="<spring:theme code="product.bookmark.and.share"/>"
			src="<c:url value="/_ui/desktop/common/images/addthis.gif"/>">
		</a>
		<script
			src="//s7.addthis.com/js/250/addthis_widget.js#pub=xa-4afd47c25121d881"
			type="text/javascript"></script>
		<%-- AddThis Button END --%>

	</div>
</div>
