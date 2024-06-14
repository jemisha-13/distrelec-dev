(function ($) {

	/**
	* Module implementation.
	*
	* @namespace Tc.Module
	* @class Buying-section
	* @extends Tc.Module
	*/
	Tc.Module.CompareList = Tc.Module.extend({

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


			// connect
			this.sandbox.subscribe('compareGrid', this);
			this.sandbox.subscribe('toolItems', this);
			this.sandbox.subscribe('bundledAvailability', this);
			this.sandbox.subscribe('cart', this);


			this.$grid = this.$$('.compare-list-grid');
			this.$scroller = this.$$('.compare-list-scroll');

			this.$items = this.$$('.mod-compare-list-item .b-list-tools');// all product items in list
			this.productCodes = [];// placeholder for all product-codes in list

			this.requestProductToggleStates = $.proxy(this.requestProductToggleStates, this);
			this.requestInitialProductToggleStates = $.proxy(this.requestInitialProductToggleStates, this);
			this.onShowMoreTechnicalAttributes = $.proxy(this.onShowMoreTechnicalAttributes, this);
			
			this.tmplStockLevel = doT.template($('#tmpl-stock_level', $ctx).html());
			this.tmplStockLevelPickupHeader = $('#tmpl-stock_level_pickup_header', $ctx).html();
			this.tmplStockLevelPickupRow = doT.template($('#tmpl-stock_level_pickup_row', $ctx).html());

			this.tmplCartListItem = doT.template($('#tmpl-cart-list-item', $ctx).html());			
		},


		/**
		* Hook function to do all of your module stuff.
		*
		* @method on
		* @param {Function} callback function
		* @return void
		*/
		on: function (callback) {
			
			var tableWidth = $('.grid').css('width');
			$('.tableGrid').css('width', tableWidth);
			
			this.updateGridLayout();
			this.requestInitialProductToggleStates();

		    $(".container-topscroll").scroll(function(){
		        $(".compare-list-scroll").scrollLeft($(".container-topscroll").scrollLeft());
		    });
		    
		    $(".compare-list-scroll").scroll(function(){
		        $(".container-topscroll").scrollLeft($(".compare-list-scroll").scrollLeft());
		    });
			
		    var tableGridWidth = $('.tableGrid').css('width');
		    $('.scroll-top').css('width', tableGridWidth);
		    
			if (parseInt(tableGridWidth, 10) < 767){
				$('.scroll-top').css('display', 'none');
			}

			callback();
		},

		after: function () {
			// Update Components to have the same height as the highest one
			this.alignComponentHeight('.mod-scaled-prices');
			this.alignTableRows();
			this.alignComponentHeight('.item-number-and-type');
			this.alignComponentHeight('.mod-detail-accordion-content');
			this.alignComponentHeight('.b-valign-attr');
			this.constrainDownloads();
		},

		alignComponentHeight: function(elementHtmlClass){
			var $elementsToAlign = this.$items.find(elementHtmlClass),
				maxElementHeight = 0;

			$elementsToAlign.each(function(index, element) {
				var $element = $(element),
					elementHeight;

				// to remove possibly earlier added height if it is an adjustment after clicking show more
				// must be removed before getting the outerHeight
				$element.removeAttr('style');
				elementHeight = $element.outerHeight();

				if (elementHeight > maxElementHeight) {
					maxElementHeight = elementHeight;
				}
			});

			$elementsToAlign.css('height', maxElementHeight);
		},

		alignTableRows: function(){
			var $elementsToAlign = $("tr[class^='feature-']");
			
			for (var i=1; i< $elementsToAlign.length/2 ; i++){
				var $rowElements = $(".feature-"+i);
				var maxElementHeight = 0;
				
				for (var j=0; j< $rowElements.length ; j++){
					var $rowElement = $rowElements[j];
					var elementHeight = $rowElement.scrollHeight;
					
					if (elementHeight > maxElementHeight) {
						maxElementHeight = elementHeight;
					}					
					
				}
				$rowElements.css('height', maxElementHeight + 7);
			}

		},		
		
		
		// fired from detail accordion content attributes show more button
		onShowMoreTechnicalAttributes: function(data){
			this.alignComponentHeight('.mod-detail-accordion-content');
		},

		// if no item at all displays downloads, collapse containers
		constrainDownloads: function() {
			var $containers = this.$items.find('.download-constrain'),
				blnCollapse = true,
				blnItemHasDownloads = true;

			$.each($containers, function(i,e) {
				blnItemHasDownloads = $(e).children('.link-download-pdf').length > 0;

				if (blnItemHasDownloads) {
					blnCollapse = false;
				}
			});

			if (blnCollapse) {
				document.querySelectorAll('.items__dataSheet')[0].classList.add('hidden');
				$containers.hide();
			}
		},

		// sandbox listener
		onGridChange: function() {
			this.updateGridLayout();
			this.requestInitialProductToggleStates();// constrain code placeholder with actual list-items
		},

		// update grid (in response to add/remove items to compare):
		//	- change width and overflow
		updateGridLayout: function() {
			var self = this,
				gridWidth = self.$grid.width();
				gridLength = self.$grid.find('.mod-compare-list-item').length , // -1 because the first column is the head (common column)
				scrollWidth = gridLength * 280;// constrain with .g-item width + border (parent container adds space to fit popovers in)
			
			var tableGridWidth = $('.tableGrid').css('width');
		},

		// collect all product codes in grid (via data-id), trigger helper fnc using this array in order to request all product-tool states
		requestInitialProductToggleStates: function() {
			var self = this;
			var productCounter = 0;

			$.each(self.$items, function(index, product) {
				var code = $(product).data('id');

				if (typeof code !== 'undefined') {
					self.productCodes[productCounter] = code;
					productCounter++;
				}
			});

			if (self.productCodes.length > 0) {
				this.requestProductToggleStates(self.productCodes);
			}
		},

		// helper for checking all product tools' state,
		// param: [productCodesArray]
		requestProductToggleStates: function(productCodesArray) {
			var self = this;

			$.ajax({
				url: '/checkToggles',
				type: 'post',
				data: {
					productCodes: productCodesArray
				},
				dataType: 'json',
				success: function(data, textStatus, jqXHR) {
					self.fire('updateToolItemStates', { products: data.products }, ['toolItems']);
					self.getAvailable(0);
				},
				error: function(jqXHR, textStatus, errorThrown) {
				}
			});
		},

		// collect all product codes in grid (via data-id), trigger helper fnc using this array in order to update all availability states compare-list
		getAvailable: function (start) {
			var self = this,
				$ctx = this.$ctx,
				$listItems = $ctx.find('.mod-compare-list-item').not('.skin-compare-list-item-head'),
				$hiddenCode = $listItems.find('.hidden_product_code_compare'),
				productCodes = this.productCodes,
				productCodesQuantities = [],
				productNum = [],
				i,
				len = $hiddenCode.length;

			// Gather product data for each cart list item
			for (i = start; i < len; i += 1) {
				productNum.push(i);
			}

			// Perform availability request comparelist
			$.ajax({
				url: '/availability',
				dataType: 'json',
				data: {
					productCodes: this.productCodes.join(','),
					detailInfo: true
				},
				contentType: 'application/json',
				success: function (data) {
					var items = data.availabilityData.products,
						item,
						item2,
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
						
						$listItem = $listItems.eq(productNum[i]);
						var productCode = this.toString();
						self.getPopover(item2, $listItem, productCode);
					});

					$ctx.find('.loading').removeClass('loading');
				}
			});


		},
		
		
		
		/**
		 *
		 * @getPopover compare-list
		 *
		 * @param item
		 * @param $listItem
		 */
		getPopover: function (item, $listItem, productCode) {
			var self = this,
				stockLevelPickup = '',
				countLines = 0,
				statusCode =  parseInt($('.leadTimeFlyout-'+productCode).data('status-code'));

			if (isNaN(statusCode)) {
				statusCode = 0;
			}

			$.each(item.stockLevels, function (index, stockLevel) {

				// In Stock
				if (typeof stockLevel.deliveryTime === 'string' && stockLevel.deliveryTime.indexOf('24') === 0 && stockLevel.available > 0) {					
					// short
					var $inStock = $('.info-stock-'+productCode).find('.inStockText');
					$('.info-stock-'+productCode).find('.instock').removeClass('hidden');
					if($inStock.data('instock-text') !== undefined) {
						var inStockText = $inStock.data('instock-text').replace('{0}', stockLevel.available).replace('{1}', stockLevel.available);
						$inStock.html(inStockText);
					}
					
					// long (flyout)
					var $inStockLong = $('.leadTimeFlyout-'+productCode).find('.inStockLong');
					if($inStockLong.data('instock-text') !== undefined) {
						var inStockTextLong = $inStockLong.data('instock-text').replace('{0}', stockLevel.available);
						$inStockLong.html(inStockTextLong);
					}
				}
				
				// Further additional compare list (Only in CH and only for warehouseCode = 7371)
				var warehouseCdcCode = $('.leadTimeFlyout').data('warehouse-cdc');
				
				if(stockLevel.warehouseCode == warehouseCdcCode && stockLevel.available > 0){
					
					// short
					var $further = $('.info-stock-'+productCode).find('.furtherStockText');
					$('.info-stock-'+productCode).find('.further').removeClass('hidden');
					
					if ($further.data('further-text') !== undefined){
						var furtherText = $further.data('further-text').replace('{0}', stockLevel.available).replace('{1}', stockLevel.deliveryTime.split(' ')[0]);
						$further.html(furtherText);	
					}
					
					// long (flyout)
					var $furtherLong = $('.leadTimeFlyout-'+productCode).find('.furtherLong');
					
					if ($furtherLong.data('further-text') !== undefined){
						var furtherLongText = $furtherLong.data('further-text').replace('{0}', stockLevel.available).replace('{1}', stockLevel.deliveryTime.split(' ')[0]);
						$furtherLong.html(furtherLongText);	
						countLines++;
					}

				}				
						
			});
				
			// Pick up
			// For shops, display availability if
			// 1) there is an available quantity in _any_ warehouse, regardless of sales status, or
			// 2) sales status is < 40, regardless of available quantities
			if (item.stockLevelPickup !== undefined && item.stockLevelPickup.length > 0) {
				$.each(item.stockLevelPickup, function (index, stockLevelPickup) {
					if ((item.stockLevelTotal > 0 || statusCode < 40) && $('.info-stock-'+productCode).length) {
						var $pickUp = $('.info-stock-'+productCode).find('.pickupInStoreText');
						$('.info-stock-'+productCode).find('.pickup').removeClass('hidden');
						if($pickUp.data('pickup-text') !== undefined) {
							var pickupText = $pickUp.data('pickup-text').replace('{0}', stockLevelPickup.stockLevel).replace('{1}', stockLevelPickup.stockLevel);
							$pickUp.html(pickupText);
						}
						
						countLines++;
						
						// long (flyout) compare
						var $pickUpLong = $('.leadTimeFlyout-'+productCode).find('.pickupLong');
						var $pickupLongPopup = $('.leadTimeFlyout-'+productCode).find('.pickupLongPopup');
						if ($pickUpLong.data('pickup-long-text') !== undefined){
							var pickpupLongText = $pickUpLong.data('pickup-long-text').replace('{0}', stockLevelPickup.stockLevel);
							$pickUpLong.html(pickpupLongText);
						}
						if ($pickupLongPopup.data('pickup-long-text-popup') !== undefined){
							var pickpupLongText_ = $pickupLongPopup.data('pickup-long-text-popup').replace('{0}', stockLevelPickup.stockLevel);
							$pickupLongPopup.html(pickpupLongText_);
						}
					}	
				});
			}

			// more stock available in X weeks - tabular
			if (item.leadTimeErp !== undefined && item.leadTimeErp > 0  && statusCode < 40) {
				var $moreStockAvailable = $('.leadTimeFlyout-'+productCode).find('.moreStockAvailable');
				if($moreStockAvailable.data('morestock-text') !== undefined){
					var moreStockAvailableText = $moreStockAvailable.data('morestock-text').replace('{0}', item.leadTimeErp);
					$moreStockAvailable.html(moreStockAvailableText);
				}
			}
			
			//More in [] week(s) --> More stock available in [ ] week(s) (In CH display this when any of the above conditions equal 0 instead) tabular
			if (countLines < 3 && item.leadTimeErp !== undefined && item.leadTimeErp > 0  && statusCode < 40) {
				var $moreStockAvailablePDP = $('.info-stock-'+productCode).find('.moreStockAvailableText');
				$('.info-stock').find('.moreStockAvailable').removeClass('hidden');
				if ($moreStockAvailablePDP.data('morestockavailable-text') !== undefined) {
					var moreStockAvailableTextPDP = $moreStockAvailablePDP.data('morestockavailable-text').replace('{0}', item.leadTimeErp);
					$moreStockAvailablePDP.html(moreStockAvailableTextPDP);
				}
			}
			
			// Adjust cell height depending on number of lines displayed

			var maxHeight = 0,
				$shippingInfo = $('.mod-shipping-information'),
				$infoCell = $shippingInfo.find('.cell-availability');
			$infoCell.each(function() {
				if ($(this).height() > maxHeight) {
					maxHeight = $(this).height();
				}
			});
			$shippingInfo.height(maxHeight + 10);
			$('.gu-3.availability').height(maxHeight + 10);

		}
	});

})(Tc.$);
