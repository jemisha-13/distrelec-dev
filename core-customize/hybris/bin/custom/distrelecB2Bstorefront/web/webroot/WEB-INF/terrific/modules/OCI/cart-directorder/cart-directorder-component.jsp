<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/terrific/format"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<spring:eval expression="@configurationService.configuration.getString('factfinder.json.suggest.url')" var="autocompleteUrl" scope="application" />

<c:set var="isLoggedIn" value="true" />

<div id="currentTypeahea$directOrder0" class="quickorder quickorder--component">

	<sec:authorize access="!hasRole('ROLE_CUSTOMERGROUP')">
		<hr class="divider" />
		<c:set var="isLoggedIn" value="false" />
	</sec:authorize>
	<h2 class="heading"><spring:message code="cart.directorder.direct.order" /></h2>

	<ul>
		<li><a href="#"><spring:message code="" text="Art No."/></a></li>
		<li><a <c:if test="${isLoggedIn eq false}">class="favorites-modal-tab"</c:if> href="/shopping/favorite" data-aasectionpos="c3r1"
			   data-aasectiontitle="Welcome to Distrelec"
			   data-aabuttonpos="5"
			   data-aalinktext="Favourites"
			   data-aatype="homepage-interaction"
		><spring:message code="home.quick.order.favorite"/></a></li>
	</ul>

	</ul>
	<div class="row quickorder--field <c:if test="${isLoggedIn eq true}">isLogged</c:if>">

		<div class="col-8 col-md-9 quickorder__article">
			<input id="directOrder" name="directOrder" class="field col-12 quickorder__aticle-number" type="text" autocomplete="off"
				   data-typeahead-uri="${autocompleteUrl}" data-typeahead-channel="${ffsearchChannel}"
				   placeholder="<spring:message code='cart.directorder.articleNumberLong'/>" />
			<ul class="direct-prods typeahead" ></ul>
		</div>
		<div class="quickorder__numeric col-4 col-md-3"
			 data-min="1"
			 data-min-default="1"
			 data-step="1"
			 data-step-default="1"
			 data-min-error="<spring:message code='validation.error.min.order.quantity.default' />"
			 data-step-error="<spring:message code='validation.error.steps.order.quantity.default' />"
		>
			<%-- attr type="button" is needed to prevent IE9 from triggering button click when selecting a suggestion by hitting enter --%>
			<%-- see http://stackoverflow.com/questions/12325066/button-click-event-fires-when-pressing-enter-key-in-different-input-no-forms --%>
			<input type="text" name="quantityField" placeholder="<spring:message code='cart.directorder.qty'/>" class="col-12 quickorder__quantity field">

			<div class="numeric-popover hidden">
				<div class="arrow"></div>
				<div class="popover-content"></div>
			</div>
		</div>

	</div>

	<div class="quickorder__fieldWrapper">

	</div>

	<button class="mat-button mat-button__normal--action-blue quickorder__cta--add-product btn-add-field pull-right"
			data-aaSectionPos="c3r1" data-aaSectionTitle="Quick order" data-aaButtonPos="2" data-aaLinkText="Add Another Product" data-aaType="homepage-interaction">
		<i class="fas fa-plus"></i><spring:message code="home.quick.order.add.more" />
	</button>

	<div class="quickorder__cta--floating-add-to-cart">
		<button id="addProduct${index}" class="col-12 mat-button mat-button__normal--action-red quickorder__cta--add-to-cart btn-add-product fb-add-to-cart ellipsis pull-left"
				@click.native data-aasectionpos="c3r1"
				data-aasectiontitle="Quick order"
				data-aabuttonpos="1"
				data-aalinktext="Add to cart"
				data-aatype="homepage-interaction">
			<spring:message code="base.add-to-cart" />
		</button>
	</div>

</div>


<script id="tmpl-cart-directorder-typeahead" type="text/x-dot-template">
	{{~it :item:id }}
	<li class="direct-prod base" data-code="{{= item.code }}" data-code-erp-relevant="{{= item.codeErpRelevant }}" data-name="{{= item.name }}" data-price="{{= item.formattedPrice }}" title="{{= item.name }}"
		data-min="{{= item.ItemsMin }}" data-step="{{= item.ItemsStep }}">
		<div class="direct-prod-image">
			<div class="image-wrap">
				<img src="{{= item.thumbnail.url }}" alt="{{= item.thumbnail.alt }}" />
			</div>
		</div>
		<div class="direct-prod-info">
			<h3 class="ellipsis" title="{{= item.name }}">{{= item.name }}</h3>

			<p>
				<strong>
					<spring:message code="cart.directorder.articleNumber" />
				</strong>
				{{= item.codeErpRelevant }}
			</p>
		</div>
		<div class="direct-prod-currency">
			<p>{{= item.unit }} <spring:message code="cart.directorder.pieces" /></p>
			<sub>{{= item.currency }}</sub>
		</div>
		<div class="direct-prod-price">{{= item.formattedPrice}}</div>
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
