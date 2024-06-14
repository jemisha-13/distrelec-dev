(function($) {

	/**
	 * Productlist Skin Favorite implementation for the module Productlist Order.
	 *
	 */
	Tc.Module.CarouselTeaser.ProductLazyLoadVertical = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {
			this.requestProductToggleStates = $.proxy(this, 'requestProductToggleStates');
			this.initializeModule = $.proxy(this, 'initializeModule');
			
			parent.on(callback);
		};
		
		
		parent.isElementVisible = function (elementToBeChecked) {
		    var TopView = $(window).scrollTop();
		    var BotView = TopView + $(window).height();
		    var TopElement = $(elementToBeChecked).offset().top;
		    var BotElement = TopElement + $(elementToBeChecked).height();
		    return ((BotElement <= BotView) && (TopElement >= TopView));		
		};
		
		
		
		parent.initializeModule = function () {
			var mod = this
				,$carouselContainer = this.$ctx.find('.carousel-teaser-container')
				,ajaxUrlPostfix = $carouselContainer.data('ajax-url')
				,maxItemsInCarousel = $carouselContainer.data('max-items-in-carousel')
				,maxItemsVisibleForComponentWidth = $carouselContainer.data('max-items-visible')
				,ffSearchChannel = $carouselContainer.data('ff-search-channel')
				,ffSearchUrl = $carouselContainer.data('ff-search-url')
				,carouselTeaserItemTemplate = doT.template(this.$$('#tmpl-carousel-teaser-item').html())
				,productUrl = Tc.Utils.splitUrl(document.URL)
				,ajaxUrl = productUrl.base+ajaxUrlPostfix		// get base url from product url, because get parameters give a 500 error
				,ajaxPayload = {}
				,ids = []
				;
			
			var dataLayer = (typeof digitalData !== 'undefined' && digitalData) ?
						((typeof digitalData.cart !== 'undefined' && digitalData.cart && typeof digitalData.cart.item !== 'undefined' && digitalData.cart.item) ? digitalData.cart.item 
								: ((typeof digitalData.product !== 'undefined' && digitalData.product) ? digitalData.product : []))
						: [];
			
				$.each(dataLayer, function (index, product) {
						ids.push(product.productInfo.productID);
				});
			if (ajaxUrlPostfix === '/recommendation') {
				ajaxUrl = ffSearchUrl;
				ajaxPayload = {'do': 'getRecommendation', channel: ffSearchChannel, format: 'json', maxResults: 6, id: ids}; // "do" is a reserved word, needs to be inside quotes
			}

			$.ajax({
				url: ajaxUrl, // /de/.../p/15413281/recommendation, '/_ui/all/data/carousel-teaser-product.json',
				type: 'GET',
				dataType: 'json',
				data: ajaxPayload,
				traditional: true,
				success: function (data, textStatus, jqXHR) {
					if((data.carouselData && data.carouselData.length) || (data.resultRecords && data.resultRecords.length)) { // carouselData from Hybris, resultRecords from FactFinder
						var numberOfItemsFromFactFinder = data.numberOfItems ? data.numberOfItems : data.resultRecords.length
							,itemsVisible = maxItemsVisibleForComponentWidth
							,carouselData = data.carouselData ? data.carouselData : data.resultRecords
						;

						// If there are less products coming from factfinder, than can be displayed, itemsVisible gets adjusted to the smaller number
						// also Nav Buttons are not visible and header has more space
						if(numberOfItemsFromFactFinder <= maxItemsVisibleForComponentWidth){
							itemsVisible = numberOfItemsFromFactFinder;
							mod.$ctx.find('.carousel-header').addClass('no-nav-buttons');
							mod.$ctx.find('.carousel-nav').addClass('hidden');
						}
						mod.$ctx.find('.carousel-teaser').data('items-visible', itemsVisible);

						// component property maxItemsToDisplay (property from cms) can override the number of items coming from factfinder
						if(maxItemsInCarousel > 0){
							numberOfItemsFromFactFinder = maxItemsInCarousel;
						}
						$.each(carouselData, function(index, product) {
							if(index < numberOfItemsFromFactFinder){
								
								if (!data.numberOfItems) {  // JSON response direct from FF, adjust data schema
									product.activePromotionLabels = [];
									var labels = JSON.parse(product.record.PromotionLabels.replace(/\{/g,'{"').replace(/:/g,'":"').replace(/,/g,'","').replace(/\}/g,'"}').replace(/""/g,'"').replace(/\}","\{/g,'},{')); // JSON response format fix
									$.each(labels, function (ix, label) {
										if (label.active==='true') {
											product.activePromotionLabels.push({});
										}
									});
									product.code = product.id;
									product.distManufacturer = {name: product.record.Manufacturer, brand_logo: {url:  product.record.ManufacturerImageUrl}};
									product.erpCode = product.id;
									product.itemPositionOneBased = index+1;
									product.name = product.record.Title;
									// render price as string with 2–4 decimals
									product.price = {};
									var prices =  product.record.Price.split('|');
									var priceData = ($('.shopsettings').data('channel-label') === 'B2C' ? prices[1] : prices[2]).split(';'); // Gross prices for B2C, net for B2B
									product.price.currency = priceData[0];
									product.price.formattedValue =  (priceData[2].split('='))[1];
									var priceParts = product.price.formattedValue.split('.');
									if (priceParts.length===1) priceParts[1] = '00';
									if (priceParts[0].length===0) priceParts[0]='0';
									if (priceParts[1].length<2) priceParts[1] += '00'.substring(priceParts[1].length);
									product.price.formattedValue = priceParts.join('.');
									if (priceParts[1].length>4) product.price.formattedValue = parseFloat(product.price.formattedValue).toFixed(4);					
									product.productImageAltText = product.name;
									product.productImageUrl = product.record.ImageURL;
									product.promotiontext = '';
									product.showCarouselItemHead = true;
									product.typeName =product.record.TypeName;
									product.url =product.record.ProductURL;
									
									if (product.record.SalesUnit !== undefined){
										product.salesUnit = product.record.SalesUnit;
									}
									
								}

								product.price.formattedValue = Tc.Utils.formatPrice(product.price.formattedValue, $('.shopsettings').data('country'));
								product.position = index;

								
								var $newTeaserItem = $(carouselTeaserItemTemplate(product));

								// markup needs to be wrapped with a div to be recognized as a module by the sandbox function
								var $module = $newTeaserItem.wrap('<div></div>').parent();
								mod.sandbox.addModules($module);

								$carouselContainer.find('.carousel-teaser').append($newTeaserItem);

								$newTeaserItem.data('position', index + 1);
							}
						});

						mod.requestProductToggleStates();

						// Lazy Load caroufredsel
						Modernizr.load([{
							load: '/_ui/all/cache/dyn-jquery-caroufredsel.min.js',
							complete: function () {

								if (mod.$ctx.find('article').length) { // Only display the module if items actually were added.
									mod.$ctx.fadeIn(function(){
										mod.$ctx.removeClass('loading');
									});
								}

								// finally create carousel
								parent.createCarousel();
								parent.getAvailabilityLazy();
							}
						}]);
					}
				},

				error: function (jqXHR, textStatus, errorThrown) {
					// Ajax Fail
				}

			});

		};

	};

})(Tc.$);

