<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="mod" tagdir="/WEB-INF/tags/terrific/modules"%>
<spring:message code="cart.list.addReference" text="Add a reference code" var="sAddReferenceText"/>
<spring:message code="cart.list.done" text="Done" var="sDoneText"/>
<spring:message code="cart.list.editBtn" text="Edit" var="sEditText"/>

<script id="tmpl-cart-list-item" type="text/x-dot-template">

<div class="mod mod-cart-list-item skin-cart-list-item-cart loading">
	<div class="skin-cart-list-item-cart__holder">
		<!-- Used by cart-list module !-->
		<input type="hidden" class="hidden-product-code" value="{{= it.code }}" />
		<input type="hidden" class="hidden-entry-number" value="{{= it.entryNumber }}" />
		<!-- End cart-list module !-->


		<div class="productlabel-wrap">
			<div class="mod mod-productlabel">
				{{~it.productLabel :item:id}}
				{{? id==0 }}
				<mod:product-label label="{{= item.label }}" code="{{= item.code }}"/>
				{{?}}
				{{~}}
			</div>
		</div>

		<div class="skin-cart-list-item-cart__holder__image">
			<div class="image-wrap">
				<a href="{{=it.productUrl}}">
					<picture>
						<source sourceset="{{=it.thumbUrlWebp}}">
						<img alt="{{= it.thumbUrlAlt }}" src="{{= it.thumbUrl }}" />
					</picture>
				</a>
			</div>
		</div>
		<div class="skin-cart-list-item-cart__holder__title">
			<h3 class="ellipsis" title="{{=it.name }}"><a href="{{=it.productUrl}}">{{=it.name }}</a></h3>
		</div>

		<div class="skin-cart-list-item-cart__holder__content">
			<div class="desktop-title">
				<span class="ellipsis" title="{{=it.name }}"><a href="{{=it.productUrl}}">{{=it.name }}</a></span>
			</div>
			<div class="cell-info-table">
				<div class="cell-info-cell">
					<div class="hd"><spring:message code="cart.list.articleNumber" /></div>
					<div class="bd ellipsis" title="{{= it.codeErpRelevant }}">{{= it.codeErpRelevant }}</div>
				</div>
				<div class="cell-info-cell">
					<div class="hd"><spring:message code="cart.list.typeName" /></div>
					<div class="bd ellipsis" title="{{= it.typeNameFull }}">{{= it.typeName }}</div>
				</div>
				<div class="cell-info-cell">
					<div class="hd"><spring:message code="cart.list.manufacturer" /></div>
					<div class="bd ellipsis" title="{{= it.manufacturerFull }}">{{= it.manufacturer }}</div>
				</div>
			</div>
		</div>

		<div class="skin-cart-list-item-cart__holder__availability cell cell-availability">
			<div class="availability">
				<div class="load-complete available"></div>
			</div>
		</div>

		<div class="skin-cart-list-item-cart__holder__numeric">
			<div class="numeric numeric-small"
				 data-min="{{= it.orderQuantityMinimum }}"
				 data-step="{{= it.orderQuantityStep }}"
				 data-min-error="<spring:message code='validation.error.min.order.quantity' arguments='{{= it.codeErpRelevant }},{{= it.orderQuantityMinimum }}' htmlEscape='true' />"
				 data-step-error="<spring:message code='validation.error.steps.order.quantity' arguments='{{= it.codeErpRelevant }},{{= it.orderQuantityStep }}' htmlEscape='true' />"
			>

				<div class="btn-wrapper">
					<button class="btn-wrapper__btn btn numeric-btn numeric-btn-down disabled">&ndash;</button>
					<input  class="btn-wrapper__input ipt" type="text" name="countItems" value="{{= it.orderQuantityValue }}" />
					<button class="btn-wrapper__btn btn numeric-btn numeric-btn-up">+</button>
				</div>
				<div class="numeric-popover popover top">
					<div class="arrow"></div>
					<div class="popover-content"></div>
				</div>

			</div>

			<div class="toolbar">
				<button class="btn-numeric-remove"><spring:message code="cart.list.remove" /></button>
				<div class="toolbar__edit">
				<span class="toolbar__edit__reference">

				</span>
					<span class="toolbar__edit__text">
						${sEditText}
					</span>
				</div>
				<span class="toolbar__add-reference">
					${sAddReferenceText}
				</span>
				<div class="toolbar__input">
					<input name="reference" class="field ipt-reference" maxlength="35" type="text" value="" placeholder="<spring:message code='cart.list.reference' />" />
					<span class="toolbar__input__btn">
						${sDoneText}
					</span>
				</div>
			</div>

		</div>
</div>

</script>
