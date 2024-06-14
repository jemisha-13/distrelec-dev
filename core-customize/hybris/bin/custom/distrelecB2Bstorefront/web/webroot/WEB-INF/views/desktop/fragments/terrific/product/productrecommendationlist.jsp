<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="ulid" value="prodRecoUL${recoId}"/>

<jsp:useBean id="random" class="java.util.Random" scope="application"/>
<c:set var="divId" value="div${random.nextInt(1000)}"/>

<c:choose>
	<c:when test="${not empty productReferences}">
		<c:choose>
			<c:when test="${style == \"STANDARD\"}">
				<div id="${divId}" class="carousel-component__headline">${fn:escapeXml(title)}</div>
				<div id="${ulid}" class="carousel__component--carousel carousel-standard row">
					<c:forEach items="${productReferences}" var="product" varStatus="lStatus">
						<c:url value="${product.target.url}" var="productUrl"/>
						<div class="carousel-standard__item"
						data-prodreco-item="prodRecoItem"
						data-prodreco-item-code='${product.target.code}'
						data-prodreco-item-component-id='${componentId}'>
							<a class="carousel-standard__item__anchor" href="${productUrl}" data-aainteraction="recommendation click" data-product-id="${product.target.code}" data-position="${lStatus.count}" data-pagination="Carousel" data-component="${componentId}" data-scenarioID="${recoType}" data-stock="${stockLevels[product.target.code]}">
								<div class="carousel-standard__item__thumb">
									<c:set var="imageUrl" value="${not empty product.productImages[0].portrait_small_webp.url ? product.productImages[0].portrait_small_webp.url : not empty product.productImages[0].portrait_small.url ? product.productImages[0].portrait_small.url : '/_ui/all/media/img/missing_portrait_small.png' }"/>
									<c:set var="imageAlt" value="${not empty product.productImages[0].portrait_small_webp.altText ? product.productImages[0].portrait_small_webp.altText : not empty product.productImages[0].portrait_small.altText ? product.productImages[0].portrait_small.altText : sImageMissing }"/>
									<img alt="${imageAlt}" src="${imageUrl}" />
								</div>

								<c:set var = "maxLength" value = '32'/>
								<div class="carousel-standard__item__name">
									<c:choose>
										<c:when test = '${fn:length(product.target.name) <= maxLength}'>
											${fn:escapeXml(product.target.name)}
										</c:when>
										<c:otherwise>
											${fn:substring(fn:escapeXml(product.target.name), 0, maxLength)}...
										</c:otherwise>
									</c:choose>
								</div>
								<div class="carousel-standard__item__stock"><spring:message code="product.stock" />  &nbsp;${stockLevels[product.target.code]}</div>
								<div class="carousel-standard__item__price"><format:fromPrice priceData="${product.target.price}"/></div>
								<div class="carousel-standard__item__btn">
									<span class="btn mat-button mat-button__solid--action-green">
										 <spring:message code="text.shopNow" /> <i class="fas fa-angle-right"></i>
									</span>
								</div>
							</a>
						</div>
					</c:forEach>
				</div>
			</c:when>
			<c:when test="${style == \"GRID\"}">
				<div class="carousel-component__headline">${fn:escapeXml(title)}</div>
				<div class="carousel__component--carousel carousel-grid row">
					<c:forEach items="${productReferences}" var="product" varStatus="lStatus">
						<c:url value="${product.target.url}" var="productUrl"/>
						<div class="col-12 col-sm-6 col-lg-3 carousel__item"
							 data-prodreco-item="prodRecoItem"
							 data-prodreco-item-code='${product.target.code}'
							 data-prodreco-item-component-id='${componentId}'>
							<a class="card-item-anchor" href="${productUrl}" data-aainteraction="recommendation click" data-product-id="${product.target.code}" data-position="${lStatus.count}" data-pagination="Grid" data-component="${componentId}" data-scenarioID="${recoType}" data-stock="${stockLevels[product.target.code]}">
								<div class="card-item">
									<div class="card-item__image">
										<img alt="${product.target.productImages[0].portrait_small.altText == null ? sImageMissing : product.target.productImages[0].portrait_small.altText}" src="${product.target.productImages[0].portrait_small.url eq null ?  "/_ui/all/media/img/missing_portrait_small.png" : product.target.productImages[0].portrait_small.url}" />
									</div>
									<c:set var="productName" value="${fn:substring(product.target.name, 0, 60)}" />
									<div class="card-item__content">
										<h3>${fn:escapeXml(productName)}</h3>
										<div id="wrapper3" class="stock-msg">
											<p><spring:message code="product.stock" /> &nbsp;${stockLevels[product.target.code]}</span></p>
										</div>
										<p class="price">
											<format:fromPrice priceData="${product.target.price}"/>
										</p>
									</div>
								</div>
							</a>
						</div>
					</c:forEach>
				</div>
			</c:when>
		</c:choose>
	</c:when>
</c:choose>
