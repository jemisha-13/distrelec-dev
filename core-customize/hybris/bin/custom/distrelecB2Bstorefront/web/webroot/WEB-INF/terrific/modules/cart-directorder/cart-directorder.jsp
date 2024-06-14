<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<spring:message code="cart.directorder.direct.order" var="sQuickOrderTitle" text="Quick order" />
<spring:message code="cart.directorder.input.placeholder" var="sInputPlaceholder" text="Placeholder text" />

<spring:eval expression="@configurationService.configuration.getString('factfinder.json.suggest.url')" var="autocompleteUrl" scope="application" />

<div class="mod-cart-directorder__main">
	<div class="holder">
		<label class="required d-none" for="directOrder">
			${sQuickOrderTitle}
		</label>
		<div class="holder__relative">
			<input id="directOrder" name="directOrder" placeholder="${sInputPlaceholder}" class="holder__input field" type="text" autocomplete="off" data-typeahead-uri="${autocompleteUrl}" data-typeahead-channel="${ffsearchChannel}" />
			<ul class="direct-prods typeahead"></ul>
		</div>
		<div class="numeric numeric-small holder__numeric quickorder__numeric cart-quickorder__numeric"
			 data-min="1"
			 data-min-default="1"
			 data-step="1"
			 data-step-default="1"
			 data-min-error="<spring:message code='validation.error.min.order.quantity.default' />"
			 data-min-error-default="<spring:message code='validation.error.min.order.quantity.default' />"
			 data-step-error="<spring:message code='validation.error.steps.order.quantity.default' />"
			 data-step-error-default="<spring:message code='validation.error.steps.order.quantity.default' />"
		>
			<div class="btn-wrapper">
				<button class="btn-wrapper__btn btn numeric-btn numeric-btn-down disabled">&ndash;</button>
				<input  class="btn-wrapper__input ipt" type="text" name="countItems" placeholder="1" value="1" />
				<button class="btn-wrapper__btn btn numeric-btn numeric-btn-up">+</button>
			</div>
			<div class="numeric-popover popover top">
				<div class="arrow"></div>
				<div class="popover-content"></div>
			</div>
		</div>
		<a id="addProduct" data-aainteraction="quick order" class="mat-button mat-button--action-red btn-add-product fb-add-to-cart ellipsis" href="#"><spring:message code='cart.directorder.add.product'/></a>
	</div>
</div>

<script id="tmpl-cart-directorder-typeahead" type="text/x-dot-template">
    {{~it :item:id }}
    <li class="direct-prod base" data-code="{{= item.code }}" data-code-erp-relevant="{{= item.codeErpRelevant }}" data-name="{{= item.name }}" data-price="{{= item.formattedPrice }}" title="{{= item.name }}"
		data-min="{{= item.ItemsMin }}" data-step="{{= item.ItemsStep }}">

		<div class="direct-prod__content">
			<div class="direct-prod-image">
				<div class="image-wrap">
				<picture>
                    <source sourceset="{{=item.thumbnail.url }}">
					<img src="{{=item.thumbnail.url }}" alt="{{= item.thumbnail.alt }}" />
				</picture>
				</div>
			</div>
			<div class="direct-prod-info">
				<h3 class="ellipsis" title="{{= item.name }}">{{= item.name }}</h3>
			</div>
			<div class="direct-prod-art">
				<p>
					<strong>
						<spring:message code="cart.directorder.articleNumber" />
					</strong>
					{{= item.codeErpRelevant }}
				</p>
			</div>
			<div class="direct-prod-price">{{= item.formattedPrice}}</div>
			<div class="direct-prod-currency">
				<p>{{= item.unit }} <spring:message code="cart.directorder.pieces" /></p>
				<sub>{{= item.currency }}</sub>
			</div>
		</div>
		<div class="direct-prod__btn">
			<span class="mat-button mat-button--action-green">
				select
			</span>
		</div>

        <a class="direct-prod-click"></a>

		{{? item.energyEfficiencyData.length === 2 }}
			<mod:energy-efficiency-label skin="cart-directorder"
										 productCode="{{= item.productCode }}"
										 productEnergyEfficiency="{{= item.energyEfficiencyData[0] }}"
										 productManufacturer="{{= item.manufacturer }}"
										 productType="{{= item.typeName }}"
										 productEnergyPower="{{= item.energyEfficiencyData[1] }}"
			/>
		{{?}}


	</li>
    {{~}}
</script>
