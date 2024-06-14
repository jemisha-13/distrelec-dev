<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>

<!-- Used in carousel-teaser-item-product and toolsitem module -->
<input type="hidden" class="hidden-product-code" value="{{= it.code }}" />
<!-- End carousel-teaser-item-product and toolsitem module -->

<!-- 9.2 Rotating Teaser Tracking (Javascript) !-->
 
<c:set var="codeErpRelevant" value="${product.code == undefined ? 'x' : product.code}" />
<c:set var="teasertrackingid" value="${wtTeaserTrackingId}.mainId=${codeErpRelevant}.{{= it.code }}.-" />
<spring:message code='carpet.add.moreInfo' text='More Information' var="sMoreInfo" />


<a href="{{= it.url }}" class="item-link" data-position={{=it.position+1}}>
	{{? it.showCarouselItemHead == true }}
		<div class="hd">
			{{? it.distManufacturer != ''}}
				<mod:product-manufacturer manufacturerLogoUrl="{{= it.distManufacturer.brand_logo.url}}" manufacturerLogoAltText="{{= it.distManufacturer.name}}" showLink="false" />
			{{?}}
			
			
			{{? it.energyEfficiencyData != undefined}}
				 <mod:energy-efficiency-label skin="recommendation" productCode="{{= it.code }}" productEnergyEfficiency="{{= it.energyEfficiencyData }}"  />
			{{?}}

		</div>
	{{?}}
	<div class="bd" >
		<img class="item-image" alt="{{= it.productImageAltText}}" src="{{= it.productImageUrl.replace(/portrait_small/, 'landscape_medium') }}" />
	</div>
	<div class="ft">
		<div class="ft-description">
			<h3 class="base item-name ellipsis" title="{{= it.name }}">{{= it.name }}</h3>
			<c:if test="${displayPromotionText}">
				<span class="promotion-text ellipsis" title="{{= it.promotiontext }}">{{= it.promotiontext }}</span>
			</c:if>
		</div>
		<c:choose>
			<c:when test="${hidePrice}">
				<span class="btn btn-buy ellipsis">${sMoreInfo}</span>
			</c:when>
			<c:otherwise>
				<a class="btn btn-buy ellipsis reloadPage" href="#"><spring:message code='carpet.add.cart' text='Buy' /></a>
				<span class="product-price"><span class="currency">{{= it.price.currency }}</span>&nbsp;{{= it.price.formattedValue }}</span>
			</c:otherwise>
		</c:choose>
	</div>
</a>
