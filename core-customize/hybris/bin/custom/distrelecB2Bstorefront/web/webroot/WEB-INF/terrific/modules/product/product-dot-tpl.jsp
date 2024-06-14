<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script id="tmpl-product-list-item" type="text/x-template-dotjs">
	<li class="mod mod-product skin-product-search{{? it.eol || it.phaseOut == true }} skin-product-not-buyable{{?}}">

	<%-- Used for productlist and in product.js --%>
	<input type="hidden" class="hidden-product-code" value="{{= it.code }}" />
	<%-- End productlist and product.js --%>

		<article class="main" data-product-url="{{= it.url }}">
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

			{{? it.buyable == true && it.catPlusItem == false}}
				<nav class="ctrls">
					<mod:product-tools productId="{{= it.code }}"/>
				</nav>
			{{?}}
			{{? it.buyable==true }}
				{{? it.showProductLink==true }}
					<a href="{{= it.url }}" class="teaser-link" data-position="{{= it.position }}">
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
				<a href="{{= it.url }}" class="title-link" data-position="{{= it.position }}">
			{{?}}
				<h3 class="title">
					<span class="ellipsis" title="{{= it.name }}">{{= it.name }}</span>
				</h3>
			{{? it.showProductLink==true }}
				</a>
			{{?}}

			<div class="list-attribs loading">
				<div class="table" data-position="{{= it.position }}">
					<div class="table-row">
						<div class="table-cell first">
							<span class="label"><spring:message code="product.articleNumber" /></span>
							<span class="value">{{= it.codeErpRelevant }}</span>
							{{? it.productFamilyUrl != ''}}
								<spring:message code="product.family.linkTextExtended" var="sProductFamilyLinkText" />
								<span class="value" ><a class="product-family" title="${sProductFamilyLinkText}" href="{{= it.productFamilyUrl }}">${sProductFamilyLinkText}<i></i></a></span>
							{{?}}
						</div>
						<div class="table-cell second">
							&nbsp;
						</div>
					</div>
					<div class="table-row">
						<div class="table-cell first"><span class="label"><spring:message code="product.typeName" /></span><span class="value type-name">{{= it.typeName }}</span></div>
						<div class="table-cell second"><span class="label"><spring:message code="product.stock" /></span><span class="value"><span class="product-stock"></span></span></div>
					</div>
					<div class="table-row last">
						<div class="table-cell first"><span class="label"><spring:message code="product.manufacturer" /></span><span class="value">{{= it.manufacturer }}</span></div>
						<div class="table-cell second"><span class="label"><spring:message code="product.packing.unit" /></span><span class="value">{{= it.salesUnit }}</span></div>
					</div>
				</div>
				<mod:scaled-prices template="dot-tpl" skin="product-list" />
			</div>

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
		</article>
	</li>
</script>
