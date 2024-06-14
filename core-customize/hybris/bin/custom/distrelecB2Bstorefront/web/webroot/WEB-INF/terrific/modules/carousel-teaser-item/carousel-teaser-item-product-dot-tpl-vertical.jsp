<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>

<!-- Used in carousel-teaser-item-product and toolsitem module -->
<input type="hidden" class="hidden-product-code" value="{{= it.code }}" />
<!-- End carousel-teaser-item-product and toolsitem module -->
 
<c:set var="codeErpRelevant" value="${product.code == undefined ? 'x' : product.code}" />

<c:set var="salesUnitLowerCase" value="{{= it.salesUnit }}" />
<c:if test="${salesUnitLowerCase eq '1 pieces'}">
	<c:set var="salesUnitLowerCase" value="1 piece" />
</c:if>


<div id="wrapper" class="teaserItemProductLazy">
	<div id="cleared">
 		<div class="ft-description ellipsis"> 
			<h3 class="ellipsis" title="{{= it.name }}">   
				<a href="{{= it.url }}">
					{{= it.name }}
				</a>    
			</h3> 
 		</div>	
	</div>
  	<div id="leftColumn">
  		<div class="bd">
  			<a href="{{= it.url }}">   
  				<img width="180" height="138" class="item-image" alt="{{= it.productImageAltText}}" 
  					src="{{= it.productImageUrl.replace(/portrait_small/, 'landscape_medium') }}" />
  			</a>
  		</div>
  	</div>
  	<div id="rightColumn">
		<div class="manufacturerName ellipsis" title="{{= it.distManufacturer.name }}">  	
			{{= it.distManufacturer.name }}
		</div>		

		<div id="wrapper2" class="stock-{{= it.code }}" data-productCode="{{= it.code }}" >
			<div id="leftColumn2" class="ellipsis" title="<spring:message code='product.stock' text='Stock' />">
				<spring:message code='product.stock' text='Stock' />
			</div>
			<div id="rightColumn2">
			</div>
		</div>

		<br />
		
	
		<div id="wrapper2">
			<div id="leftColumn2long" class="ellipsis leftColumn2long" title="<spring:message code="buyingSection.per.salesUnit.excl.vat.short" arguments="${salesUnitLowerCase}" />" >
				<spring:message code="buyingSection.per.salesUnit.excl.vat.short" arguments="${salesUnitLowerCase}" />
			</div>
			<div id="rightColumn2">

			</div>
		</div>
		
		<br />
		

		
		<div id="wrapper2">
			<div id="leftColumn2Price" class="ellipsis" title="<spring:message code='quotationhistory.price' text='Price' />" >
			</div>
			<div id="rightColumn2" class="priceTeaserItem">
				<span class="currency">{{= it.price.currency }}</span>&nbsp;{{= it.price.formattedValue }}
			</div>
		</div>
		
		<br />
		
		<div class="ft">
			<a class="btn btn-buy ellipsis reloadPage" href="#"><spring:message code='carpet.add.cart' text='Buy' /></a>
		</div>
  		
  	</div>
</div>