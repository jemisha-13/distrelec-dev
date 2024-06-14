(function ($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Productlist
	 * @extends Tc.Module
	 */
	Tc.Module.Productlist = Tc.Module.extend({

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
			this.sandbox.subscribe('campaign', this);
			this.sandbox.subscribe('productlist', this);
			this.sandbox.subscribe('shoppinglistBulkAction', this);
			this.sandbox.subscribe('toolItems', this);
			this.sandbox.subscribe('facetActions', this);
			this.sandbox.subscribe('lightboxStatus', this);

			this.$products = this.$$('.mod-product', $ctx);

			this.availTmplDots = $.availableBar.tmplDots;
			this.availTmplTime = $.availableBar.tmplTime;

			this.availState = $.availableBar.states;

			this.onPaginationChange = $.proxy(this, 'onPaginationChange');
			this.onProductListSwitch = $.proxy(this, 'onProductListSwitch');
			this.onLoadProductsForFacetSearch = $.proxy(this, 'onLoadProductsForFacetSearch');
			this.onShowReloadLayer = $.proxy(this, 'onShowReloadLayer');
			this.loadProductsCallback = $.proxy(this, 'loadProductsCallback');
			this.showProductListLoadingOverlay = $.proxy(this, 'showProductListLoadingOverlay');
			this.hideProductListLoadingOverlay = $.proxy(this, 'hideProductListLoadingOverlay');
			this.requestProductToggleStates = $.proxy(this, 'requestProductToggleStates');
			this.requestInitialProductAvailabilityStates = $.proxy(this, 'requestInitialProductAvailabilityStates');
			this.requestProductAvailabilityStates = $.proxy(this, 'requestProductAvailabilityStates');
			this.requestInitialProductToggleStates = $.proxy(this, 'requestInitialProductToggleStates');
			this.updateBrowserUrl = $.proxy(this, 'updateBrowserUrl');
			this.updateReloadLayer = $.proxy(this, 'updateReloadLayer');
			this.generateNewUrl = $.proxy(this, 'generateNewUrl');
			this.onFilteredItemClick = $.proxy(this, 'onFilteredItemClick');
            this.onPlpScroll = $.proxy(this, 'onPlpScroll');
            
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function (callback) {

			this.requestInitialProductAvailabilityStates();
			this.requestInitialProductToggleStates();
			this.updateReloadLayer();

			var productCodesArray = [];
			iconsVisible = this.$$('.ico');

			$.each(iconsVisible, function (index, icon) {
				productCodesArray[index] = $(icon).data('product-code');
			});

			this.requestProductToggleStates(productCodesArray);

            window.onscroll =  function() {

                var plpFilterBar = $("#plp-filter-action-bar");
                var plpFilterList = document.getElementById("plp-filter-product-list"),
					sticky =  0,
                    ScrollTop = document.body.scrollTop;

                if (plpFilterList) {
					if ($('html').hasClass('isie7')) {
						sticky = plpFilterList.offsetTop + plpFilterList.parentNode.offsetTop + plpFilterList.parentNode.parentNode.offsetTop;
					} else {
						sticky = plpFilterList.offsetTop - 200;
					}
				}

                if (ScrollTop === 0) {

                    if (window.pageYOffset) {
                        ScrollTop = window.pageYOffset;
					} else {
                        ScrollTop = (document.body.parentElement) ? document.body.parentElement.scrollTop : 0;
					}

                }

            };

			callback();
		},
		
		detectIE:function () {
			  var ua = window.navigator.userAgent;
			  
			  var msie = ua.indexOf('MSIE ');
			  if (msie > 0) {
			    // IE 10 or older => return version number
			    return parseInt(ua.substring(msie + 5, ua.indexOf('.', msie)), 10);
			  }

			  var trident = ua.indexOf('Trident/');
			  if (trident > 0) {
			    // IE 11 => return version number
			    var rv = ua.indexOf('rv:');
			    return parseInt(ua.substring(rv + 3, ua.indexOf('.', rv)), 10);
			  }

			  var edge = ua.indexOf('Edge/');
			  if (edge > 0) {
			    // IE 12 => return version number
			    return parseInt(ua.substring(edge + 5, ua.indexOf('.', edge)), 10);
			  }

				// other browser
			  return false;
		},

		onPaginationChange: function (data) {

			var  mod = this
				,urlDynamic = this.$$('.productlist').data('current-query-url')
				,urlDocument = decodeURIComponent(document.URL)
				,urlAppendix = "/showmore"
				,urlAppendixTechnical = "/search"
				,page = data.page
				,pageSize = data.pageSize
				,$backenddata = $('#backenddata')
				,useTechnicalView = $('.shopsettings', $backenddata).data('use-technical-view')
				,method = 'GET'
			;

			mod.showProductListLoadingOverlay();

			// if there is a dynamic url from ajax facet feature, we take it over the document url (IE8/IE9 do not support pushstate)
			var url = urlDocument;
			if(urlDynamic !== undefined && urlDynamic !== ''){
				url = urlDynamic;
			}

			if (url.indexOf('?') === -1 ) { // Current page loaded with POST request, add parameters
				var $facetsData = $('.xmod-facets .other-facets').data(); 
				url += '?' + $facetsData.nonFilterParameters + $facetsData.allFilterParameters;
			}
			if (url.length > 1900) method = 'POST';

				//check if any selected manufacturer contains & and replace with encoded value in url string

				var filterQuery = "";

				$('.facet-item .active').each(function(i,v){

					if($(v).attr('title').indexOf('&') !== -1) {
						filterQuery = (document.referrer === "" ? $(v).parent().data("facet-value-name").replace("%20","+") : $(v).parent().data("facet-value-name").replace("&","%26"));
					}

				});

			if(filterQuery !== "") {
                filterQuery = filterQuery.replace("%26","&").replace("%20","+").replace(" ","+");
				filterQueryFormatted = filterQuery.replace("&", "%26").replace("%20","+").replace(" ","+");
				url = url.replace(filterQuery, filterQueryFormatted);
			}

			var urlObject = Tc.Utils.splitUrl(url);

			var sort = mod.getUrlParameter(urlDocument, 'sort');

			// if there were no get params in the url before
			if(urlObject.get === undefined){
				urlObject.get = {};
			}

			if (urlObject.get._dummy !== undefined){
				delete urlObject.get._dummy; // Used for resetting filter, we don't need it here
			}			
			if (urlObject.get.xF !== undefined){
				delete urlObject.get.xF; // Used for expanding selected facet, we don't need it here
			}

			urlObject.get.page = page;

			//Do not add pageSize parameter if pageSize=10(default value) for category search
			if(url.indexOf("/c/") !== -1)
			{
				if(pageSize !== 10) {
					urlObject.get.pageSize = pageSize;
				}
			} else {
				urlObject.get.pageSize = pageSize;
			}	
				
			
			if (sort !== undefined){
				urlObject.get.sort = sort;
			}		

			Tc.Utils.postForm(urlObject.base,urlObject.get,method,true);
			

		},

		
		getUrlParameter: function(url, sParam) {
		    var sPageURL = url,
		        sURLVariables = sPageURL.split('&'),
		        sParameterName,
		        i;

		    for (i = 0; i < sURLVariables.length; i++) {
		        sParameterName = sURLVariables[i].split('=');

		        if (sParameterName[0] === sParam) {
		            return sParameterName[1] === undefined ? true : sParameterName[1];
		        }
		    }
		},		

		onProductListSwitch: function(data){
			var mod = this,
				method = 'GET';
			this.showProductListLoadingOverlay();

			// if there was an url stored on the switch button module (facet ajax functionality), we take it over the document url
			var ajaxUrl = document.URL;
			if(data.currentUrl !== undefined && data.currentUrl !== ''){
				ajaxUrl = data.currentUrl;
			}
			
			if (ajaxUrl.indexOf('?') === -1 ) { // Current page loaded with POST request, add parameters
				var $facetsData = $('.xmod-facets .other-facets').data(); 
				ajaxUrl += '?' + $facetsData.nonFilterParameters + $facetsData.allFilterParameters;
			}
			if (ajaxUrl.length > 1900) method = 'POST';
			
			var urlObject = Tc.Utils.splitUrl(ajaxUrl);
			
			// if there were no get params in the url before
			if(urlObject.get === undefined){
				urlObject.get = {};
			}
			
			//emtpy the object
			if (urlObject.get.useTechnicalView !== undefined){
				delete urlObject.get.useTechnicalView;
			}
			if (urlObject.get.useListView !== undefined){
				delete urlObject.get.useListView;
			}		
			if (urlObject.get.useIconView !== undefined){
				delete urlObject.get.useIconView;
			}			
			if (urlObject.get._dummy !== undefined){
				delete urlObject.get._dummy; // Used for resetting filter, we don't need it here
			}			
			if (urlObject.get.xF !== undefined){
				delete urlObject.get.xF; // Used for expanding selected facet, we don't need it here
			}			
			
			
			var sort = mod.getUrlParameter(ajaxUrl, 'sort');
			if (sort !== undefined){
				urlObject.get.sort = sort; 
			}			
			
			
			//filling the object
			if(data.useTechnicalView){
				urlObject.get.useTechnicalView = true;
			}
			if(data.useStandardView){
				urlObject.get.useTechnicalView = false;
			}
			if(data.useIconView){
				urlObject.get.useIconView = true;
			}

			Tc.Utils.postForm(urlObject.base,urlObject.get,method,true);
			
			
			
		},

		// event is coming from facets module
		onLoadProductsForFacetSearch: function (data) {
			var  mod = this
				,urlLoadProductsForFacet = data.facetUrl //"/_ui/all/data/load-products-for-facet.json"
				,$backenddata = $('#backenddata')
				,useTechnicalView = $('.shopsettings', $backenddata).data('use-technical-view')
				,language = $('.shopsettings', $backenddata).data('language')
				,pageSize = this.$$('.productlist').data('page-size')
			;

			if(data.isReloadLayerUpdate === undefined) {
				this.showProductListLoadingOverlay();
			}

			var urlObject = Tc.Utils.splitUrl(urlLoadProductsForFacet);

			// if there were no get params in the url before
			if(urlObject.get === undefined){
				urlObject.get = {};
			}

			urlObject.get.pageSize = pageSize;

			
			var isUseListView = mod.getParameterByName('useListView');   
			var isUseTechnicalView = mod.getParameterByName('useTechnicalView');
			var isUseIconView = mod.getParameterByName('useIconView');
			
			if (isUseListView !== null){
				urlObject.get.useTechnicalView = false;
			}
			
			if (isUseTechnicalView !== null){
				urlObject.get.useTechnicalView = true;
			}
			
			if (isUseIconView !== null){
				urlObject.get.useIconView = true;
			}	
			
			var currentQueryUrl = Tc.Utils.joinUrl(urlObject);

			$.ajax({
				url: currentQueryUrl,
				type: 'GET',
				dataType: 'json',
				data:{
					requestType: 'ajax',
					useTechnicalView: useTechnicalView
				},
				success: function (responseData, textStatus, jqXHR) {
					var status = false;

					if(responseData.productsCount > 0){
						mod.$$('.productlist .list .mod-product:not(.skin-product-template)').remove();
						mod.loadProductsCallback(responseData, useTechnicalView);

						if(responseData.factFinderCampaign.feedbackCampaigns.length !== 0 || responseData.factFinderCampaign.advisorCampaigns.length !== 0) {

							if(responseData.factFinderCampaign.feedbackCampaigns.length > 0) {
								$.each(responseData.factFinderCampaign.feedbackCampaigns, function(index, item) {

									if(item.feedbackTextTop) {
										mod.fire('feedbackTextTopChange', {feedbackTextTop: item.feedbackTextTop, change: true}, ['campaign']);
									}
									if(item.pushedProductsList) {
										mod.fire('carouselChange', {factFinderCampaign: item.pushedProductsList}, ['campaign']);
									} else {
										mod.fire('hideCarousel', ['campaign']);
									}
								});
							} else {
								mod.fire('feedbackTextTopChange', {feedbackTextTop: '', change: false}, ['campaign']); // no new campaign, removes existing one
							}

							if(responseData.factFinderCampaign.advisorCampaigns.length > 0) {
								$.each(responseData.factFinderCampaign.advisorCampaigns, function(index, item) {
									mod.fire('advisorCampaignChange', {advisorCampaign: item}, ['campaign']);
								});
							} else {
								mod.fire('hideAdvisorCampaignChange', ['campaign']);
							}

						} else {
							// no new campaigns, removes existing ones
							mod.fire('feedbackTextTopChange', {feedbackTextTop: '', change: false}, ['campaign']);
							mod.fire('hideAdvisorCampaignChange', ['campaign']);
						}

						// page parameter needs to be set again since it can change depending on facet parameters
						var ajaxHistoryEntry = "facets";
						mod.generateNewUrl(currentQueryUrl, responseData.pagination.currentPage, pageSize, ajaxHistoryEntry, data.isPopstateEvent);

						mod.fire('paginationChangeCallback', {pagination: responseData.pagination}, ['productlist']);

						status = true;
					}
					// when the last filter to show any products was removed, we reroute to the category page
					else if(responseData.control.pageReload){
						location.href = data.facetUrl;
					}
					else{
						mod.fire('error', {
							title: data.errorTitle,
							boxTitle: "",
							boxMessage: data.errorMessage
						}, ['lightboxStatus']);
					}

					mod.fire('loadProductsForFacetSearchCallback', {
						status: status,
						productsCount: responseData.productsCount,
						productsCountCatalogPlus: responseData.productsCountCatalogPlus,
						currentQueryUrl: data.facetUrl,
						filters: responseData.filters,
						facetGroups: responseData.facetGroups,
						categoriesCount: responseData.categories.length,
						categories: responseData.categories,
						lazyFacetsCount: responseData.lazyFacets.length,
						lazyFacets: responseData.lazyFacets,
						isPopstateEvent: data.isPopstateEvent,
						clickedFacet: data.clickedFacet,
						clickedFacetGroup: data.clickedFacetGroup
					}, ['facetActions']);
				},
				error: function (jqXHR, textStatus, errorThrown) {
					var status = false;

					mod.fire('error', {
						title: data.errorTitle,
						boxTitle: "",
						boxMessage: data.errorMessage
					}, ['lightboxStatus']);

					mod.fire('loadProductsForFacetSearchCallback', {
						status: status,
						clickedFacet: data.clickedFacet
					}, ['facetActions']);
				},
				complete: function(jqXHR, textStatus, errorThrown){

					mod.hideProductListLoadingOverlay();
				}
			});
		},

		
		getParameterByName: function(name, url) {
		    if (!url) url = window.location.href;
		    name = name.replace(/[\[\]]/g, "\\$&");
		    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
		        results = regex.exec(url);
		    if (!results) return null;
		    if (!results[2]) return '';
		    return decodeURIComponent(results[2].replace(/\+/g, " "));
		},		
		
		
		updateReloadLayer: function() {
			var mod = this
				,currentQueryUrl = mod.$$('.productlist').data('current-query-url');

			mod.$$('.js-update-result').on('click', function(e) {
				e.preventDefault();

				mod.fire('loadProductsForFacetSearchReloadLayerData', {currentQueryUrl: currentQueryUrl}, ['facetActions']);
			});
		},

		loadProductsCallback: function(data, useTechnicalView){
			var mod = this
				,newProducts = []
				,productCodesArray = [];
		
			
			if ($('.tabular-search-table').length === 0 ){
			
				if (useTechnicalView ){
					var templateStandard = doT.template(this.$$('#tmpl-product-list-item').html());
					var templateTechnical = doT.template(this.$$('#tmpl-product-list-item-technical').html());
				
					$.each(data.products, function(index, item) {

                        item.nameURIEncoded = encodeURI(item.name);
                        item.manufacturerURIEncoded = encodeURI(item.manufacturer);
                        item.typeNameURIEncoded = encodeURI(item.typeName);
                        item.position = index;

						var template = null;
						if(useTechnicalView && !item.eol && item.buyable){
							template = templateTechnical;
						}
						else{
							template = templateStandard;
						}
						var $newProduct = $(template(item)).css("display","none");
		
						// markup needs to be wrapped with a div to be recognized as a module by the sandbox function
						var $module = $newProduct.wrap('<div></div>').parent();
						mod.sandbox.addModules($module);
		
						mod.$$('.productlist .list', mod.$ctx).append($newProduct);
						$newProduct.fadeIn();
						newProducts[index] = $newProduct;
						productCodesArray[index] = $newProduct.find('.hidden-product-code').val();
					});
					
				}
				else{
					location.reload();
				}
				
				
				// Make ajax calls for availability and toggle states
				mod.requestProductAvailabilityStates(productCodesArray, newProducts);
				mod.requestProductToggleStates (productCodesArray);
			}
			else{
				location.reload();
			}
		},

		// product list overlay is used for loading products for selected facet
		showProductListLoadingOverlay: function(){

			this.$$('.ajax-product-loader:not(.js-apply-facets)').css('display', 'block');

			var self = this,
				$ctx = this.$ctx,
				$overlay = $('.mod-productlist .message-wrapper'),
				loadingMessage = $overlay.find('.loading-message'),
				vHeight = $.waypoints('viewportHeight'),
				oOffset = $overlay.offset(),
				messageOffset = loadingMessage.offset();

			if (messageOffset.top < oOffset.top) {
				loadingMessage.addClass('stickToTop');
			}

			if (oOffset.top > vHeight) {
				loadingMessage.addClass('stickToTop');
			}

			// Waypoint for the Top of the Overlay
			$overlay.waypoint(function (direction) {

				if (direction === 'up') {
					loadingMessage
						.addClass('stickToTop')
						.removeClass('stickToBottom');
				} else {
					loadingMessage
						.removeClass('stickToTop');
				}
			}, {
				offset: function () {
					var sightCorrection = 60; // Sight Correction is a trial and error value to make fine adjustment
					return (vHeight / 2) - sightCorrection;
				}
			});


			// Waypoint for the Bottom of the Overlay
			$overlay.waypoint(function (direction) {

				if (direction === 'down') {
					loadingMessage
						.addClass('stickToBottom')
						.removeClass('stickToTop');
				} else {
					loadingMessage
						.removeClass('stickToBottom');
				}
			}, {
				offset: function () {
					var sightCorrection = 84; // Sight Correction is a trial and error value to make fine adjustment
					return (vHeight / 2) - $(this).height() + loadingMessage.height() + sightCorrection;
				}
			});

			loadingMessage.css('visibility', 'visible');
		},

		// reload layer is used for loading products for multiple selected facet
		onShowReloadLayer: function(){

			this.$$('.ajax-product-loader.js-apply-facets').css('display', 'block');

			var self = this,
				$ctx = this.$ctx,
				$overlay = $('.message-wrapper.js-apply-facets', $ctx),
				loadingMessage = $overlay.find('.loading-message'),
				vHeight = $.waypoints('viewportHeight'),
				oOffset = $overlay.offset(),
				messageOffset = loadingMessage.offset();

			if (messageOffset.top < oOffset.top) {
				loadingMessage.addClass('stickToTop');
			}

			if (oOffset.top > vHeight) {
				loadingMessage.addClass('stickToTop');
			}

			// Waypoint for the Top of the Overlay
			$overlay.waypoint(function (direction) {

				if (direction === 'up') {
					loadingMessage
						.addClass('stickToTop')
						.removeClass('stickToBottom');
				} else {
					loadingMessage
						.removeClass('stickToTop');
				}
			}, {
				offset: function () {
					var sightCorrection = 60; // Sight Correction is a trial and error value to make fine adjustment
					return (vHeight / 2) - sightCorrection;
				}
			});


			// Waypoint for the Bottom of the Overlay
			$overlay.waypoint(function (direction) {

				if (direction === 'down') {
					loadingMessage
						.addClass('stickToBottom')
						.removeClass('stickToTop');
				} else {
					loadingMessage
						.removeClass('stickToBottom');
				}
			}, {
				offset: function () {
					var sightCorrection = 84; // Sight Correction is a trial and error value to make fine adjustment
					return (vHeight / 2) - $(this).height() + loadingMessage.height() + sightCorrection;
				}
			});

			loadingMessage.css('visibility', 'visible');
		},

		hideProductListLoadingOverlay: function(){
				this.$$('.message-wrapper .loading-message').css('visibility', 'hidden');
				this.$$('.ajax-product-loader').css('display', 'none');
		},

		requestInitialProductAvailabilityStates: function () {
			// paged class is set on shopping and favorite list pages for hidden products
			var allProductsVisible = this.$$('.list .mod-product:not(.paged,.skin-product-template)')
				, $isBomToolReviewPage = $('body').hasClass('skin-layout-bom-tool-review') ? true : false
				, productCodesArray = []
				, mod = this
				;

			$.each(allProductsVisible, function (index, product) {
				productCodesArray[index] = $(product).find('.hidden-product-code').val();
			});

            if ( $isBomToolReviewPage ) {
                productCodesArray.length = 0;
                var bomProducts = $('.skin-product-bom');

                $.each(bomProducts, function (index, product) {
                    productCodesArray[index] = $(product).find('.hidden-product-code').val();
                });

            }

			if(productCodesArray.length > 0){
				this.requestProductAvailabilityStates(productCodesArray, allProductsVisible);
			}



		},

		requestProductAvailabilityStates: function (productCodesArray, newProducts) {
			var mod = this,
				details = $('.skin-productlist-bom').length !== 0;

			$.ajax({
				url: '/availability',
				dataType: 'json',
				data: {
					productCodes: productCodesArray.join(','),
					detailInfo: details
				},
				contentType: 'application/json',
				success: function (data) {
					var items = data.availabilityData.products,
						item,
						item2,
						$listItem,
						$product;

					if (details) {
						var $hiddenCode = newProducts.find('.hidden-product-code'),
							productCodes = [],
							productCodesQuantities = [],
							productNum = [],
							len = $hiddenCode.length,
                            $isBomToolReviewPage = $('body').hasClass('skin-layout-bom-tool-review') ? true : false;

						for (var i = 0; i < len; i++) {
							productCodesQuantities.push($hiddenCode.eq(i).val() + ';' + $('input:text.ipt')[i].value);
							productCodes.push($hiddenCode.eq(i).val());
							productNum.push(i);
						}

                        if ( $isBomToolReviewPage ) {
                            productCodes.length = 0;
                            productCodes = productCodesArray;
                        }

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
	
							$listItem = newProducts.eq(productNum[i]);
							
							var productCode = this.toString();
							mod.getPopover(item2, $listItem, productCode);
						});
					} else {
						// loop items, apply availability data to product template
						for (var x = 0, l = items.length; x < l; x++) {
							$product = $(newProducts[x]);
	
							for (var y = 0; y < items.length; y++){
								if (items[y][$product.find('.hidden-product-code').val()] !== undefined){
									item = items[y][$product.find('.hidden-product-code').val()];
								}
							}
							
							// add check for undefined, since some OCI Customer got an error in IE, DISTRELEC-5667
							if(item !== undefined){
								$product.find('.available-bar').append(mod.availTmplTime(item));
								$product.find('.product-stock').html(item.stockLevelTotal);
								$product.find('.list-attribs').removeClass('loading');
							}
						}

					}
				}
			});
		},

        getPopover: function(item, $listItem, productCode){

        var self = this,
            stockLevelPickup = '',
            countLines = 0,
            $infoStock = $('.info-stock-'+productCode),
            $plpProductItem = $('.plp-filter-products__product.productCode-'+productCode),
            statusCode = parseInt($('.leadTimeFlyout-'+productCode).data('status-code'));

        if (isNaN(statusCode)) {
            statusCode = 0;
        }

        if ( item === undefined ) {
            return true;
        }


        $.each(item.stockLevels, function (index, stockLevel) {

            // In Stock
			if (stockLevel.available > 0) {
				// short
				var $inStock = $infoStock.find('.inStockText');

				if ($inStock.data('instock-text') !== undefined) {
					var inStockText = $inStock.data('instock-text').replace('{0}', stockLevel.available);
					$inStock.html(inStockText);
				}

				// long (flyout)
				var $inStockLong = $('.leadTimeFlyout-' + productCode).find('.inStockLong');
				if ($inStockLong.data('instock-text') !== undefined) {
					var inStockTextLong = $inStockLong.data('instock-text').replace('{0}', stockLevel.available);
					$inStockLong.html(inStockTextLong);
				}

				if ( stockLevel.available > 0 ) {
					$infoStock.find('.instock').removeClass('hidden');
					$infoStock.find('.availableToOrder').addClass('hidden');
					$infoStock.find('.sales-status--status-text-40-45').addClass('hidden');
				} else {
					$infoStock.find('.availableToOrder').removeClass('hidden');
				}

				if ( (statusCode >= 40 && statusCode <= 45) || (statusCode === 10 || statusCode === 60 || statusCode === 90 || statusCode === 91 || statusCode === 0) ) {
					$infoStock.find('.availableToOrder').addClass('hidden');
					$infoStock.find('.sales-status--status-text-40-45').removeClass('hidden');

					if (item.stockLevelTotal > 0) {
						$infoStock.find('.sales-status--status-text-40-45 .instock-41-45').removeClass('hidden');
					}
					else {
						$infoStock.find('.sales-status--status-text-40-45 .outofstock-41-45').removeClass('hidden');
					}

				}

				countLines++;
			}

            // Further additional product list (Only in CH and only for warehouseCode = 7371) tabular
            var warehouseCdcCode = $('.leadTimeFlyout').data('warehouse-cdc');

            // further Stock
			if (stockLevel.available > 0) {

				// short
				var $further = $('.info-stock-'+productCode).find('.further');
				$('.info-stock-'+productCode).find('.further').removeClass('hidden');

				if ($further.data('further-text') !== undefined) {
					var furtherText = $further.data('further-text').replace('{0}', stockLevel.available).replace('{1}', stockLevel.deliveryTime.split(' ')[0]);
					$further.append("<div class='further-text'>"+furtherText+"</div>");
				}

				// long (flyout)
				var $furtherLong = $('.leadTimeFlyout-' + productCode).find('.furtherLong');

				if ($furtherLong.data('further-text') !== undefined) {
					var furtherLongText = $furtherLong.data('further-text').replace('{0}', stockLevel.available).replace('{1}', stockLevel.deliveryTime.split(' ')[0]);
					$furtherLong.html(furtherLongText);
					countLines++;
				}

			}

            // more stock available
			// more stock available in X weeks - tabular
			if (stockLevel.leadTime !== undefined && stockLevel.leadTime > 0 && statusCode < 40) {
				var $moreStockAvailable = $('.leadTimeFlyout-' + productCode).find('.moreStockAvailable');

				if ($moreStockAvailable.data('morestock-text') !== undefined) {
					var moreStockAvailableText = $moreStockAvailable.data('morestock-text').replace('{0}', stockLevel.leadTime);
					$moreStockAvailable.html(moreStockAvailableText);
				}

			}

			//More in [] week(s) --> More stock available in [ ] week(s) (In CH display this when any of the above conditions equal 0 instead) tabular
			if (countLines < 3 && stockLevel.leadTime !== undefined && stockLevel.leadTime > 0 && statusCode < 40) {
				var $moreStockAvailablePDP = $infoStock.find('.moreStockAvailableText');
				$('.info-stock').find('.moreStockAvailable').removeClass('hidden');

				if ($moreStockAvailablePDP.data('morestockavailable-text') !== undefined) {
					var moreStockAvailableTextPDP = $moreStockAvailablePDP.data('morestockavailable-text').replace('{0}', stockLevel.leadTime);
					$moreStockAvailablePDP.html(moreStockAvailableTextPDP);
				}

			}

        });

        // Pick up
        // For shops, display availability if
        // 1) there is an available quantity in _any_ warehouse, regardless of sales status, or
        // 2) sales status is < 40, regardless of available quantities
        if (item.stockLevelPickup !== undefined && item.stockLevelPickup.length > 0) {
            $.each(item.stockLevelPickup, function (index, stockLevelPickup) {
                if (item.stockLevelTotal > 0 || statusCode < 40) {
                    var $pickUp = $infoStock.find('.pickupInStoreText');
                    $infoStock.find('.pickup').removeClass('hidden');
                    if($pickUp.data('pickup-text') !== undefined) {
                        var pickupText = $pickUp.data('pickup-text').replace('{0}', stockLevelPickup.stockLevel);
                        $pickUp.html(pickupText);
                    }

                    countLines++;

                    // long (flyout) tabular
                    var $pickUpLong = $('.leadTimeFlyout-'+productCode).find('.pickupLong');
                    if ($pickUpLong.data('pickup-long-text') !== undefined){
                        var pickpupLongText = $pickUpLong.data('pickup-long-text').replace('{0}', stockLevelPickup.stockLevel);
                        $pickUpLong.html(pickpupLongText);
                    }
                }
            });
        }



    },

		requestInitialProductToggleStates: function () {
			// paged class is set on shopping and favorite list pages for hidden products
			var allProductsVisible = this.$$('.list .mod-product:not(.paged)')
				, productCodesArray = []
				, mod = this
				;
			
			if (allProductsVisible.length === 0){
				// we are in tabular view
				allProductsVisible = this.$$('.list .prices-cart-column');
			} 

			$.each(allProductsVisible, function (index, product) {
				productCodesArray[index] = $(product).find('.hidden-product-code').val();
			});
			if(productCodesArray.length > 0){
				this.requestProductToggleStates(productCodesArray);
			}
		},

		requestProductToggleStates: function (productCodesArray) {
			var mod = this;
			
			if (productCodesArray[0] !== undefined){

				$.ajax({
					url: '/checkToggles',
					type: 'post',
					data: {
						productCodes: productCodesArray
					},
					success: function (data, textStatus, jqXHR) {
						mod.fire('updateToolItemStates', { products: data.products }, ['toolItems']);
					},
					error: function (jqXHR, textStatus, errorThrown) {
					}
				});
			
			}
		},

		updateBrowserUrl: function (data) {
			var newUrl = data.currentQueryUrl
				, title = "" // currently ignored by Firefox
				, isIE = false
				, ieVersion = 11
				;

			if (navigator.appName == 'Microsoft Internet Explorer' ||  !!(navigator.userAgent.match(/Trident/) || navigator.userAgent.match(/rv 11/)) || $.browser.msie == 1){
				isIE = true;
				ieVersion = this.detectIE();
			}
			
		    // only pushState if it was not a popstate event and user went back in browser history
			if(!data.isPopstateEvent && ieVersion > 8){
				//DISTRELEC-7386
				window.history.pushState(data.historyStateObject, title, newUrl);
			}

		},

		generateNewUrl: function (urlToModify, page, pageSize, ajaxHistoryEntry, isPopstateEvent){
			var urlObject = Tc.Utils.splitUrl(urlToModify);

			if(urlObject.get === undefined){
				urlObject.get = {};
			}
			urlObject.get.page = page;
			urlObject.get.pageSize = pageSize;
			var currentQueryUrl = Tc.Utils.joinUrl(urlObject);

			this.$$('.productlist').data('current-query-url', currentQueryUrl);

			if (!$('html').hasClass('lt-ie10') && !Modernizr.iseproc) {

				// state object is getting serialized on popState event firing on back/forward button click
				var stateObj = { isAjaxFacetHistoryEntry : false, isAjaxPaginationHistoryEntry: false };

				if(ajaxHistoryEntry === "facets"){
					stateObj.isAjaxFacetHistoryEntry = true;
				} else if (ajaxHistoryEntry === "pagination"){
					stateObj.isAjaxPaginationHistoryEntry = true;
				}

				this.updateBrowserUrl({isPopstateEvent: isPopstateEvent, currentQueryUrl: currentQueryUrl, historyStateObject: stateObj });
			}
		},
		
		onFilteredItemClick: function(ev) {
			var $a = $(ev.target).closest('a');
			if ($a.attr('href') !== undefined && $a.attr('href').length > 1900) { // For very long URLs, delete filter values
				ev.preventDefault();
				var urlObject = Tc.Utils.splitUrl($a.attr('href'));
				for (var object in urlObject.get) {
					if (object.indexOf('filter_') === 0) {
						delete urlObject.get[object];
					}
				}
				
				Tc.Utils.postForm(urlObject.base,urlObject.get,'GET',true);
			}
		}
		
	});

})(Tc.$);
