(function ($) {

	/**
	 * Product Skin Bom implementation for the module Productlist Order.
	 *
	 */
	Tc.Module.MetahdItem.Cart = function (parent) {


		this.on = function (callback) {

			this.$popover = null;
			this.popoverTimer = null;
			this.popoverHideDelay = 4000;

			this.onCartChange = $.proxy(this.onCartChange, this);
			this.hidePopover = $.proxy(this.hidePopover, this);

            var productItemTemp = $('#template-metahd-item-cart-product-item', this.$ctx).html(),
                productItemTempWrapepr = '<a href="{{=it.productUrl}}" class="product-row datalayerCartTrack">'+productItemTemp+'</a>';

			this.tmplCartIsEmpty = doT.template($('#template-metahd-item-cart-is-empty', this.$ctx).html());
			this.tmplProductItem = doT.template(productItemTempWrapepr);

			// Event Listener
			$(document).on('cartDataAvailable', $.proxy(this.updateMiniCart, this));
			$(document).on('cartChange', this.onCartChange);

			parent.on(callback);
		};

		//
		// There is new data for the mini cart
		this.onCartChange = function(ev, data) {
			if(data.type !== 'directOrderQuantityError' && data.type !== 'directOrderProductIdentifierError'){

                // do not show +1 popover on: cart update, cart remove, recalculate update, directOrder - but on other updates (e.g. add to cart from detail page)
				if (data.actionIdentifier === 'bomAddToCart' || data.actionIdentifier === 'shoppingListAddToCart' || data.actionIdentifier === 'directOrderComponent' || data.actionIdentifier === 'orderDetailAddToCart' ) {
                    this.showPopover(data.quantity);
				}  else if (data.actionIdentifier !== 'cartUpdate' && data.actionIdentifier !== 'removeItemFromCart' && data.actionIdentifier !== 'recalculateLayer' && data.actionIdentifier !== 'directOrder'){
				   this.showPopover(data.ajaxSuccessData.cartData.addedQuantity);
			    }

				this.updateMiniCart();
			}
		};

		this.stateLogic = {

			states: [
				'isEmpty',
				'hasProducts'
			],
				skins: [
				'skin-metahd-item-cart-is-empty',
				'skin-metahd-item-cart-has-products'
			],

				currState: '',
				currSkin : '',

				update: function() {

					// Set current state & skin
					this.currState = this.states[0]; 			// isEmpty
					this.currSkin = this.skins[0];

					if(window._cart.totalItems > 0) {
						this.currState = this.states[1]; 		// hasProducts
						this.currSkin = this.skins[1];

					}

			}
		};

		this.updateMiniCart = function() {

			var mod = this,
				$ctx = this.$ctx
				,$flyout = $('.flyout', $ctx)
				,$totalPriceLocal = $('.totalPriceLocal', $flyout)
				,$productCount = $('.product-count', $ctx)
				;

			// Refresh cart state
			this.stateLogic.update();

			// Set Module Skin
			$ctx
				.removeClass(mod.stateLogic.skins.join(' '))
				.addClass(mod.stateLogic.currSkin);

			// Set Product Count
			if(this.stateLogic.currState == 'isEmpty') {
				$productCount.html(window._cart.totalItems);
			} else {
				$productCount.html(window._cart.totalItems);
				if(window._cart.totalItems === 1) {
					$ctx.find('.product').removeClass('hidden');
					$ctx.find('.products').addClass('hidden');
				} else {
					$ctx.find('.products').removeClass('hidden');
					$ctx.find('.product').addClass('hidden');
				}
			}

			// Update Flyout Header Price
			$totalPriceLocal.find('.currency').html(window._cart.currency);
			$totalPriceLocal.find('.value').html(Tc.Utils.formatPrice(window._cart.totalPriceLocal,$('.shopsettings').data('country')));

			// Update Flyout Body
			this.updateMiniCartBody();
		};

		this.updateMiniCartBody = function() {

			var  self = this
				,$flyout = this.$$('.flyout')
				,$flyoutBody = $('.bd', $flyout)
				,aProductListRowHtml = []
				;

			switch(this.stateLogic.currState) {

				case 'hasProducts':

					if(window._cart.products.length > 0) {
						$('.flyout__fade').removeClass('hidden');
					}

					// just the last 4 products should be displayed
					$.each(window._cart.products.slice(-4), function(index, product) {

                        var $countryisocode = $('body').data('isocode');

                        if ( product.thumbUrl.indexOf('/_ui/all/media/img/missing_') > -1 ) {

                            if ( $countryisocode == 'DK' || $countryisocode == 'FI' || $countryisocode == 'NO' || $countryisocode == 'SE' || $countryisocode == 'LT' || $countryisocode == 'LV' || $countryisocode == 'EE' || $countryisocode == 'NL' || $countryisocode == 'PL') {
                                product.thumbUrl = '/_ui/all/media/img/missing_landscape_small-elfa.png';
								product.thumbUrlWebp = '/_ui/all/media/img/missing_landscape_small-elfa.png';
							} else {
                                product.thumbUrl = '/_ui/all/media/img/missing_landscape_small.png';
								product.thumbUrlWebp = '/_ui/all/media/img/missing_landscape_small.png';
							}

						}

						aProductListRowHtml.push(self.tmplProductItem(product));
					});

					$flyoutBody
						.empty()
						.html(aProductListRowHtml.reverse())
					;
					break;

				case 'isEmpty':
					$flyoutBody.html(self.tmplCartIsEmpty(window._cart));
					$('.flyout__fade').addClass('hidden');
					break;

			}

			// add css class
			$flyoutBody
				.attr('class', 'bd')
				.addClass('product-count-' + window._cart.totalItems) // i.e. product-count-12
			;

			if(window._cart.products.length >= 4) {
				$('.flyout__fade').addClass('four-plus');
				$('.flyout-body > .bd').addClass('container-four');
			}

		};


		//
		// Show a popover indicating a user action
		this.showPopover = function(quantityDifference) {
			var content = parseInt(quantityDifference) > 0 ? '<span class="sign">+</span> ' + quantityDifference : '<span class="sign">-</span> ' + parseInt(quantityDifference) * -1,
				$popoverEl
				;

			this.$popover = this.$$('.popover-origin').popover({
				content: content + '<span class="ico-cart-popover"><i></i></span>'
				,placement: 'bottom'
				,trigger: 'manual'
				,html: true
			})
				.popover('show');

			// force the popover width and position to be as designed
			// todo: make re-useable for other metahd-x modules
			$popoverEl = this.$$('.popover');
			$popoverEl.css({
				width: this.$ctx.width() + 3 // review JW to have the same width as the metahd item
			}).offset({
					left: this.$ctx.offset().left - 1,
					top: $popoverEl.offset().top + 3 // review JW to adjust popover to Flyouts
				});

            if ($(window).width() < 767) {
                $popoverEl.css('top','20px');
            }

			this.popoverTimer = setTimeout(this.hidePopover, this.popoverHideDelay);
			this.$popover.next().on('mouseenter', this.hidePopover);
		};

		this.hidePopover = function() {
			clearTimeout(this.popoverTimer);
			this.$popover.popover('destroy');
		};
	};

})(Tc.$);
