(function ($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Cart-list
	 * @extends Tc.Module
	 */
	Tc.Module.CartList = Tc.Module.extend({

		/**
		 * Initialize.
		 *
		 * @method init
		 * @return {void}
		 * @constructor
		 * @param {jQuery} $ctx the jquery context
		 * @param {Sandbox} sandbox the sandbox to get the resources from
		 * @param {Number} id the unique module id
		 */
		init: function ($ctx, sandbox, id) {
			// call base constructor
			this._super($ctx, sandbox, id);

			// subscribe to connector channel/s
			this.sandbox.subscribe('cart', this);
			this.sandbox.subscribe('cartlistBulkAction', this);
			this.sandbox.subscribe('cartlistRemoveAction', this);
			this.sandbox.subscribe('lightboxShoppinglist', this);

            if ($('.skin-cart-list-item-review').length === 0 && $('.order-history').length === 0) {
                this.tmplStockLevel = doT.template($('#tmpl-stock_level', $ctx).html());
                this.tmplStockLevelPickupHeader = $('#tmpl-stock_level_pickup_header', $ctx).html();
                this.tmplStockLevelPickupRow = doT.template($('#tmpl-stock_level_pickup_row', $ctx).html());
                this.tmplCartListItem = doT.template($('#tmpl-cart-list-item', $ctx).html());
            }

		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function (callback) {
			var $ctx = this.$ctx,
				self = this;

 			// Load the Availability of every product in the cart list

			// dont execute ajax availability in Checkout Review or Order History
			if ($('.skin-cart-list-item-review').length === 0 && $('.order-history').length === 0){
				self.getAvailable(0);
			}

			callback();
		},

		/**
		 *
		 * @method getAvailable cart-list.js
		 *
		 * @param start
		 */
		getAvailable: function (start) {
			var self = this,
				$ctx = this.$ctx,
				$listItems = $ctx.find('.cart-list').find('.mod-cart-list-item').not('.sub-item').not('.quote-head').not('.dummy-item'),
				$hiddenCode = $listItems.find('.hidden-product-code'),
				productCodes = [],
				productCodesQuantities = [],
				productNum = [],
				i,
				len = $hiddenCode.length;

			// Gather product data for each cart list item

			// - In the cart where we have the quantity input field
			if (len == $('input:text.ipt').length - 1) {
				for (i = start; i < len; i += 1) {
					productCodesQuantities.push($hiddenCode.eq(i).val() + ';' + $('input:text.ipt')[i].value);
					productCodes.push($hiddenCode.eq(i).val());
					productNum.push(i);
				}

				// - On the checkout page where we don't have the quantity field
			} else {
				for (i = start; i < len; i += 1) {
					// DISTRELEC-25132 - add product code only once
					if (!productCodesQuantities.includes($hiddenCode.eq(i).val())) {
						productCodesQuantities.push($hiddenCode.eq(i).val());
						productCodes.push($hiddenCode.eq(i).val());
						productNum.push(i);
					}
				}
			}
			// Perform availability request cart-list
			$.ajax({
				url: '/availability',
				dataType: 'json',
				data: {
					productCodes: productCodesQuantities.join(','),
					detailInfo: true
				},
				contentType: 'application/json',
				success: function (data) {
					var items = data.availabilityData.products,
						item2,
						swissStock = 0,
						$listItem;

					$.each(productCodes, function (i) {
						var count = 0;
						var found = false;
						for (var item in items) {
							if (items[count][this.toString()] !== undefined && !found){
								item2 = items[count][this.toString()];
								found = true;
							}
							count++;
						}

						var productCode = this.toString();
						var $cartListItem = $('.js-cart-list-item-' + productCode);
						var $inputCount = $('input[name="countItems"]', $cartListItem);

						$listItem = $listItems.eq(productNum[i]);
						self.getPopover(item2, $listItem, productCode, swissStock);
						// Toggle visibility of the backorder messages in cart item depending on the stock and selected value
						Tc.Module.CartListItem.BackorderMessages($cartListItem, item2.stockLevelTotal, $inputCount.val());
					});

					$ctx.find('.loading').removeClass('loading');
				}
			});


		},

		/**
		 *
		 * @displayAvailableToOrder cart-list.js
		 *
		 * @param productCodeClass
		 */
		displayAvailableToOrder: function(productCodeClass) {
			productCodeClass.find('.inStockText').addClass('hidden');
			productCodeClass.find('.instock').addClass('hidden');
			productCodeClass.find('.leadtime').addClass('hidden');
			productCodeClass.find('.comingSoonText').removeClass('hidden');
			productCodeClass.find('.comingsoon').removeClass('hidden');
		},


		/**
		 *
		 * displayInStockText cart-list.js
		 *
		 * @param productCodeClass
		 * @param findClass
		 * @param dataText
		 * @param stockAmount
		 */
		displayTextWithStock: function(productCodeClass, findClass, dataText, stockAmount) {
			var $targetClass = productCodeClass.find(findClass);
			if ($targetClass.data(dataText) !== undefined) {
				var updatedText = $targetClass.data(dataText).replace('{0}', stockAmount);
				$targetClass.html(updatedText);
				$targetClass.removeClass('hidden');
			}
		},

		/**
		 *
		 * displayInStockText cart-list.js
		 *
		 * @param productCodeClass
		 * @param findClass
		 * @param dataText
		 * @param stockAmount
		 * @param deliveryTime
		 */
		displayTextWithStockAndDelivery: function(productCodeClass, findClass, dataText, stockAmount, deliveryTime) {
			var $targetClass = productCodeClass.find(findClass);
			if ($targetClass.data(dataText) !== undefined) {
				var updatedText = $targetClass.data(dataText).replace('{0}', stockAmount).replace('{1}', deliveryTime.split(' ')[0]);
				$targetClass.html(updatedText);
				$targetClass.removeClass('hidden');
			}
		},

		/**
		 *
		 * @getPopover cart-list.js
		 *
		 * @param item
		 * @param $listItem
		 */
		getPopover: function (item, $listItem, productCode, swissStock) {
			var self = this,
				productCodeClass = $('.info-stock-' + productCode),
				statusCode =  parseInt($('.leadTimeFlyout-'+productCode).data('status-code')),
				isSwitzerland = digitalData.page.pageInfo.shop === 'distrelec switzerland';

			var isCDC = false;
			var isCH = false;

			if (isNaN(statusCode)) {
				statusCode = 0;
			}

			$.each(item.stockLevels, function (index, stockLevel) {
				if (stockLevel.warehouseCode === '7371') {
					isCDC = true;
				}

				if (stockLevel.warehouseCode === '7374') {
					isCH = true;
				}

				if(stockLevel.isWaldom && stockLevel.mview === "BTR") {
					productCodeClass.closest('.skin-cart-list-item-cart__holder').find('.numeric .ipt').data('max-waldom-stock', stockLevel.available);
				}

				// DISTRELEC-25275: If product has no stock but is buyable or it is status 21
				if (item.stockLevelTotal === 0 || statusCode === 21) {
					self.displayAvailableToOrder(productCodeClass);
				}
				// DISTRELEC-25275: If product has no stock in any warehouse and it is not status 21
				else if (statusCode !== 21 && stockLevel.available > 0) {

					// If the webshop is CH, display the stock headline and/or additional/pick-up text
					if (isSwitzerland) {
						// CH/CDC messages visibility logic notes:
						// 7371 -> CDC
						// 7374 -> CH
						// if CH > 0 && CDC > 0 -> display "X in stock", "X additional stock available in X days" and "pickup" if available
						// if CH > 0 ONLY -> display "X in stock", hide "X additional stock available in X days"
						// if CDC > 0 ONLY -> display "X in stock", hide "X additional stock available in X days"

						// If product in stock for CH warehouse, assign value to swissStock variable
						if (stockLevel.warehouseCode === '7374') {
							swissStock = stockLevel.available;
						}
						// If product in stock for CDC Warehouse, display additional stock text
						else if (stockLevel.warehouseCode === '7371') {
							if (item.stockLevelPickup !== undefined && item.stockLevelPickup.length > 0) {
								$.each(item.stockLevelPickup, function (index, stockLevelPickup) {
									if (stockLevelPickup.stockLevel > 0) {
										self.displayTextWithStockAndDelivery(productCodeClass, '.additional', 'additional-text', stockLevel.available, stockLevel.deliveryTime);
									}
								});
							}
						}

						// If stock in present for CH warehouse, display the amount in headline
						if (swissStock !== 0) {
							self.displayTextWithStock(productCodeClass, '.inStockText', 'instock-text', swissStock);
						} else {
							self.displayTextWithStock(productCodeClass, '.inStockText', 'instock-text', stockLevel.available);
						}
					}
					// If the webshop is not CH, display only in stock headline
					else {
						self.displayTextWithStock(productCodeClass, '.inStockText', 'instock-text', stockLevel.available);
					}
				}
			});

			if (isSwitzerland) {
				// If it is ONLY CDC or ONLY CH, hide "additional" message
				if (!isCDC || !isCH) {
					productCodeClass.find('.additional').addClass('hidden');
				}

				// Iterate through pickup stocks and show them if value is larger than zero
				if (item.stockLevelPickup !== undefined && item.stockLevelPickup.length > 0) {
					$.each(item.stockLevelPickup, function (index, stockLevelPickup) {
						if (stockLevelPickup.stockLevel > 0) {
							self.displayTextWithStock(productCodeClass, '.pickup', 'pickup-text', stockLevelPickup.stockLevel);
						}
					});
				}
			}
		},

		/**
		 *
		 * @method prepareModel
		 *
		 * @param cartItem, cartListSize
		 */
		prepareModel: function (cartItem, cartListSize) {
			var model = {};

			model.code = cartItem.code;
			model.codeErpRelevant = cartItem.codeErpRelevant;
			model.entryNumber = cartListSize;
			model.productLabel = cartItem.productLabel;
			model.productUrl = cartItem.productUrl;
			model.thumbUrl = cartItem.thumbUrl;
			model.thumbUrlAlt = cartItem.thumbUrlAlt;
			model.thumbUrlWebp = cartItem.thumbUrlWebp;
			model.name = cartItem.name;
			model.typeName = cartItem.typeName;
			model.typeNameFull = cartItem.typeName;
			model.manufacturer = cartItem.manufacturer;
			model.manufacturerFull = cartItem.manufacturer;
			model.orderQuantityMinimum = cartItem.orderQuantityMinimum;
			model.orderQuantityStep = cartItem.orderQuantityStep;
			model.orderQuantityValue = cartItem.quantity;
			model.currency = cartItem.currency;
			model.mySinglePrice = "";
			model.listSinglePrice = "";
			model.mySubtotal = "";
			model.listSubtotal = "";

			return model;
		},

		/**
		 *
		 * @method onNewCartListItem
		 * Only used for events coming from Direct Order
		 * @param data
		 */
		onNewCartListItem: function (data) {

			var self = this,
				$ctx = this.$ctx,
				$list = $('.cart-list', $ctx),
				$items,
				start,
				$data,
				$last,
				height,
				lengthCartList = data.ajaxSuccessData.cartData.products.length - 1,
				backendTemplateBridge = {},
				newCartListItem = data.ajaxSuccessData.cartData.products[lengthCartList];

			backendTemplateBridge = self.prepareModel(newCartListItem, lengthCartList);
			$data = $(self.tmplCartListItem(backendTemplateBridge)).wrap('<div></div>').parent();

			$('.mod', $data).data('connectors', ["cart"]);
			self.sandbox.addModules($data);

			$list.prepend($data.children());
			$items = $list.find('.mod-cart-list-item');
			start = $items.length - 1;
			$last = $items.last();
			height = 150;
			$last
				.css({
					height: 0,
					opacity: 0
				})
				.animate({
					height: height,
					opacity: 1
				}, 500, function () {
					self.getAvailable(start);
					self.fire('activateRecalculate', {}, ['cart']);
					self.fire('checkVisible', {}, ['cart']);
				});
		},

		/**
		 *
		 * @method onUpdateCartListItem
		 * only used for events coming from cart direct order
		 * @param data
		 */
		onUpdateCartListItem: function (data) {

			var self = this,
				$ctx = this.$ctx,
				updatedEntry = data.ajaxSuccessData.updatedEntry,
				$cartListItemNumericForUpdate = $ctx.find(".hidden-entry-number[value='" + updatedEntry + "']").parent().find('.numeric .ipt');

			if (!isNaN(updatedEntry) && updatedEntry<data.ajaxSuccessData.cartData.products.length) {
				$cartListItemNumericForUpdate.val(data.ajaxSuccessData.cartData.products[updatedEntry].quantity);
			}
			self.fire('activateRecalculate', {}, ['cart']);
		},

		/**
		 *
		 * @method prepareModel
		 *
		 * @param cartItem, cartListSize
		 */
		prepareModelForRecalculate: function (cartItem) {
			var model = {};

			var productVolumePrices = cartItem.volumePrices;
			var productVolumePricesKey = 0;
			var orderEntryQuantity = cartItem.quantity;
			var productVolumePricesKeyListPrice = cartItem.price;
			var productVolumePricesKeyListPriceCurrencyIso = cartItem.currency;
			var productVolumePricesKeyListPriceValue = productVolumePrices[productVolumePricesKey];
			var productYourPriceDiffers = cartItem.yourPriceDiffers;
			var orderEntryBasePriceValue = cartItem.priceLocal;
			var totalPrice = orderEntryQuantity * productVolumePricesKeyListPriceValue;

			return model;
		},

		/**
		 *
		 * @method onWriteCalcBoxDataItem
		 *
		 * @param data
		 */
		onWriteCalcBoxDataItem: function (data) {

			var self = this,
				$ctx = this.$ctx;

			$.each(data.data, function () {

				var item = this,
                    $box = $ctx.find('#' + item.code + "-price-single, #" + item.code + "-price-total"),
                    attr;

				for (attr in item) {
					$box.find('[data-json="' + attr + '"]').html(item[attr]);
				}

				// Show / Hide Standard Price List depending on if same as customer price or not
                if (! item.showListPriceLine){
                	$box.find('.price-light').hide();
                }
	            else{
	                $box.find('.price-light').show();
                }
			});

			self.getAvailable(0);
		},

		// Cartlist add to Shopping List bulk action ***************************************************************
		onAddAllProductsToShoppingList: function (data) {
			var allProducts = this.$$('.cart-list .quote-item-checkout-row-false')

				, productCodesArray = []
				, productQuantityArray = []
				, productReferenceArray = []
				;

			allProducts.each(function (index, product) {
				var productCode = $(product).find('.hidden-product-code').val()
					, productQuantity = $(product).find('.numeric .ipt').val()
					, productReference = $(product).find('.hidden-product-reference').val()
					;

				productCodesArray[index] = productCode;
				productQuantityArray[index] = productQuantity;
				productReferenceArray[index] = productReference;

			});


			// Open Lightbox
			this.fire('checkUserLoggedIn', {productCodesArray: productCodesArray, productQuantityArray: productQuantityArray, productReferenceArray: productReferenceArray}, ['lightboxShoppinglist']);
		},

		/**
		 * @method onEmptyCart
		 */
		onEmptyCart: function () {

			var self = this,
				$ctx = this.$ctx;

			// list with cart entries
			var $cartList = $('.cart-list', $ctx);

			$cartList.css({
				overflow: 'hidden'
			}).animate({
					height: 0,
					opacity: 0
				}, 500, $.ajax({
				url: '/cart/empty-cart',
				dataType: 'json',
				method: 'post',
				success: function (data, textStatus, jqXHR) {
					// reload cart-page on success resetVoucher

                    if(window.location.href.indexOf("redeemVoucher") > -1 || window.location.href.indexOf("resetVoucher") > -1) {
                        $('.skin-metahd-item-cart .menuitem.popover-origin')[0].click();
                        window.location.href = '/cart#emptied';
                    } else {
                        window.location.href = '/cart#emptied';
                        window.location.reload();
					}

				},
				error: function (jqXHR, textStatus, errorThrown) {
					// Ajax Fail
				}
			})
			);

		}
	});
})(Tc.$);
