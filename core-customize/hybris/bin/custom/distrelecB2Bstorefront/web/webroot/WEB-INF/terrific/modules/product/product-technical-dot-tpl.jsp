<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script id="tmpl-product-list-item-technical" type="text/x-template-dotjs">
	<li class="mod mod-product skin-product-search skin-product-technical-old{{? it.eol || it.phaseOut == true }} skin-product-not-buyable{{?}}">

	<%-- Used for productlist and in product.js --%>
	<input type="hidden" class="hidden-product-code" value="{{= it.code }}" />
	<input type="hidden" class="hidden-product-name" value="{{= it.nameURIEncoded }}" />
	<input type="hidden" class="hidden-manufacturer" value="{{= it.manufacturerURIEncoded }}" />
	<input type="hidden" class="hidden-product-price" value="{{= it.price.formattedValue }}" />
	<input type="hidden" class="hidden-product-type-name" value="{{= it.typeNameURIEncoded }}" />
	<%-- End productlist and product.js --%>

		<article class="main" data-product-url="{{= it.url }}">
			{{? it.buyable==true }}
				{{? it.showProductLink==true }}
					<a href="{{= it.url }}" class="teaser-link">
				{{?}}
					<div class="image-container">
						<div class="image-wrapper">
							<img width="83" height="110" alt="{{= it.productImageAltText}}" src="{{= it.productImageUrl}}" />	
						</div>
					</div>
				{{? it.showProductLink==true }}
					</a>
				{{?}}
			{{?}}
			{{? it.showProductLink==true }}
				<a href="{{= it.url }}" class="title-link">
			{{?}}
				<h3 class="title">
					<span class="ellipsis" title="{{= it.name }}">{{= it.name }}</span>
				</h3>
			{{? it.showProductLink==true }}
				</a>
			{{?}}
			{{? it.activePromotionLabels != ''}}
				<div class="productlabel-wrap">
					{{~it.activePromotionLabels :item:id}}
						{{? id==0 }}
							<mod:product-label label="{{= item.label }}" code="{{= item.code }}"/>
						{{?}}
					{{~}}
				</div>
			{{?}}


			<mod:energy-efficiency-label skin="product" productCode="{{= it.code }}" productEnergyEfficiency="{{= it.productEnergyEfficiency }}"  />


			{{? it.eol != false}}
				<div class="eol{{? it.buyableReplacementProduct != false}} hasReplacement{{?}}">
					<p class="eol-message"><spring:message code="product.notBuyable.endOfLife.message" arguments="{{= it.endOfLifeDate}},{{= it.replacementReason}}"/></p>
					<a class="btn-secondary" href="{{= it.url }}"><spring:message code="product.notBuyable.endOfLife.alternative"/></a>
				</div>
			{{?}}
			{{? it.phaseOut == true}}
				<div class="phase-out">
					<p class="phase-out-message"><spring:message code="product.notBuyable.temporarly.message" /></p>
				</div>
			{{?}}

			<div class="list-attribs loading">
				<div class="table">
					<div class="table-row">
						<div class="table-cell first">
							<span class="label"><spring:message code="product.articleNumber" /></span>
							<span class="value">{{= it.codeErpRelevant }}</span>
							{{? it.productFamilyUrl != ''}}
								<spring:message code="product.family.linkText" var="sProductFamilyLinkTechnicalText" />
								<span class="value" ><a class="product-family" title="${sProductFamilyLinkTechnicalText}" href="{{= it.productFamilyUrl }}">${sProductFamilyLinkTechnicalText}<i></i></a></span>
							{{?}}
						</div>
						<div class="table-cell second">
							{{? it.technicalAttributes[0]}}							
							<span class="label" title="{{= it.technicalAttributes[0].key}}">{{= it.technicalAttributes[0].key}}</span><span class="value" title="{{= it.technicalAttributes[0].value}}">{{= it.technicalAttributes[0].value}}</span>
							{{?}}
						</div>						
						<div class="table-cell third">
							{{? it.technicalAttributes[5]}}							
							<span class="label" title="{{= it.technicalAttributes[5].key}}">{{= it.technicalAttributes[5].key}}</span><span class="value" title="{{= it.technicalAttributes[5].value}}">{{= it.technicalAttributes[5].value}}</span>
							{{?}}
						</div>
					</div>
					<div class="table-row">
						<div class="table-cell first"><span class="label"><spring:message code="product.typeName" /></span><span class="value type-name">{{= it.typeName }}</span></div>						
						<div class="table-cell second">
							{{? it.technicalAttributes[1]}}
							<span class="label" title="{{= it.technicalAttributes[1].key}}">{{= it.technicalAttributes[1].key}}</span><span class="value" title="{{= it.technicalAttributes[1].value}}">{{= it.technicalAttributes[1].value}}</span>
							{{?}}
						</div>						
						<div class="table-cell third">
							{{? it.technicalAttributes[6]}}
							<span class="label" title="{{= it.technicalAttributes[6].key}}">{{= it.technicalAttributes[6].key}}</span><span class="value" title="{{= it.technicalAttributes[6].value}}">{{= it.technicalAttributes[6].value}}</span>
							{{?}}
						</div>
					</div>
					<div class="table-row">
						<div class="table-cell first"><span class="label"><spring:message code="product.manufacturer" /></span><span class="value manufacturer">{{= it.distManufacturer.name }}</span></div>						
						<div class="table-cell second">
							{{? it.technicalAttributes[2]}}
							<span class="label" title="{{= it.technicalAttributes[2].key}}">{{= it.technicalAttributes[2].key}}</span><span class="value" title="{{= it.technicalAttributes[2].value}}">{{= it.technicalAttributes[2].value}}</span>
							{{?}}
						</div>						
						<div class="table-cell third">
							{{? it.technicalAttributes[7]}}
							<span class="label" title="{{= it.technicalAttributes[7].key}}">{{= it.technicalAttributes[7].key}}</span><span class="value" title="{{= it.technicalAttributes[7].value}}">{{= it.technicalAttributes[7].value}}</span>
							{{?}}
						</div>
					</div>
					<div class="table-row">
						<div class="table-cell first"><%-- price comes from absolute positioned module scaled-prices --%></div>						
						<div class="table-cell second">
							{{? it.technicalAttributes[3]}}
							<span class="label" title="{{= it.technicalAttributes[3].key}}">{{= it.technicalAttributes[3].key}}</span><span class="value" title="{{= it.technicalAttributes[3].value}}">{{= it.technicalAttributes[3].value}}</span>
							{{?}}
						</div>						
						<div class="table-cell third">
							{{? it.technicalAttributes[8]}}
							<span class="label" title="{{= it.technicalAttributes[8].key}}">{{= it.technicalAttributes[8].key}}</span><span class="value" title="{{= it.technicalAttributes[8].value}}">{{= it.technicalAttributes[8].value}}</span>
							{{?}}
						</div>
					</div>
					<div class="table-row last">
						<div class="table-cell first">
							<%-- DISTRELEC-8425 --%>
							<%-- span class="load-complete available-text label"><spring:message code="product.available" /></span> <ul class="available-bar small"></ul --%>
							<span class="load-complete available-text label"><spring:message code="product.stock" /></span><span class="value"><span class="product-stock"></span></span>
							<span class="load-in-process label"><spring:message code="product.shipping.loading.availability" /></span>
						</div>						
						<div class="table-cell second">
							{{? it.technicalAttributes[4]}}
							<span class="label" title="{{= it.technicalAttributes[4].key}}">{{= it.technicalAttributes[4].key}}</span><span class="value" title="{{= it.technicalAttributes[4].value}}">{{= it.technicalAttributes[4].value}}</span>
							{{?}}
						</div>						
						<div class="table-cell third">
							{{? it.technicalAttributes[9]}}
							<span class="label" title="{{= it.technicalAttributes[9].key}}">{{= it.technicalAttributes[9].key}}</span><span class="value" title="{{= it.technicalAttributes[9].value}}">{{= it.technicalAttributes[9].value}}</span>
							{{?}}
						</div>
					</div>
				</div>
				<mod:scaled-prices template="productlist-technical-dot-tpl" skin="product-list-technical" />
			</div>
		</article>
		{{? it.buyable == true && it.catPlusItem == false}}
			<nav class="ctrls">
				<mod:product-tools productId="{{= it.code }}"/>
			</nav>
		{{?}}
	</li>
</script>
