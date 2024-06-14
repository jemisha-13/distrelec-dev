(function($) {

	/**
	 * Product Skin Shopping implementation for the module Productlist Order.
	 *
	 */
	Tc.Module.Product.Shopping = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {
			var mod = this;

			// subscribe to connector channel/s
			this.sandbox.subscribe('lightboxStatus', this);
			this.sandbox.subscribe('shoppinglist', this);
			this.sandbox.subscribe('shoppinglistBulkAction', this);
			this.sandbox.subscribe('cart', this);

			this.onSelectAllProductsCheckboxChange = $.proxy(this.onSelectAllProductsCheckboxChange, this);
			this.onSelectThisProductCheckboxChange = $.proxy(this.onSelectThisProductCheckboxChange, this);
			this.updateQuantity = $.proxy(this.updateQuantity, this);

			this.checkboxSelectProduct = this.$$('.select-product');
			this.checkboxSelectProduct.on('change', this.onSelectThisProductCheckboxChange);

			if(this.checkboxSelectProduct.is(':checked')) {
				this.checkboxSelectProduct.trigger('change', this.onSelectThisProductCheckboxChange);
			}

			this.$numeric = this.$$('.numeric');
            this.shopplingListPageCartBtn = $('.skin-layout-shopping-list .btn-cart');

			Tc.Utils.numericStepper(mod.$numeric, {
				change: function (ev) {
                    mod.checkShoppingListValid('change');
					mod.updateQuantity(ev);
				},
                error: function (ev) {
                    mod.checkShoppingListValid('error');
                },
                success: function (ev) {
                    mod.checkShoppingListValid('success');
                }
			});

			parent.on(callback);
		};

		this.checkShoppingListValid = function (_mode) {
            var mod = this,
				listEle = $(".skin-product-shopping"),
                listEleSelected = $(".skin-product-shopping.active"),
                listEleSelectedLen = listEleSelected.length,
                listEleSelection,
                listEleSelectionError,
                listEleHasError = '';

            if ( listEleSelectedLen > 0) {

                $.each(listEleSelected, function( key, _ele ) {
                    listEleSelection = $(_ele).hasClass('active');
                    listEleSelectionError = $(_ele).find('.numeric').hasClass('numeric-error');

                    if ( listEleSelection && listEleSelectionError) {
                        listEleHasError = 'error';
                    }

                });

			} else {

                $.each(listEle, function( key, _ele ) {

                    if ( $(_ele).find('.numeric').hasClass('numeric-error') ) {
                        listEleHasError = 'error';
                    }

                });

			}

			if ( listEleHasError === 'error') {
				mod.shopplingListPageCartBtn.addClass('disabled');
			} else {
				mod.shopplingListPageCartBtn.removeClass('disabled');
			}

        };

		this.onSelectAllProductsCheckboxChange = function(data){
			var checkBox = this.$$('.select-product');
			if(!this.$ctx.hasClass('paged')){
				if(data.isSelected){
					checkBox.prop('checked',true);
					checkBox.addClass('checked');
				}
				else{
					checkBox.prop('checked', false);
					checkBox.removeClass('checked');
				}
			}
		};

		this.onSelectThisProductCheckboxChange = function(ev){
            var mod = this,
				checkBox = $(ev.target)
				,checkBoxChecked = checkBox.prop('checked')
			;
            checkBox.parents('.skin-product-shopping').toggleClass('active');
			this.fire('selectSingleProductCheckboxChange', { isSelected : checkBoxChecked }, ['shoppinglist']);
			checkBox.removeClass('checked');
            mod.checkShoppingListValid('change');
		};

		this.updateQuantity =  function (ev, data) {
			var mod = this;
			var shoppinglistId = this.$$('.hidden-list-id').val()
				,productCode = this.$$('.hidden-product-code').val()
				,listEle = $(".skin-product-shopping")
				,listEleHasError = ''
			;

			$.each(listEle, function( key, _ele ) {

				if ( $(_ele).find('.numeric').hasClass('numeric-error') ) {
					listEleHasError = 'error';
				}

			});

			if ( listEleHasError !== 'error') {
				// Logic is reused from "terrific/modules/buying-section/js/buying-section.js" START
				var $productScope = mod.$numeric.closest('.js-shopping-list-item');
				var productIndex = $productScope.index();
				var currentPriceStorageString = 'currentPrice' + productIndex;
				var currentPricePerStorageString = 'currentPricePer' + productIndex;
				var inputValue = $('.ipt', $productScope).val(); // Quantity
				var arr = []; // qty breaks
				var arrTwo = []; // prices
				var pricesWithVat = [];
				var arrPricePer = []; // prices per quantity

				$(".mod-scaled-prices .body", $productScope).each(function(index, elem) {
					var itemQty = $(this)
						.find(".nth1")
						.text()
						.replace("+", "")
						.replace("\u00a0", "")
						.replace(" ", "");
					var itemPrice = $(this)
						.find(".js-pricesWithoutVat")
						.text();
					var itemPricePer = $(this)
						.find(".nth2-price-per .price-per")
						.text();

					var itemPriceWithVat = $(this).find(".js-pricesWithVat").text();

					arr.push(itemQty);
					arrTwo.push(itemPrice);
					pricesWithVat.push(itemPriceWithVat);
					arrPricePer.push(itemPricePer);
				});

				arr.join(",");
				arrTwo.join(",");
				arrPricePer.join(",");
				pricesWithVat.join(",");


				var price = localStorage.getItem(currentPriceStorageString); // Storing Price using local storage
				var pricePer = localStorage.getItem(currentPricePerStorageString); // Storing Price using local storage
				var currentQuantity = parseInt(inputValue);

				$.each(arr, function(i) {
					var found = false;
					var breakPoint = parseInt(arr[i]);

					if (currentQuantity === breakPoint) {
						var GetPriceGreen = arrTwo[i].match(/[\d\.]+/)[0];

						if ( arrPricePer[i].length > 0) {
							var GetPricePer = arrPricePer[i].match(/[\d\.]+/)[0];
						}

						found = true;

						setTimeout(function() {
							$(".skin-scaled-prices-single .price.js-withoutVat .odometer-price", $productScope).html(arrTwo[i]);
							$(".skin-scaled-prices-single .price.js-withVat .odometer-price", $productScope).html(pricesWithVat[i]);

							$(".skin-scaled-prices-single .price-per--quantity", $productScope).html(
								'<span class="odometer-priceper green-price">' + arrPricePer[i] + "</span>"
							);
							localStorage.setItem(currentPriceStorageString, GetPriceGreen);
							localStorage.setItem(currentPricePerStorageString, GetPricePer);
						}, 3.0);
					} else if (currentQuantity < breakPoint) {
						var redClass = "",
							redClassPer = "";
						GetPriceRed = arrTwo[i - 1].match(/[\d\.]+/)[0];
						if ( arrPricePer[i - 1].length > 0) {
							GetPricePerRed = arrPricePer[i - 1].match(/[\d\.]+/)[0];

							if (pricePer !== GetPricePerRed) {
								redClassPer = " red-priceper";
							}

							localStorage.setItem(currentPricePerStorageString, GetPricePerRed);
						}

						found = true; // If condition is fulfilled exit loop

						if (currentQuantity === breakPoint - 1 && price !== GetPriceRed) {
							redClass = " red-price";
						}

						localStorage.setItem(currentPriceStorageString, GetPriceRed);

						setTimeout(function() {
							$(".skin-scaled-prices-single .price.js-withoutVat .odometer-price", $productScope).html(arrTwo[i - 1]);
							$(".skin-scaled-prices-single .price.js-withVat .odometer-price", $productScope).html(pricesWithVat[i - 1]);

							$(".skin-scaled-prices-single .price-per--quantity", $productScope).html(
								'<span class="odometer-priceper '+redClassPer+'">' + arrPricePer[i-1] + "</span>"
							);
						}, 3.0);
					} else if (i === arr.length - 1 && currentQuantity > breakPoint) {
						localStorage.setItem(currentPriceStorageString, arrTwo[i].match(/[\d\.]+/)[0]);

						if ( arrPricePer[i].length > 0) {
							localStorage.setItem(currentPricePerStorageString, arrPricePer[i].match(/[\d\.]+/)[0]);
						}

						setTimeout(function() {
							$(".skin-scaled-prices-single .price.js-withoutVat .odometer-price", $productScope).html(arrTwo[i]);
							$(".skin-scaled-prices-single .price.js-withVat .odometer-price", $productScope).html(pricesWithVat[i]);

							$(".skin-scaled-prices-single .price-per--quantity", $productScope).html(
								'<span class="odometer-priceper green-priceper">' + arrPricePer[i] + "</span>"
							);
						}, 3.0);
					}

					if (found) {
						return false;
					}
				});
				// Logic is reused from "terrific/modules/buying-section/js/buying-section.js" STOP

				this.fire('activateRecalculate', shoppinglistId, ['cart']);
			} else {
				$('.skin-cart-recalculatelayer-shopping').hide();
			}

			$.ajax({
				url: '/shopping/update/entry',
				type: 'post',
				data: {
					listId: shoppinglistId,
					productCode: productCode,
					desired: this.$numeric.find('.ipt').val()
				},
				success: function (data, textStatus, jqXHR) {
				},
				error: function (jqXHR, textStatus, errorThrown) {
				}
			});

		};
	};

})(Tc.$);
