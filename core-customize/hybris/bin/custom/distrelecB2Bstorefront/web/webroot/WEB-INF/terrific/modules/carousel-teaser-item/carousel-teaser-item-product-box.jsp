<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="namicscommerce" uri="/WEB-INF/tld/namicscommercetags.tld"%>

<spring:message code="product.carousel-teaser.image.missing" text="Image not found" var="sImageMissing"/>

<!-- Used in carousel-teaser-item-product and toolsitem module -->
<input type="hidden" class="hidden-product-code" value="${itemData.code}" />
<!-- End carousel-teaser-item-product and toolsitem module -->

<c:set var="salesUnitLowerCase" value="${fn:toLowerCase(itemData.salesUnit)}" />
<c:if test="${salesUnitLowerCase eq '1 pieces'}">
	<c:set var="salesUnitLowerCase" value="1 piece" />
</c:if>

<div id="wrapper" class="teaserItemProductLazy">
	<div id="cleared">
 		<div class="ft-description ellipsis">
			<h3 class="ellipsis" title="${itemData.name}">
				<a href="${itemData.url}">
					${itemData.name}
				</a>    
			</h3> 
 		</div>	
	</div>
  	<div id="leftColumn">
  		<div class="bd">
  			<a href="${itemData.url}">
				<c:set var="productImage" value="${itemData.productImages[0]}"/>
				<c:set var="landscapeSmallJpg" value="${not empty productImage.landscape_small.url ? productImage.landscape_small.url : '/_ui/all/media/img/missing_landscape_small.png' }"/>
				<c:set var="landscapeSmallWebP" value="${not empty productImage.landscape_small_webp.url ? productImage.landscape_small_webp.url : landscapeSmallJpg}"/>
				<c:set var="landscapeSmallAlt" value="${not empty productImage.landscape_small_webp.altText ? productImage.landscape_small_webp.altText : not empty productImage.landscape_small.altText == null ? productImage.landscape_small.altText : sImageMissing }"/>
				<picture>
					<source srcset="${landscapeSmallWebP}">
					<img width="200" height="168" class="item-image" alt="${landscapeSmallAlt}" src="${landscapeSmallJpg}"/>
				</picture>
			</a>
  		</div>
  	</div>
  	<div id="rightColumn">
		<c:if test="${not empty itemData.distManufacturer}">
			<div class="manufacturerName ellipsis" title="${itemData.distManufacturer.name}">  	
				${itemData.distManufacturer.name}
			</div>		
		</c:if>
			


		<div id="wrapper2" class="stock-${itemData.code}" data-productCode="${itemData.code}" >
			<div id="leftColumn2" class="ellipsis" title="<spring:message code='product.stock' text='Stock' />">
				<spring:message code='product.stock' text='Stock' />
			</div>
			<div id="rightColumn2">
			</div>
		</div>
		

		<div id="wrapper2">
			<div id="leftColumn2long" class="ellipsis leftColumn2long specialHeightLeft2Column" title="<spring:message code="buyingSection.per.salesUnit.excl.vat.short" arguments="${salesUnitLowerCase}" />" >
				<spring:message code="buyingSection.per.salesUnit.excl.vat.short" arguments="${salesUnitLowerCase}" />
			</div>
			<div id="rightColumn2">
			</div>
		</div>
		
		

		
		<div id="wrapper2">
			<div id="leftColumn2Price" class="ellipsis" title="<spring:message code='quotationhistory.price' text='Price' />" >
			</div>
			<div id="rightColumn2" class="priceTeaserItem priceTeaserItemHorizontal">
				<format:price format="defaultSplit" priceData="${itemData.price}" />
			</div>
		</div>
		

		
		<div class="ft">
			<a class="btn btn-buy ellipsis reloadPage" href="#"><spring:message code='carpet.add.cart' text='Buy' /></a>
		</div>
  		
  	</div>
</div>


