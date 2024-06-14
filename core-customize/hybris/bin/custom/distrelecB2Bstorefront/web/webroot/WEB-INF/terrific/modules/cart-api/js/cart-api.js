(function ($) {

	// - Dev Snippet -
	//   Trigger "add" via Console:
	//   $(document).trigger('cart', { actionIdentifier: 'actionIdentifier', type: 'add', productCode: '3010006', quantity: 8});
	//   If quantity is 0, backend will determine the appropriate quantity according to minumum order quantity

	window.cart = null;


	var _path = {
		add: '/cart/add', // ?productCodePost=3010006&qty=4
		addBulk: '/cart/add/bulk', // bulk add to cart
		update: '/cart/update', // ?entryNumber=0&quantity=0
		remove: '/cart/remove', // ?productCodePost=0
		addQuote: '/cart/addquotation', // ?quotationId=0020033439&productsJson=[{"itemNumber": "000010","productCode":"29999999","quantity":40}]
		removeQuote: '/cart/removequotation', // ?quotationId=20000456
		recalculate: '/cart/recalculate', // recalculate layer on cart page
		getCart: '/cart/json' // no params
	};

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Cart-api
	 * @extends Tc.Module
	 */
	Tc.Module.CartApi = Tc.Module.extend({

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
			this.sandbox.subscribe('lightboxStatus', this);
			this.sandbox.subscribe('cart', this);

			this.onCartDone = $.proxy(this.onCartDone, this);
			this.onCartFail = $.proxy(this.onCartFail, this);
			
			// array to store and cancel ongoing ajax calls
			this.jXhrPool = [];
			this.jXhrPoolId = 0;

			this.onGet();
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function (callback) {
			$(document).on('cart', $.proxy(function (ev, data) {

				switch (data.type) {
					case 'add':
						this.onAdd(data);
						break;
					case 'addBulk':
						this.onAddBulk(data);
						break;
					case 'remove':
						this.onRemove(data);
						break;
					case 'addQuote':
						this.onAddQuote(data);
						break;
					case 'removeQuote':
						this.onRemoveQuote(data);
						break;
					case 'update':
						this.onUpdate(data);
						break;
					case 'recalculate':
						this.onRecalculate(data);
						break;
					case 'get':
						this.onGet(data);
						break;

					default:
						throw('event type is not matched:' + data.type);
				}
			}, this));

			callback();
		},

		onAdd: function (data) {
			var postData = {
				productCodePost: data.productCodePost, qty: data.qty , origPos: data.origPos , q: data.queryParam, trackQuery: data.trackQuery, pos: data.pos,prodprice: data.prodprice, origPageSize: data.origPageSize, isProductFamily: data.productFamily,campaign: data.productCampaign,filterapplied: data.filterApplied
			};

			window.dataLayer.push({'ecommerce': null});  // Clear the previous ecommerce objects, if present
			window.dataLayer.push({
				event: 'addToCart',
				ecommerce: {
					currencyCode: window.digitalData.page.pageInfo.currency,
					add: {
						actionField: {list: window.digitalData.page.pageCategory.pageType },
						products: [{
							id: data.productCodePost,
							quantity: data.qty
						}]
					}
				}
			});
			if(data.queryParam !== ''){
				postData.q = data.queryParam;
			}
			this.submit(_path.add, postData, data);
		},

		onAddBulk: function (data) {
			this.submit(_path.addBulk, {
				productsJson: data.productsJson
			}, data);
		},

		onRemove: function (data) {
			this.submit(_path.remove, {
				entryNumber: data.entryNumber
			}, data);
		},

		onAddQuote: function (data) {
			this.submit(_path.addQuote, {
				quotationId: data.quoteId,
				productsJson: data.productsJson
			}, data);
		},

		onRemoveQuote: function (data) {
			this.submit(_path.removeQuote, {
				quotationId: data.quoteId
			}, data);
		},

		onUpdate: function (data) {
			this.submit(_path.update, {
				entryNumber: data.entryNumber, quantity: data.quantity
			}, data);
		},

		onRecalculate: function (data) {
			this.submit(_path.recalculate, {}, data);
		},

		onGet: function (data) {
			this.getCart(_path.getCart, data);
		},

		/* Client-Server Stuff */
		submit: function (path, data, evData) {
			if (evData.actionIdentifier === 'toolsitemAddToCart'){
			path=path+"?pageType="+evData.actionIdentifier;	
			}
			var self = this,
				$isBomtool = ( $('.mod-bom-toolbar .btn-add-cart').length > 0 ) ? true : false,
                $btnBomAddCart = $('.mod-bom-toolbar .btn-add-cart'),
                $btnBomViewCart = $('.mod-bom-toolbar .btn-view-cart');
			var currentXhrId = self.jXhrPoolId++;
			$.ajax({
				url: path, dataType: 'json',
				method: 'post',
				data: data,
				beforeSend: function( jqXHR ) {
					// Stop all previous ongoing ajax calls with same item
					if (data.entryNumber) {
						var ixToDelete=[];
						$.each(self.jXhrPool, function(index, jqXHR) {
							if (typeof jqXHR !== 'undefined' && jqXHR[1] === data.entryNumber && jqXHR[0] !== null && jqXHR.readyState !== 4) {
								jqXHR[0].abort();
								ixToDelete.push(index);
							}
						});

						// Remove aborted ajax calls from the pool
						$.each(ixToDelete, function(index, ix) {
							self.jXhrPool[ix] = undefined;
						});
						
						// Add new ajax call to the pool
						self.jXhrPool[currentXhrId] = [jqXHR,data.entryNumber];
					}
				},
				success: function (data, textStatus, jqXHR) {
					self.onCartDone(data, textStatus, jqXHR, evData);
					if (evData.actionIdentifier === 'directOrderComponent' && evData.type === 'addBulk' || evData.type === 'addQuote' || evData.actionIdentifier === 'removeItemFromCart'){
						var errMsg = $('#modalStatus').css('display');
						if ( errMsg !== 'block') {
                            window.location.href = '/cart';
						}
					}

                    var add2Cart = (null === digitalData || digitalData.addToCart === undefined) ? {} : JSON.parse(JSON.stringify(digitalData.addToCart));
                    var products = (null === add2Cart || add2Cart.product === undefined) ? [] : JSON.parse(JSON.stringify(digitalData.addToCart.product));
                    $.each($(".quickorder--field "), function () {
                        var product = {};
                        var productCode = $(this).find("input[name^=directOrder]").val();

                        if (null !== productCode && productCode !== undefined && productCode !== "") {
                            product.productId = productCode;
                            product.quantity = $(this).find('input[name^=quantityField]').val();
                            products.push(product);
                        }

                    });

                    add2Cart.method = "homepage direct order";
                    add2Cart.product = products;
                    digitalData.addToCart = add2Cart;

                    $('.quickorder input').val('');
                    $('.quickorder__quantity').addClass('disabled');

                    if ( $isBomtool ) {

                    	var addedProducts = data.cartData.products
							, $btnSaveAllProductToShoppingList = $('.mod-bom-toolbar .btn-add-shopping')
							, $btnSaveAllProductToCartList = $('.mod-bom-toolbar .btn-add-cart')
							, $allProductSelection = $('.bom-product-controllbar__select-all input');

                        $.each(addedProducts, function (index,item) {
                            var productId = item.code,
                            	$bomProduct = $('.bom-product--'+productId);

                            $bomProduct.addClass('added-to-cart');
                            $btnBomAddCart.removeClass('active');
                            $bomProduct.parents('.skin-product-bom').removeClass('active');
                        });

                        $btnBomViewCart.removeClass('hidden');

                        $btnSaveAllProductToShoppingList.attr('disabled', 'disabled');
                        $btnSaveAllProductToCartList.attr('disabled', 'disabled');
                        $allProductSelection.prop( "checked", false );
                        $allProductSelection.parent().removeClass('active');
					}

                    if (typeof Bootstrapper !== 'undefined') {
						Bootstrapper.ensEvent.trigger("homepage add to cart");
					}
                },
				error: function (jqXHR, textStatus, errorThrown) {
					self.onCartFail(jqXHR, textStatus, errorThrown, evData);
				}
			});
		},

		getCart: function (path, evData) {
			var self = this;

			// Use Cache: false for IE8, see DISTRELEC-2233

			$.ajax({
				url: path, cache: false, dataType: 'json', method: 'get', success: function (data, textStatus, jqXHR) {
					self.onCartDone(data, textStatus, jqXHR, evData);
				}, error: function (jqXHR, textStatus, errorThrown) {
					self.onCartFail(jqXHR, textStatus, errorThrown, evData);
				}
			});
		},

		onCartDone: function (data, textStatus, jqXHR, evData) {

			if (!data.cartData) {
				return;
			}

			// Global _cart data variable
			window._cart = data.cartData;

			var productCount = 0;

			// Some errors happened during add to cart
			if (data.errorData) {
				this.onCartFail(jqXHR, textStatus, data.errorData.msg, evData);

				// if event came from directOrder, do not show the error lightbox and fire event instead
				if(data.errorData.isMinQuantityOrStepError){
					$(document).trigger('cartChange', {
						actionIdentifier: evData.actionIdentifier,
						type: 'directOrderQuantityError',
						quantity: productCount,
						ajaxData: data
					});
				}
				else if(data.errorData.isUnknownProductIdentifierError){
					$(document).trigger('cartChange', {
						actionIdentifier: evData.actionIdentifier,
						type: 'directOrderProductIdentifierError',
						quantity: productCount,
						ajaxData: data
					});
				}
				else{
					var boxMessage = "";

					// Add to cart single Product
					if(data.errorData.msg !== ""){
						boxMessage += data.errorData.msg + "<br/>";
					}

					// Add to Cart bulk action
					if(data.errorData.errorProducts.length > 0){
						$.each(data.errorData.errorProducts, function(index, errorProduct){
							boxMessage += errorProduct.errorMessage;

							//we dont need the item code part on quick order homepage

							if(!$('body').hasClass('skin-layout-home') && $('.mod-cart-directorder')) {
								boxMessage += " (";
								$.each(errorProduct.products, function(index, errorProductCode){
									if(index > 0){
										boxMessage += ", ";
									}
									boxMessage += errorProductCode;
								});
								boxMessage += ")<br/>";
							}

						});
					}

					// Show Lightbox with product specific errors
					if (data.errorData.msgType=='info') {
						this.fire('info', {
							title:data.errorData.msgTitle,
							boxTitle: ' ',
							boxMessage: boxMessage
						}, ['lightboxStatus']);
					} else {
						this.fire('error', {
							title: data.errorData.msgTitle,
							boxTitle: data.errorData.msgTitle,
							boxMessage: boxMessage
						}, ['lightboxStatus']);
					}
				}
			}

			// normal add to cart action
			if(data.cartData.addedQuantity > 0){
				productCount = 1;
			}
			// Cart Update Case in cart with numeric stepper, quantity can be pos or negative
			else if(data.cartData.updateQuantity > 0 || data.cartData.updateQuantity < 0){
				productCount = 1;
			}
			// add to cart bulk action only
			else if(data.cartData.addedProductsCount > 0){
				productCount = data.cartData.addedProductsCount;
			}

			// trigger cartChange Event if evData is not undefined and there was actually a chart change (update/add)
			if(evData !== undefined && (productCount > 0)) {

				// updated a product which was already in the cart
				if(parseInt(data.updatedEntry) >= 0){
					evData && $(document).trigger('cartChange', {
						actionIdentifier: evData.actionIdentifier,
						type: 'update',
						quantity: productCount,
						ajaxSuccessData: data
					});
				}

				// new item was added to the cart
				else{
					evData && $(document).trigger('cartChange', {
						actionIdentifier: evData.actionIdentifier,
						type: 'add',
						quantity: productCount,
						ajaxSuccessData: data
					});
				}

				//DISTRELEC-9525 Update price calc box
				if($('.skin-layout-checkout').length > 0){
					$('.mod-cart-pricecalcbox .calc-subtotal').fadeTo('fast', 0.5).fadeTo('fast', 1.0);
					$('.mod-cart-pricecalcbox .calc-subtotal').html(window._cart.subTotal);
					
					$('.mod-cart-pricecalcbox .calc-delivery').fadeTo('fast', 0.5).fadeTo('fast', 1.0);
					$('.mod-cart-pricecalcbox .calc-delivery').html(window._cart.deliveryCost.formattedValue);
					
					$('.mod-cart-pricecalcbox .calc-tax').fadeTo('fast', 0.5).fadeTo('fast', 1.0);
					$('.mod-cart-pricecalcbox .calc-tax').html(window._cart.tax);
					
					$('.mod-cart-pricecalcbox .calc-total').fadeTo('fast', 0.5).fadeTo('fast', 1.0);
					$('.mod-cart-pricecalcbox .calc-total').html(window._cart.totalPrice);
				}

			}

			// Remove product or quote from cart
			if(evData !== undefined && (evData.actionIdentifier === 'removeItemFromCart')){
				evData && $(document).trigger('cartChange', {
					actionIdentifier: evData.actionIdentifier,
					type: 'remove',
					quantity: evData.qty,
					ajaxSuccessData: data
				});
			}

			// Recalculate Layer Update
			if(evData !== undefined && evData.actionIdentifier === 'recalculateLayer'){
				evData && $(document).trigger('cartChange', {
					actionIdentifier: evData.actionIdentifier,
					type: 'update',
					quantity: 0,
					ajaxSuccessData: data
				});
			}

			// if not it was just our initial getCart()
			if (evData === undefined) {
				$(document).trigger('cartDataAvailable');
			}
		},

		onCartFail: function (jqXHR, textStatus, errorThrown, evData) {
			evData && evData.fail && evData.fail(textStatus, errorThrown);
		},

		after: function() {

        }

	});

})(Tc.$);
