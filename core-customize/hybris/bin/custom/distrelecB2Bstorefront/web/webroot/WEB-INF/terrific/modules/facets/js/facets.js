(function ($) {

	/**
	 * Facet implementation.
	 *
	 * For filtering products by categories and filters.
	 * - Add class 'is-expanded' to a .facet-group to make a facet open on page load
	 * - Unfortunately 'is-expanded' is also used for the show more link state
	 *
	 * @namespace Tc.Module
	 * @class Facets
	 * @extends Tc.Module
	 */
	Tc.Module.Facets = Tc.Module.extend({

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

			// Subscribe to channels
			this.sandbox.subscribe('lightboxStatus', this);
			this.sandbox.subscribe('facetActions', this);
			this.sandbox.subscribe('windowHistoryEvents', this);
			
			this.bindAdditionalFacetChangeEventListener = $.proxy(this, 'bindAdditionalFacetChangeEventListener');

			this.onFacetItemClick = $.proxy(this, 'onFacetItemClick');
			this.onLoadProductsForFacetSearchReloadLayerData = $.proxy(this, 'onLoadProductsForFacetSearchReloadLayerData');
			this.onLoadProductsForFacetSearchCallback = $.proxy(this, 'onLoadProductsForFacetSearchCallback');
			this.onSliderInputValueChange = $.proxy(this, 'onSliderInputValueChange');
			this.onFacetsWindowsHistoryPopStateEvent = $.proxy(this, 'onFacetsWindowsHistoryPopStateEvent');
			this.generateActiveFilters = $.proxy(this, 'generateActiveFilters');
			this.generateFacetGroups = $.proxy(this, 'generateFacetGroups');
			this.generateAdditionalFacetGroupsDropDownOptions = $.proxy(this, 'generateAdditionalFacetGroupsDropDownOptions');

			this.loadProductsForFacetErrorTitle = this.$$('.xmod-filter .bd').data('load-products-error-title');
			this.loadProductsForFacetErrorMessage = this.$$('.xmod-filter .bd').data('load-products-error-message');

			// timer used for slider keyup timeout
			this.timer = null;

			// used for reloadLayer onFacetItemClick
			var $backenddata = $('#backenddata');
			this.autoApplyFilter = false; // no auto-apply with facets redesign [DISTRELEC-7036]
			this.useTechnicalView = $('.shopsettings', $backenddata).data('useTechnicalView');
			
			this.reloadLayerFilterstring = {};
			this.elementsInFilterBox = [];
			this.filtersToRemove = [];
			
			// Used for shift-click
			this.lastClickedFacetNumber = 0;
			this.lastClickedFacetName = '';
			this.lastClickedFacetRange = [];
			this.shiftReleased = true;

			
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function (callback) {

			var self = this;

			// Initializes Facets (toggling, bindings..)
			this.initFacets();

			// Initialize Sliders - Lazy Load jQuery UI Slider
			Modernizr.load([
				{
					load: '/_ui/all/cache/dyn-jquery-ui-slider.min.js',
					complete: function () {
						self.initSliders();
					}
				}
			]);
			
			// only load if there are actually additional facets
			if (this.$$('.select-additional-facets').length > 0) {
				// Initializes the additional facets dropdown
				this.initAdditionalFacetDropdown();
				this.stringBundle = this.i18n.addBundleLiteralById('additional-facet-strings', 'additionalFacet', this.$ctx);
			}
			
			// Check for released Shift key (for shift-click selection of facet values).
			self.shiftGear();
			
			callback();
		},
		
		/**
		 * Hook function to trigger your events.
		 *
		 * @method after
		 * @return void
		 */
		after: function () {
			$('#currencyMeta').attr('content', $('.shopsettings').data('currency'));
			$('#lowpriceMeta').attr('content', $('.slider').data('curr-min'));

			// initialize addWindowPopstateEvent
			this.fire('addWindowPopstateEvent', ['windowHistoryEvents']);
		},

		//
		// Init

		initFacets: function () { // parse facet information in here
			var self = this,
				$showHideToggleList = this.$$('.other-facets'),
                $PlpFilterSearchFacetList = this.$$('.plp-filter-search-facets .other-facets'),
            	$PlpFilterControllbar = this.$$('.plp-filter-controllbar'),
                $showHideFiltersBtn = $('.productlistpage__show-hide-filter'),
				$showFiltersText = $showHideFiltersBtn.data('show-text'),
                $hideFiltersText = $showHideFiltersBtn.data('hide-text');


			// Bind open / close facets
			$showHideToggleList.on('click', '.show-more-link', function (e) {
				self.showHideFacetValues(e);
			});

            $showHideToggleList.on('click', '.facet-view__link', function (e) {
                self.slidePlpSearchFacet(e);
            });
			
			$showHideToggleList.on('click', '.btn-close', function (e) {
				self.hideFacetValues(e);
			});
			
			// Bind Reset button			
			$showHideToggleList.on('click', '.btn-reset', function (e) {
				self.clearFacetSelection(e);
			});

            $PlpFilterSearchFacetList.on('click', '.facet-reset', function (e) {
                self.clearPlpSearchCurrentFacetCSelection(e);
            });


			// Bind Apply filter button

            $PlpFilterControllbar.on('click', '.plp-filter-controllbar__apply-filter', function (e,b) { // b = boolean to indicate if triggered by clicking on a closed facet
                self.plpApplyFilter(e,b);
            });

			// Bind min-max value selections
			$showHideToggleList.on('change', '.select-min-value, .select-max-value', function (e) {
				self.updateMinMaxValues(e);
			});

			// add click listener to facet items
			this.$$('.other-facets').on('click', '.facet-group .facet-item .facet-item__anchor', self.onFacetItemClick);

			var expandedFacet = decodeURIComponent(this._getUrlParam($showHideToggleList.data('non-filter-parameters'),'xF'));
			$showHideToggleList.find('.facet-group.type-checkbox').each(function() {
				if ($(this).data('facet-group-name')===expandedFacet) {
					$(this).find('a.show-more-link').click();
					return;
				}
			});

			var	$btnInfo = self.$$('.btn-info'),
				$toolTipHover =  self.$$('.tooltip-hover');

			$btnInfo.on('click',
				function(ev) {
					ev.preventDefault();
					targetPos = $(ev.target).offset();
					$toolTipHover.toggleClass('hidden').offset({left:targetPos.left-183,top:targetPos.top-$toolTipHover.height()-14});
				}).on('mouseleave', function(ev) {
					$toolTipHover.addClass('hidden');
					this.blur();
				});


			var plpFilterSearchFacetLen = $('#plp-filter-search-facets .facet-group').length,
				plpFilterSingleInput = $('.plp-filter-search-facets__search-wrapper__input'),
			    plpFilterSingleSearchIcon = $('.plp-filter-search-facets__search-wrapper__icon'),
				$plpFaecetSerch = $('#plp-filter-search-facets'),
                $plpFaecetSerchForIe7n8 = $('.plp-filter-search-facets'),
                $plpFacetViewMoreLink = $('.facet-view__more-scroll-link'),
				$plpFacetViewMoreLinkWrapper = $('.facet-view__more-scroll-wrapper'),
                $plpFacetSerchMoreLinkWrapper = $('.xmod-facets__more-scroll-wrapper'),
				$plpFacetSerchMoreLinkWrapperLeft = $('.xmod-facets__more-scroll-wrapper-left'),
            	$plpFacetSerchMoreLinkWrapperForIe7n8 = $('.xmod-facets__more-scroll-wrapper.for-ie7-ie8');

			for (var i=1;i < plpFilterSearchFacetLen;i++) {

				if($('#plp-search-facet-'+i).length) {
					var leftPos = $('#plp-search-facet-'+i).position().left;
					$('#plp-search-facet-'+i).attr('data-leftpos', leftPos);
				}

			}

			if ( $plpFaecetSerch.get(0).scrollHeight > $plpFaecetSerch.get(0).offsetHeight ) {
                $plpFacetViewMoreLinkWrapper.removeClass('hidden');
			}

            if ( $plpFaecetSerch.get(0).scrollWidth < $plpFaecetSerch.get(0).offsetWidth ) {
                $plpFacetSerchMoreLinkWrapper.addClass('hidden');
            }

            if ( $('html').hasClass('isie7') || $('html').hasClass('isie8') ) {
                if ($plpFaecetSerchForIe7n8.get(0).scrollWidth < $plpFaecetSerchForIe7n8.get(0).offsetWidth) {
                    $plpFacetSerchMoreLinkWrapperForIe7n8.addClass('hidden');
                }

            }

            $plpFacetViewMoreLink.on('click', function (ev) {
                ev.preventDefault();

                var self = $(this),
                	$scrollTopPos = parseInt( $(".facet-view").scrollTop() + 50 );

                $(".facet-view").stop().animate({
					scrollTop: $scrollTopPos
				},  function() {
                });

            });

            $plpFaecetSerch.scroll(function(){
				if($(this).scrollTop() + $(this).innerHeight() >= $(this)[0].scrollHeight) {
                    $plpFacetViewMoreLinkWrapper.addClass('hidden');
				} else {
                    $plpFacetViewMoreLinkWrapper.removeClass('hidden');
				}
			});

            $plpFacetSerchMoreLinkWrapper.on('click', function (ev) {
                ev.preventDefault();

                var self = $(this),
                    $scrollToElement = $("#plp-filter-search-facets");

                if ( $('html').hasClass('isie7') || $('html').hasClass('isie8') ) {
                    $scrollToElement = $( ".plp-filter-search-facets" );
                }
                $scrollLeftPos = parseInt( $scrollToElement.scrollLeft() + ($("#plp-search-facet-1").outerWidth() * 4));

                $scrollToElement.stop().animate({
                    scrollLeft: $scrollLeftPos
                },  function() {
                });

            });

			$plpFacetSerchMoreLinkWrapperLeft.on('click', function (ev) {
                ev.preventDefault();

                var self = $(this),
                    $scrollToElement = $("#plp-filter-search-facets");

                if ( $('html').hasClass('isie7') || $('html').hasClass('isie8') ) {
                    $scrollToElement = $( ".plp-filter-search-facets" );
                }

                $scrollLeftPos = parseInt( $scrollToElement.scrollLeft() - ($("#plp-search-facet-1").outerWidth() * 4));

                $scrollToElement.stop().animate({
                    scrollLeft: $scrollLeftPos
                },  function() {
                });

            });

            $plpFaecetSerch.scroll(function(){

                if($(this).scrollLeft() + $(this).innerWidth() >= $(this)[0].scrollWidth) {
                    $plpFacetSerchMoreLinkWrapper.addClass('hidden');
                } else {
					$plpFacetSerchMoreLinkWrapper.removeClass('hidden');
                } 

				if($(this).scrollLeft() > 0) {
					$plpFacetSerchMoreLinkWrapperLeft.removeClass('hidden');
				}else {
					$plpFacetSerchMoreLinkWrapperLeft.addClass('hidden');
				}
            });

            $plpFaecetSerchForIe7n8.scroll(function(){
                if($(this).scrollLeft() + $(this).innerWidth() >= $(this)[0].scrollWidth) {
                    $plpFacetSerchMoreLinkWrapperForIe7n8.addClass('hidden');
                } else {
                    $plpFacetSerchMoreLinkWrapperForIe7n8.removeClass('hidden');
                }
            });

            // plp-filter-search-facets__search feature
            plpFilterSingleInput.on('keyup', function(ev){
                ev.preventDefault();

                var clickedFacet = $(ev.target).closest('.plp-filter-search-facets__content'),
                    facetList = clickedFacet.find('.facet-value-list'),
                    searchTerm = $(this).val().toLowerCase(),
                    searchTermClearIcon = clickedFacet.find('.plp-filter-search-facets__clear-icon');

	
                if (searchTerm.length > 0) {
                    searchTermClearIcon.removeClass('hidden');
				} else {
                    searchTermClearIcon.addClass('hidden');
				}

                facetList.each(function() {

                    var facetItem = $(this).find('.facet-item');

                    facetItem.each(function() {

                        var facet = $(this).find('.facet-item__anchor__name');
                        var currentText = facet.data('value-facet');

						if(!isNaN(currentText)){
							currentText=currentText.toString();
						}

                        if (currentText !== undefined) {
                            currentText = currentText.toUpperCase();
                            searchTerm = searchTerm.toUpperCase();

                            if (currentText.indexOf(searchTerm) >= 0) {
                                $(this).removeClass('hidden');
                            } else {
                                $(this).addClass('hidden');
                            }
                        }

                    });

                });

            });

            plpFilterSingleInput.on('focus', function(ev){
                ev.preventDefault();

                var clickedFacet = $(ev.target).closest('.plp-filter-search-facets__content'),
                    searchTermSearchIcon = clickedFacet.find(plpFilterSingleSearchIcon);

                searchTermSearchIcon.addClass('active');

            });

            plpFilterSingleInput.on('blur', function(ev){
                ev.preventDefault();

                var clickedFacet = $(ev.target).closest('.plp-filter-search-facets__content'),
                    searchInput = clickedFacet.find(plpFilterSingleInput),
                    searchTerm = searchInput.val().toLowerCase(),
                    searchTermSearchIcon = clickedFacet.find(plpFilterSingleSearchIcon);

                if (searchTerm.length < 1) {
                    searchTermSearchIcon.removeClass('active');
				}

            });

            $('.plp-filter-search-facets__clear-icon').on('click', function(ev){
                ev.preventDefault();

                var clickedFacet = $(ev.target).closest('.plp-filter-search-facets__content'),
                    searchInput = clickedFacet.find(plpFilterSingleInput),
                    searchTermSearchIcon = clickedFacet.find(plpFilterSingleSearchIcon);
                    searchFacetItems = clickedFacet.find('.facet-item');

                $(this).addClass('hidden');
                searchInput.val('');
                searchFacetItems.removeClass('hidden');
                searchTermSearchIcon.removeClass('active');
            });

            $('.plp-filter-controllbar__toggle-filter').on('click', function(ev){

                $("html, body").stop().animate({
                    scrollTop: $(".productlistpage").offset().top - 100
                }, 50);

            });

			$('.plp-filter-controllbar__onoffswitch-checkbox').on('click', function(ev){

				var $target = $(ev.target);

				if($target.prop("checked") === true){
					self.setPlpProductListDetailView(true);
					if (typeof(Storage) !== "undefined") {
						localStorage.setItem("PlpProductListDetailView", "true");
					}

				} else {
					self.setPlpProductListDetailView(false);
					if (typeof(Storage) !== "undefined") {
						localStorage.setItem("PlpProductListDetailView", "false");
					}
				}

			});


            $('.mobile-header__close').on('click', function(){
                $('.skin-facets-plp-filter-search').removeClass('skin-facets-plp-filter-search--mobile');
                $('.productlistpage__filter-search').removeClass('productlistpage__filter-search--mobile');
                $('.productlistpage__filter-action-bar').removeClass('mobile');
                $('.facet-group__title').removeClass('facet-group__title--active');
                $(".facet-group__list").removeClass('facet-group__list--active');
                $('body').removeClass('mobile-category-filter');
			});

            self.checkPlpSearchCurrentFacetCSelection(plpFilterSearchFacetLen);

            if (typeof(Storage) !== "undefined") {
                var plpFliterViewSearch = localStorage.getItem('PlpFliterViewSearch');

                if ( plpFliterViewSearch === null || plpFliterViewSearch === undefined ) {
                    plpFliterViewSearch = 'true';
				}

                if ( plpFliterViewSearch == 'true') {
                    $('.productlistpage__show-hide-filter').html($hideFiltersText);
                    $('.productlistpage__filter-view-search').removeClass('hide');
                } else {
                    $('.productlistpage__filter-view-search').addClass('hide');
                    $('.productlistpage__show-hide-filter').html($showFiltersText);
                }

            }

            self.setFilterShowHideStatus();

            $('.plp-filter-controllbar__apply-filter').addClass('disabled').attr('disabled', 'disabled');

            if ( $('html').hasClass('isie7') || $('html').hasClass('isie8') ) {

            	var faceltListLen = $('#plp-filter-search-facets > li').length
					,faceltListItemWidth = $('#plp-filter-search-facets > li').width()
					,faceltListWidth = faceltListLen * faceltListItemWidth;

                $('#plp-filter-search-facets').width(faceltListWidth + 60);

			}

            var $appliedFilterItem = $('.plp-filter-controllbar__applied-filter-item'),
				$resetAllFilterItem = $('.plp-filter-controllbar__reset-all-filter'),
				$pageSize,
                $plpfaceltListLen = $('#plp-filter-search-facets > li').length,
                $plpfaceltListType = $('#plp-filter-search-facets > li').hasClass('type-slider');

            if ( $plpfaceltListType && $plpfaceltListLen === 1 ) {
                $('.skin-productlist-filters').addClass('hidden');
            }

            if ( window.location.href.indexOf("pageSize=") > 0 ) {
                $pageSize = window.location.href.split("pageSize=")[1].split('&')[0];
            }

            if ( $appliedFilterItem.length > 0) {

                $.each($appliedFilterItem, function (index, item) {
                    var newHref = $(item).find('.facet-link').attr('href');

                    if ( $pageSize !== undefined ) {
                        newHref += "&pageSize=" + $pageSize;
                    }
                    if ( $(item).find('.facet-link').attr('href').indexOf("pageSize=") === -1 ) {
                        $(item).find('.facet-link').attr('href',newHref);
                    }
                });

                if ( $resetAllFilterItem.attr('href').indexOf("pageSize=") === -1 ) {
                    var newHrefReset = $resetAllFilterItem.attr('href');
                    if ( $pageSize !== undefined ) {
                        newHrefReset += "&pageSize=" + $pageSize;
                    }
                    $resetAllFilterItem.attr('href',newHrefReset);
                }

            }


        },

		setFilterShowHideStatus: function () {

            var $appliedFilterLength = $('.plp-filter-controllbar__applied-filter-item').length,
                $isFlitersHide = $('.productlistpage__filter-view-search').hasClass('hide'),
                $isFliterControlBar = $('.productlistpage__filter-action-bar');

            if ( $appliedFilterLength < 1 && $isFlitersHide) {
                $isFliterControlBar.addClass('hidden');
            } else {
                $isFliterControlBar.removeClass('hidden');
			}

        },

		initAdditionalFacetDropdown: function () {
			var self = this
				, $select = this.$ctx.find('#select-additional-facets')
				;

			// Lazy Load SelectBoxIt Dropdown
			if (!Modernizr.touch && ! Modernizr.isie7  && ! Modernizr.iseproc) {
				Modernizr.load([
					{
						load: '/_ui/all/cache/dyn-jquery-selectboxit.min.js',
						complete: function () {
							$select.selectBoxIt({
								autoWidth: false,
								defaultText: $select.data('pretext'),
								dynamicPositioning: false
							});

						}
					}
				]);
			}

			// initialize change listener anyway, no matter if its touch or not
			$select.on('change.selectFacetOption', self.bindAdditionalFacetChangeEventListener);
		},

		initSliders: function () {

			var self = this
				, $sliders = this.$$('.slider')
				;

			if ($sliders.length !== 0) {

				$.each($sliders, function (index, slider) {

					var $slider = $(slider)
						, values = [ parseFloat($slider.data('absMin')), parseFloat($slider.data('absMax')) ]
						;
					$slider.slider({
						animate: 'fast',
						min: values[0],
						max: values[1],
						step: 0.1,
						range: true,
						values: values,
						create: function (event, ui) {
							self.onSliderCreate(event, ui); 
						},
						slide: function (event, ui) {

							// Calculation of min Range needs to be up here to work correctly, cannot be down in event handler
							var sliderWidthInPixel = $slider.width()
								, minimumRangeWidthInPixel = 2 * $slider.find('.ui-slider-handle').width()/2 // Minimum Range = 2 * (Handle Width / 2) = Handle Width
								, sliderMin = parseFloat($slider.data('abs-min'))
								, sliderMax = parseFloat($slider.data('abs-max'))
								, sliderCurrMin = parseFloat($slider.data('curr-min'))
								, sliderCurrMax = parseFloat($slider.data('curr-max'))

								// DISTRELEC-6777: We have to increase the precision of the slider.
								, minimumRangeInPercent = 1
								, minimumRangeInValues = 5								
								;


							if (Math.abs(ui.values[ 0 ] - ui.values[ 1 ]) < minimumRangeInValues) {
								return false;
							}
							else {
								self.onSliderSlide(event, ui);
							}

						},
						stop: function (event, ui) {
							var facetItem = $(event.target).closest('.facet-item');
							self.onSliderStop(facetItem);
						}
					});

					$slider.closest('.facet-item').find('.slider-value').on('keyup', function(ev){
						self.onSliderInputValueChange(ev, $slider);
					});

				});
			}
		},

		//////////////////////////////////////////////////////////////////////////////

		//
		// Listener

		// Slider is created
		onSliderCreate: function (event, ui) {

			var $facetItem = $(event.target).closest('.facet-item')
				, $slider = $('.slider', $facetItem)
				, values
				;

			// always take values from data attributes, since url does not get updated in IE8/IE9
			values = [ parseFloat($slider.data('currMin')), parseFloat($slider.data('currMax')) ];

			$slider.slider('values', values);

			// Update current values and currency labels
			this.updateLabels(event, ui);
		},

		onSliderInputValueChange: function(ev, $slider){
			var $facetItem = $slider.closest('.facet-item')
				,inputFieldMin = $facetItem.find('.curr-min')
				,inputFieldMax = $facetItem.find('.curr-max')
				,currentMinValue = parseFloat(inputFieldMin.val())
				,currentMaxValue = parseFloat(inputFieldMax.val())
				,absMinValue = parseFloat($slider.data('abs-min'))
				,absMaxValue = parseFloat($slider.data('abs-max'))
				,validationError = false
				,errorMessageField = $facetItem.find('.field-msgs')
				,errorMessage = ""
				,mod = this
			;

			if(isNaN(currentMinValue) || isNaN(currentMaxValue)){
				validationError = true;
				errorMessage = errorMessageField.find('.error').data('error-1');
			}
			else if(currentMaxValue < currentMinValue){
				validationError = true;
				errorMessage = errorMessageField.find('.error').data('error-2');
			}
			else if(currentMinValue < absMinValue){
				currentMinValue = absMinValue;
				inputFieldMin.val(absMinValue);
				$('#lowpriceMeta').attr('content', absMinValue);
			}
			else if(currentMaxValue > absMaxValue){
				currentMaxValue = absMaxValue;
				inputFieldMax.val(absMaxValue);
			}

			errorMessageField.find('.error .message').empty();
			//clear any previous timer
			clearInterval(mod.timer);
			
			if(!validationError){
				errorMessageField.addClass('hidden');

				var values = [ currentMinValue, currentMaxValue ];
				$slider.slider('values', values);

				// set new timeout before loading products for the set value
				mod.timer = setTimeout(function() {
					mod.onSliderStop($facetItem);
				}, 1500);
			}
			else{
				errorMessageField.removeClass('hidden');
				errorMessageField.find('.error .message').html(errorMessage);
				return false;
			}
		},

		// Triggered many times while using the slider toggles
		onSliderSlide: function (event, ui) {

			// show current values
			this.updateLabels(event, ui);
		},

		// Trigger ajax face product reload after releasing a slider handle
		onSliderStop: function ($facetItem) { // need to add in the url here

			var facetName = $facetItem.data('facet-name')
				, sliderValues = $('.slider', $facetItem).slider('values')
				, facetValue = sliderValues[0] + '+-+' + sliderValues[1]
				, currentUrl = $facetItem.data('facet-url-path')
				, filterParam = this._getUrlParam(currentUrl, 'filter_' + facetName) 
				;

			if(this.autoApplyFilter || true){ // Always auto-update price filter
				this.$$('.ajax-action-overlay').removeClass('hidden');

				if (filterParam !== false) {
					var filterValueMatcher = new RegExp('filter_' + facetName + '=\\d\\.?\\d*\\+-\\+\\d\\.?\\d*');
					var filterValueReplacement = 'filter_' + facetName + '=' + facetValue;
					currentUrl = currentUrl.replace(filterValueMatcher, filterValueReplacement);
				} else {
					currentUrl += (currentUrl.split('?')[1] ? '&' : '?') + 'filter_' + facetName + '=' + facetValue;
				}

				// Clear any pending timers
				var id = window.setTimeout(function() {}, 0); // Get new (highest) timer ID
				while (id--) {
					window.clearTimeout(id); // will do nothing if no timeout with id is present
				}

				window.location.href = currentUrl; // Skip AJAX call, goto directly to new URL instead.

			} else {
				var mod = this
					,filterKey = 'price'
					,filterString = 'filter_' + facetName + '=' + facetValue;

				mod.fire('showReloadLayer', ['facetActions']);

				if(mod.reloadLayerFilterstring.hasOwnProperty(filterKey)){
					if(filterKey === 'price'){
						mod.reloadLayerFilterstring[filterKey] = filterString;
					}
				} else {
					mod.reloadLayerFilterstring[filterKey] = filterString;
				}
			}
		},

		typeDelay: function(){
			var timer = 0;
			return function(callback, ms){
				clearTimeout (timer);
				timer = setTimeout(callback, ms);
			};
		},

		bindAdditionalFacetChangeEventListener: function (ev) { // this is triggered when the facet list is updated
			
			ev.preventDefault();
			
			var selectedOption = $(ev.target).find(":selected")
				, facetUrl = selectedOption.val()
				, facetGroupTemplate = doT.template(this.$$('#tmpl-facet-group').html())
				, self = this
				, $select = self.$ctx.find('#select-additional-facets')
				, lightboxStatusTitle = $select.data('error-title')
				, lightboxStatusBoxTitle = $select.data('error-box-title')
				, lightboxStatusBoxMessage = $select.data('error-box-message')
				;

			
			// only use plugin method, if plugin was initialized
			if(!Modernizr.touch && ! Modernizr.isie7  && ! Modernizr.iseproc) {
				$select.data("selectBox-selectBoxIt").disable();
			}
			else{
				$select.attr('disabled', 'disabled').addClass('disabled');
			}

			if (facetUrl !== 'default') {
				$.ajax({
					url: facetUrl,
					type: 'get',
					dataType: 'json',
					success: function (response) {
						response.currentQueryUrl = ""; // is only needed for range slider, which is currently not available as additional facet
						response.expansionStatus = "is-collapsed";
						if (response.values !== undefined && response.values.length > 0) {
							var $additionalFacetGroup = $(facetGroupTemplate(response)).css("display", "none");
	
							// get i18n Strings and append to html
							var facetShowMoreLink = self.stringBundle.getString('FACET_SHOW_MORE_LINK', response);
							$additionalFacetGroup.find('.show-more-link').append(self.decodeHtml(facetShowMoreLink));
	
							var $facetValues = $additionalFacetGroup.find('.facet-list .product-count');
							$.each($facetValues, function (index, facetValue) {
								var valueProductCount = self.stringBundle.getString('FACET_VALUE_COUNT', response.values[index]);
								$(facetValue).text(valueProductCount);
							});
	
							var elementsToDecode = $additionalFacetGroup.find('.facet-list .facet-item a, .facet-header h4');
							$.each(elementsToDecode, function (index, element) {
								var newValue = self.decodeHtml($(element).html());
								$(element).html(newValue); 
							});				
	
							self.$$('.xmod-facets .other-facets').append($additionalFacetGroup);
	
							if(Modernizr.isie7  && ! Modernizr.iseproc){
								$additionalFacetGroup.show(); // IE7 shows weird behaviour for slideDown
							}
							else{
								$additionalFacetGroup.slideDown();
							}
							$additionalFacetGroup.find('.show-more-link').click();
	
							// Remove selected Option from dropdown and refresh selectboxit
							$select.find(":selected").remove();
	
							// only use plugin method, if plugin was initialized
							if(!Modernizr.touch && !  Modernizr.isie7 && ! Modernizr.iseproc) {
								$select.data("selectBox-selectBoxIt").refresh();
								$select.data("selectBox-selectBoxIt").selectOption(0);
							}
	
							if ($select.find('option').length === 1) {
								$select.closest('.additional-facets').addClass('hidden');
								$select.off();
							}
						} else {
							self.fire('error', {
								title: lightboxStatusTitle,
								boxTitle: lightboxStatusBoxTitle,
								boxMessage: lightboxStatusBoxMessage
							}, ['lightboxStatus']);
						}
					},
					error: function (jqXHR, textStatus, errorThrown) {
						// Remove selected Option from dropdown and refresh selectboxit
						$select.find(":selected").remove();

						// only use plugin method, if plugin was initialized
						if(!Modernizr.touch && !  Modernizr.isie7 && ! Modernizr.iseproc) {
							$select.data("selectBox-selectBoxIt").refresh();
							$select.data("selectBox-selectBoxIt").selectOption(0);
						}

						self.fire('error', {
							title: lightboxStatusTitle,
							boxTitle: lightboxStatusBoxTitle,
							boxMessage: lightboxStatusBoxMessage
						}, ['lightboxStatus']);
					},
					complete: function () {
						if(!Modernizr.touch && ! Modernizr.isie7 && ! Modernizr.iseproc) {
							$select.data("selectBox-selectBoxIt").enable();
						}
						else{
							$select.removeAttr('disabled').removeClass('disabled');
						}
					}
				});
			}
		},

		onFacetItemClick: function (ev) {
			var clickedFacet = $(ev.target).closest('.facet-item__anchor'),
				clickedFacetParent = clickedFacet.closest('li'),
				clickedFacetNumber = clickedFacetParent.data('facet-number');

			if(!clickedFacet.data('is-category-filter')){
				ev.preventDefault();
				if (!clickedFacet.hasClass('disabled')) {

					if(this.autoApplyFilter){
						this.$$('.ajax-action-overlay').removeClass('hidden');

						// gather data for ajax call
						var facetUrl = clickedFacet.attr('href')
							, clickedFacetGroup = $(clickedFacet).closest('.facet-group')
							, loadProductsForFacetSearchData = {}
							;
						
						var urlDocument = window.location.href;

						// prepare data for ajax call
						loadProductsForFacetSearchData.facetUrl = facetUrl;
						loadProductsForFacetSearchData.isPopstateEvent = false;
						loadProductsForFacetSearchData.clickedFacet = clickedFacet;
						loadProductsForFacetSearchData.clickedFacetGroup = clickedFacetGroup;
						loadProductsForFacetSearchData.errorTitle = this.loadProductsForFacetErrorTitle;
						loadProductsForFacetSearchData.errorMessage = this.loadProductsForFacetErrorMessage;

					    window.location.href = loadProductsForFacetSearchData.facetUrl;
					    
					} else {

						var mod = this
							,filterKey = clickedFacetParent.data('facet-value-name')
							,filterString = clickedFacetParent.data('filter-string');

						if (filterKey === undefined){
							filterKey = clickedFacet.closest('.facet-item__anchor').find('.facetValueName').text();
						}
						
						if (filterString === undefined){
							filterString = clickedFacet.closest('.facet-item__anchor').data('filter-string');
						}
							
						if (ev.shiftKey && mod.lastClickedFacetNumber !== 0 && typeof clickedFacetNumber !== 'undefined') {
							if (!mod.shiftReleased && mod.lastClickedFacetRange.length===2) {
								clickedFacet.closest('div.facet-value-list-container').find('li').slice(mod.lastClickedFacetRange[0],mod.lastClickedFacetRange[1]+1).each(function() { // New shift-click selection, clear checkboxes from previous selection
									$(this).removeClass('active').find('.facet-item__anchor').removeClass('active js-added');
									var filterKey = $(this).data('facet-value-name') || clickedFacet.closest('.facet-item__anchor').find('.facetValueName').text();
									delete mod.reloadLayerFilterstring[filterKey];
								});
								mod.lastClickedFacetNumber = mod.lastClickedFacetNumber-1 === mod.lastClickedFacetRange[0] ? mod.lastClickedFacetRange[1] : mod.lastClickedFacetRange[0]+1;
							}
							var startIndex  = Math.min(clickedFacetNumber, mod.lastClickedFacetNumber)-1,
								endIndex  = Math.max(clickedFacetNumber, mod.lastClickedFacetNumber);
							clickedFacet.closest('div.facet-value-list-container').find('li').slice(startIndex,endIndex).not(clickedFacetParent).each(function() { // Update all checkboxes except the clicked one, it will be handled later
								$(this).addClass('active').find('.facet-item__anchor').addClass('active js-added');
								var filterKey = $(this).data('facet-value-name'),
									filterString = $(this).data('filter-string');
								if (filterKey === undefined){
									filterKey = clickedFacet.closest('.facet-item__anchor').find('.facetValueName').text();
								}
								if (filterString === undefined){
									filterString = clickedFacet.closest('.facet-item__anchor').data('filter-string');
								}
								mod.reloadLayerFilterstring[filterKey] = filterString;
							});
							mod.shiftReleased = false;
							mod.lastClickedFacetRange = [startIndex,endIndex];
						} else {
							mod.lastClickedFacetRange = [];
						}

						var $elementsInTheFilterBox = $('.filterBoxElement').toArray();

						if (mod.reloadLayerFilterstring.hasOwnProperty(filterKey)){
							delete mod.reloadLayerFilterstring[filterKey]; // remove filter which already was selected
						} else {
							mod.reloadLayerFilterstring[filterKey] = filterString;
							//mod.filtersToRemove.push(filterString);
						}
						
						if (clickedFacet.hasClass('active')){
							mod.filtersToRemove.push(filterString);
							clickedFacet.removeClass('active');
							clickedFacetParent.removeClass('active');
						} else {
							clickedFacet.addClass('active');
							clickedFacetParent.addClass('active');
						}
						
						// allow removing facets for reload layer
						clickedFacet.addClass('js-added');
						
						// Save last clicked checkbox as start point for shift-click selection 
						mod.lastClickedFacetNumber = clickedFacet.hasClass('js-added') ? clickedFacetNumber : 0;

						// Disable buttons if nothing was changed from initial state
						if ($('.js-added').length) {
							mod.$$('.button-container button').removeClass('disabled').removeAttr('disabled');
						} else {
							mod.$$('.button-container button').addClass('disabled').attr('disabled', 'disabled');
						}


                        var $facetList  = clickedFacet.closest('.facet-group').find('li.facet-item.active');
						var $facetPlpSelectionLabel = clickedFacet.closest('.facet-group').find('.plp-filter-search-facets__filter-selection'),
                            $facetPlpSelectionNum = clickedFacet.closest('.facet-group').find('.plp-filter-search-facets__filter-selection-number'),
                            $facetListLabelAll = clickedFacet.closest('.facet-group').find('.plp-filter-search-facets__filter-all-label input'),
							$facetListResetBtn = clickedFacet.closest('.facet-group').find('.facet-reset'),
                            $facetListHeaderAllLabel = clickedFacet.closest('.facet-group').find('.toggle__mobile-label');

                        $facetPlpSelectionNum.html($facetList.length);

                        mod.checkResultCountOnFilterSelection(clickedFacet);

						if ( $facetList.length > 0 ) {
                            $facetListResetBtn.removeClass('hidden');
                            $facetPlpSelectionLabel.addClass('open');
                            $facetListHeaderAllLabel.addClass('hidden');
                            $facetListLabelAll.prop("checked", false);
                            $facetListLabelAll.parent().removeClass('active');
						} else {
                            $facetListResetBtn.addClass('hidden');
                            $facetPlpSelectionLabel.removeClass('open');
                            $facetListHeaderAllLabel.removeClass('hidden');
                            $facetListLabelAll.prop("checked", true);
                            $facetListLabelAll.parent().addClass('active');
						}

					}
				}
			}


		},

        checkResultCountOnFilterSelection: function (e,b) {
            $('.plp-filter-controllbar__apply-filter').addClass('disabled').attr('disabled', 'disabled');

			var currentUrl=document.URL;
			currentUrl=currentUrl.replace("/c/", "/c/cnt/");

			var $facetGroupList = $('#plp-filter-search-facets');
			var facetUrl = ($('body').hasClass('skin-layout-category') ? document.URL : $('.search-results.productlist').data('current-query-url'));
			var $facetItems = $facetGroupList.find('li.facet-item');

            if ($facetItems.length) {

            	var facetFilter = $(e).closest('li.facet-item').data('filter-string');

            	if(facetFilter !== undefined) {
					var facetFilterPathName = facetFilter.split("=")[0];
					var reStringFilter = '&?' + facetFilterPathName.replace(/\+/g,'\\+').replace(/\./g,'\\.') + '=[^&]+';
					var reFilter = new RegExp(reStringFilter, 'g');
					facetUrl = facetUrl.replace(reFilter, '');
				}

                if (facetUrl.indexOf('?')===-1) {
                    facetUrl+='?'+this.$$('.other-facets').data('non-filter-parameters')+this.$$('.other-facets').data('all-filter-parameters');
                }
                $facetItems.each(function(i) {
                    if ($(this).find('.facet-item__anchor').hasClass('active')) {
                        facetUrl += '&' + $(this).data('filter-string');
                    }
                });
            }
            if (b && this.lastClickedFacetName !== '') {
                facetUrl+='&xF='+this.lastClickedFacetName;
            }

            var urlObj = Tc.Utils.splitUrl(facetUrl),
                method = facetUrl.length > 2047 ? 'POST' : 'GET';

            var sort = this._getUrlParam(currentUrl, 'sort');

            if (sort !== undefined && sort !== false && sort !== 'false') {
                urlObj.get.sort = sort;
            }

			if($('body').hasClass('skin-layout-product-family')) {
				urlObj.base = window.location.pathname;
			}

            Tc.Utils.postFormForCount(urlObj.base,urlObj.get,method,true);
        },

		// event is coming from productlist
		onLoadProductsForFacetSearchReloadLayerData: function(data) {
			var mod = this
				,getParams = [];

			$.each(mod.reloadLayerFilterstring, function(key, value) {
				var getParam = value;
				getParams.push(getParam);
			});

			// gather data for ajax call
			var currentQueryUrl = data.currentQueryUrl;
			var loadProductsForFacetSearchData = {};

			var facetUrl = currentQueryUrl + '&' + getParams.join([separator = '&']);
			facetUrl = facetUrl.replace('/&&/g','&');
			
			$.each(mod.filtersToRemove, function(key, filterString) {
				//remove filterString if exist in facetUrl
				if (currentQueryUrl.indexOf(filterString) != -1){
					var find = filterString;
					var re = new RegExp(find, 'g');
					facetUrl = facetUrl.replace(re, '');
					facetUrl = facetUrl.replace(filterString, '');
					facetUrl = facetUrl.replace(filterString, '');
					facetUrl = facetUrl.replace(filterString, '');
					facetUrl = facetUrl.replace(filterString, '');
					facetUrl = facetUrl.replace(filterString, '');
				}
			});

			// prepare data for ajax call
			loadProductsForFacetSearchData.facetUrl = facetUrl;
			loadProductsForFacetSearchData.isPopstateEvent = false;
			loadProductsForFacetSearchData.clickedFacet = '';
			loadProductsForFacetSearchData.clickedFacetGroup = '';
			loadProductsForFacetSearchData.isReloadLayerUpdate = true;
			loadProductsForFacetSearchData.errorTitle = this.loadProductsForFacetErrorTitle;
			loadProductsForFacetSearchData.errorMessage = this.loadProductsForFacetErrorMessage;

			mod.fire('loadProductsForFacetSearch', loadProductsForFacetSearchData, ['facetActions']);
		},

		// load products for facet callback coming from product-list module
		onLoadProductsForFacetSearchCallback: function (data) {
			if (data.status) {
				this.generateActiveFilters(data);
				this.generateFacetGroups(data);
				this.generateAdditionalFacetGroupsDropDownOptions(data);
			}
			else{
				$(data.clickedFacet).removeClass('active');

				// if it was a reload layer search, remove selected filters
				$.each($('.js-added'), function() {
					if($(this).hasClass('js-added')) {
						$(this).removeClass('js-added');
						$(this).removeClass('active');
					}
				});

				this.reloadLayerFilterstring = {}; // empty object

			}

			this.removeExclusiveFacetsfromXmodFilter();
			this.$$('.ajax-action-overlay').addClass('hidden');
		},

		
		removeExclusiveFacetsfromXmodFilter: function(){
			
			var activeFacets = this.$$('.active').parent();
			var activeFacetsList = [];
			
			$.each(activeFacets, function (index, facet) {
				var facetValueName =  facet.getAttribute('data-facet-value-name');
				activeFacetsList.push(facetValueName);
			});
			
			var currentFilterBox = this.$$('.filters-box').children();
			$.each(currentFilterBox, function (index, facet) {
				var facetValueName =  facet.getAttribute('data-facet-value-name');
				var facetFilterString = facet.getAttribute('data-filter-string');
				
			});			
			
		},
		
		onFacetsWindowsHistoryPopStateEvent: function (popstateEvent) {
			var loadProductsForFacetSearchData = {};

			this.$$('.ajax-action-overlay').removeClass('hidden');

			loadProductsForFacetSearchData.facetUrl = window.location.href;
			loadProductsForFacetSearchData.isPopstateEvent = true;
			loadProductsForFacetSearchData.errorTitle = this.loadProductsForFacetErrorTitle;
			loadProductsForFacetSearchData.errorMessage = this.loadProductsForFacetErrorMessage;

			this.fire('loadProductsForFacetSearch', loadProductsForFacetSearchData, ['facetActions']);
		},

		generateActiveFilters: function (data) {
			var mod = this
				, activeFacetFilterTemp = this.$$('#tmpl-facet-active-filter').html()
				, activeFacetFilterTempWrapepr = '<li class="facet-item filters-box">' +
					'<a href="{{=it.productUrl}}" data-is-category-filter="{{= it.isCategory }}" ' +
					'data-facet-value-name="{{= it.facetValueName }}" data-filter-string="{{= it.filterString }}" ' +
					'class="facet-link">'+activeFacetFilterTemp+'</a>' +
					'</li>'
				, activeFacetFilterTemplate = doT.template(activeFacetFilterTempWrapepr)
				, activeFilterContainer = this.$$('.xmod-filter .bd ul')
				;

			activeFilterContainer.empty();

			$.each(data.filters, function (index, filterItem) {
				// Generate active facet filter items
				filterItem.name = mod.decodeHtml(filterItem.name);
				
				var activeFacetFilter = activeFacetFilterTemplate(filterItem);
				mod.$$('.xmod-filter .bd ul').append(activeFacetFilter);
			});

			if(data.filters.length === 0){
				this.$$('.empty-filter-msg').removeClass('hidden');
				this.$$('.clear-link').addClass('hidden');
			}
			else{
				this.$$('.empty-filter-msg').addClass('hidden');
				this.$$('.clear-link').removeClass('hidden');
			}
		},

		decodeHtml: function(html){
			var txt = document.createElement("textarea");
			txt.innerHTML = html;
			return txt.value;			
		},

		generateFacetGroups: function (data) {
			var mod = this
				, facetGroupTemplate = doT.template(this.$$('#tmpl-facet-group').html())
				, expandedFacetCount = this.$$('.other-facets').data('open-facets-count')
				, hasSlider = false
				, facetGroupCounter = 0
				;

			mod.$$('.xmod-facets .other-facets').empty();

			$.each(data.facetGroups, function (index, facetGroup) {
				facetGroup.currentQueryUrl = data.currentQueryUrl;
				var $additionalFacetGroup = $(facetGroupTemplate(facetGroup));

				// get i18n Strings and append to html
				var facetShowMoreLink = mod.stringBundle.getString('FACET_SHOW_MORE_LINK', facetGroup);
				$additionalFacetGroup.find('.show-more-link').append(mod.decodeHtml(facetShowMoreLink));

				var productCountValues = $additionalFacetGroup.find('.facet-list .product-count');
				$.each(productCountValues, function (index, productCountValue) {
					var valueProductCount = mod.stringBundle.getString('FACET_VALUE_COUNT', facetGroup.values[index]);
					$(productCountValue).text(valueProductCount);
				});

				var elementsToDecode = $additionalFacetGroup.find('.facet-list li .facet-item__anchor');
				$.each(elementsToDecode, function (index, element) {
					var newValue = mod.decodeHtml($(element).html());
					$(element).html(newValue); 
				});				
				
				mod.$$('.xmod-facets .other-facets').append($additionalFacetGroup);

				// set expansion status
				if(facetGroupCounter < expandedFacetCount){
					$($additionalFacetGroup).addClass('is-expanded');
					$($additionalFacetGroup).find('.facet-list').css('display', 'block');
				}

				if (facetGroup.type === "SLIDER") {
					hasSlider = true;
				}

				facetGroupCounter++;
			});

			if (hasSlider) {
				mod.initSliders();
			}
		},

		generateAdditionalFacetGroupsDropDownOptions: function (data) {
			var $select = this.$ctx.find('#select-additional-facets');
			// unbind change listener temporary to remove and add options
			$select.off();

			if (data.lazyFacetsCount >= 0) {
				$select.show();
				var additionalFacetOptionTemplate = doT.template(this.$$('#tmpl-additional-facet-dropdown-option').html());

				$select.find('option:not(.option-default)').remove(); 

				$.each(data.lazyFacets, function (index, lazyFacet) {
					lazyFacet.index = index;
					var lazyFacetOption = additionalFacetOptionTemplate(lazyFacet);
					$select.append(lazyFacetOption);

				});
			}
			else {
				$select.hide();
				$select.empty();
			}

			// only refresh, if plugin was initialized
			if(!Modernizr.touch && ! Modernizr.isie7 && ! Modernizr.iseproc) {
				$select.data("selectBox-selectBoxIt").refresh();
				$select.data("selectBox-selectBoxIt").selectOption(0);
			}

			// re-add change listener
			$select.on('change.selectFacetOption', this.bindAdditionalFacetChangeEventListener);	

		},

		//////////////////////////////////////////////////////////////////////////////

		//
		// Helper

		updateLabels: function (event, ui) {

			var $facetItem = $(event.target).closest('.facet-item')
				, $slider = $('.slider', $facetItem)
				, $labelCurrMin = $('.curr-min', $facetItem)
				, $labelCurrMax = $('.curr-max', $facetItem)
				, shopSettings = $('#backenddata .shopsettings').data()
				;

			// initially take the values from the slider because ui.values doesn't exist yet
			if (ui.values === undefined) {
				ui.values = $slider.slider('values');
			}
			$labelCurrMin.val(parseFloat(ui.values[0]));
			$labelCurrMax.val(parseFloat(ui.values[1]));

			// hide possible error message from direct input
			$facetItem.find('.field-msgs').addClass('hidden');
		},

		showHideFacetValues: function (e) {
			e.preventDefault();

			// get facet group of clicked elem
			var $target = $(e.target).closest('.facet-item__anchor');

			if ($target.hasClass('show-more-link')) {
				var mod = this
					, $toggler = $target
					, $facetGroup = $toggler.closest('.facet-group')
					, $facetList = $facetGroup.find('.facet-list')
					, $expandedFacetGroups = this.$$('.facet-group.is-expanded')
					, $otherFacetGroups = $expandedFacetGroups.not($facetGroup)
					, $otherFacetLists = $otherFacetGroups.find('.facet-list').not('.facet-list-type-slider')
					, $otherFacetSlider = $otherFacetGroups.find('.facet-list-type-slider')
					, $activeButtons = $expandedFacetGroups.find('.btn-apply-filter').not('.disabled')
					;
				this.lastClickedFacetName = $facetGroup.not('.is-expanded').length ? $facetGroup.not('.is-expanded').data('facet-group-name') : '';

                this.$$('.ajax-action-overlay').addClass('hidden');

				// close all expanded facet groups
				$otherFacetGroups.removeClass('is-expanded');
				$otherFacetSlider.slideUp();
				mod.$$('.xmod-facets, .xmod-filter').css({position:''});
				
				// Run filter if a selection is made
				if ($activeButtons.length) {
					$activeButtons.first().trigger('click',[true]);
					if ($facetGroup.hasClass('is-expanded')) {
                        this.$$('.ajax-action-overlay').addClass('hidden');
                        mod.$$('.xmod-facets, .xmod-filter').css({position:''});
					}
					return;
				}

				// toggle expanded class
				$facetGroup.toggleClass('is-expanded'); // toggle the group list

                if ($facetList.hasClass('facet-list-type-slider')) {
                    this.$$('.ajax-action-overlay').addClass('hidden');
                    this.$$('.xmod-facets, .xmod-filter').css({position:'initial'});
                } else {
                    if ( $facetGroup.hasClass('is-expanded') ){
                        this.$$('.ajax-action-overlay').removeClass('hidden');
                        $('.facet-group.is-expanded .facet-list').removeClass('hidden');
                        mod.$$('.xmod-facets, .xmod-filter').css({position:'inherit'});

                        var aHeight = $facetList.siblings('a').height();
                        var facetListLeft = $('.facet-group').css('width');
                        $facetList.css({'display':'block','left':facetListLeft});

                        var filterHeight = parseInt( $('.mod-facets').css('height'), 10);
                        var filterPopupHeight = parseInt( $('.facet-group.is-expanded .facet-list-type-checkbox').css('height'), 10);

                        if (filterPopupHeight > filterHeight ) {
                            $facetList.css('top','-30px');
						}

                    } else {
                        this.$$('.ajax-action-overlay').addClass('hidden');
                        mod.$$('.xmod-facets, .xmod-filter').css({position:''});
                    }
                }

			}

		},

		setPlpProductListDetailView: function(_plpProductListView) {

			if(_plpProductListView){
				$('.plp-filter-products__information__data__main').addClass('showdetailview');
				$('.plp-filter-products__price__second').addClass('showdetailview');
				$('.plp-filter-controllbar__onoffswitch-switch').addClass('showdetailview');
				digitalData.page.pageInfo.viewType="detailed view";
			} else {
				$('.plp-filter-products__information__data__main').removeClass('showdetailview');
				$('.plp-filter-products__price__second').removeClass('showdetailview');
				$('.plp-filter-controllbar__onoffswitch-switch').removeClass('showdetailview');
				digitalData.page.pageInfo.viewType="compact view";
			}

		},

        slidePlpSearchFacet: function (e) {
            e.preventDefault();

            var mod = this,
                $target = $(e.target);

            var $facetIndex = $target.closest('.facet-group').data('facet-index'),
            	scrollTo = $('#plp-search-facet-'+$facetIndex).data('leftpos'),
				$scrollToElement = $( "#plp-filter-search-facets" );

            if ( $('html').hasClass('isie7') || $('html').hasClass('isie8') ) {
                $scrollToElement = $( ".plp-filter-search-facets" );
            }

            $scrollToElement.animate({
                scrollLeft: scrollTo
            }, 500, function() {
				// Animation complete.
            });

        },

		// Monitor status of Shift key (for shift-select range extension)
		shiftGear: function () {
			var self = this;
			
			$(document).keyup(function(e) {
				if (!e.shiftKey) {
					self.shiftReleased = true;
				}
			});		
			
		},

		// Hide expanded facet value lists

		hideFacetValues: function (e) {
			var mod = this,
				$facetGroup = mod.$$('.facet-group.is-expanded').not('.type-slider'),
				$facetList = $facetGroup.find('.facet-list');

            $facetList.css({'display':'none'});
            mod.$$('.ajax-action-overlay').addClass('hidden');
            mod.$$('.xmod-facets, .xmod-filter').css({position:'initial'});
			$facetGroup.removeClass('is-expanded');
			e.preventDefault();
            $('.facet-list').addClass('hidden');
		},
		
        checkPlpSearchCurrentFacetCSelection: function(_plpFilterSearchFacetLen) {

            $('#plp-filter-search-facets li').each(function(i, li) {
                var $product = $(li);

                var currentFacelListLength = $product.find('.facet-value-list .facet-item.active').length,
                    $currentFacetSelectionNum = $product.find('.plp-filter-search-facets__filter-selection-number'),
                    $facetPlpSelectionLabel = $product.find('.plp-filter-search-facets__filter-selection'),
                    currentFacelListLabelAll = $product.find('.plp-filter-search-facets__filter-all-label input'),
                    $facetListHeaderAllLabel = $product.find('.toggle__mobile-label'),
                    $facetListResetBtn = $product.find('.facet-reset');

                $currentFacetSelectionNum.html(currentFacelListLength);

                if (currentFacelListLength > 0) {
                    currentFacelListLabelAll.prop("checked", false);
                    currentFacelListLabelAll.parent().removeClass('active');
                    $facetPlpSelectionLabel.addClass('open');
                    $facetListHeaderAllLabel.addClass('hidden');
                    $facetListResetBtn.removeClass('hidden');
				} else {
                    currentFacelListLabelAll.prop("checked", true);
                    currentFacelListLabelAll.parent().addClass('active');
                    $facetPlpSelectionLabel.removeClass('open');
                    $facetListHeaderAllLabel.removeClass('hidden');
                    $facetListResetBtn.addClass('hidden');
				}

            });

        },

		clearPlpSearchCurrentFacetCSelection: function(e) {

            var mod = this,
                $target = $(e.target);

            var $facetList  =$target.closest('.facet-group').find('li.facet-item.active'),
                $facetPlpSelectionLabel = $target.closest('.facet-group').find('.plp-filter-search-facets__filter-selection'),
            	$facetPlpLabelAll = $target.closest('.facet-group').find('.plp-filter-search-facets__filter-all-label input'),
            	$facetPlpResetBtn = $target.closest('.facet-group').find('.facet-reset'),
                $facetListHeaderAllLabel = $target.closest('.facet-group').find('.toggle__mobile-label');


            $facetList.each (function () {
                var filterKey = $(this).data('facet-value-name'),
                    filterString =  $(this).data('filter-string');
                if (filterKey === undefined){
                    filterKey = $(this).find('.facet-item__anchor').find('.facetValueName').text();
                }
                if (filterString === undefined){
                    filterString = $(this).find('.facet-item__anchor').data('filter-string');
                }
                if ($target.hasClass('facet-reset')) {
                    mod.$$('.min-max-wrapper select').val(0).find('option').not(':first-child').removeAttr('disabled');
                }
            });

            $facetList.removeClass('active').find('.facet-item__anchor').removeClass('active');
            mod.$$('.btn-apply-filter').removeClass('disabled').removeAttr('disabled');

            $facetPlpResetBtn.addClass('hidden');
            $facetPlpSelectionLabel.removeClass('open');
            $facetPlpLabelAll.prop("checked", true);
            $facetPlpLabelAll.parent().addClass('active');
            $facetListHeaderAllLabel.removeClass('hidden');

			mod.checkResultCountOnFilterSelection();

        },
		
		clearFacetSelection: function(e) {
			var mod = this,
				$target = $(e.target);
			var $facetList  =$target.closest('.facet-group.is-expanded').find('li.facet-item.active');
			$facetList.each (function () {
				var filterKey = $(this).data('facet-value-name'),
					filterString =  $(this).data('filter-string');
				if (filterKey === undefined){
					filterKey = $(this).find('.facet-item__anchor').find('.facetValueName').text();
				}
				if (filterString === undefined){
					filterString = $(this).find('.facet-item__anchor').data('filter-string');
				}
				if ($target.hasClass('btn-reset')) {
					mod.$$('.min-max-wrapper select').val(0).find('option').not(':first-child').removeAttr('disabled');
				}

			});
			
			$facetList.removeClass('active').find('.facet-item__anchor').removeClass('active');
			mod.$$('.btn-apply-filter').removeClass('disabled').removeAttr('disabled');

			var queryUrl = window.location.href.split("?")[1];

			if (queryUrl !== undefined && queryUrl.length !== 0) {
				var extractQueryTerm = queryUrl.split("=")[1].split("&")[0];
				Tc.Utils.postFormForCount(window.location.pathname,{q: extractQueryTerm},'GET',true);
			}
		},
		
		updateMinMaxValues: function (e) {
			var mod = this,
				$target = $(e.target),
				$other = $target.siblings('select'),
				$otherOptions = $other.find('option').not(':first'),
				val = parseFloat($target.val()),
				oVal =  parseFloat($other.val()),
				startIndex = -1,
				endIndex = 0;
			$otherOptions.removeAttr('disabled');
			if ($target.hasClass('select-min-value')) {
				startIndex  = val-1;
				endIndex = oVal > 0 ? oVal : $otherOptions.length;
				$otherOptions.slice(0,startIndex).attr('disabled','disabled');
				$other.val(endIndex);
			} else {
				startIndex = oVal > 0 ? oVal-1 : 0;
				endIndex = val;
				if (val<$otherOptions.length+1 && val > 0) {
					$otherOptions.slice(endIndex).attr('disabled','disabled');
					$other.val(startIndex+1);
				}
			}
			if (startIndex > -1 && endIndex > 0) {
				mod.clearFacetSelection(e);
                mod.clearPlpSearchCurrentFacetCSelection(e);
				$target.closest('div.facet-list').find('li').slice(startIndex,endIndex).each(function() { // Update all checkboxes
					$(this).addClass('active').find('.facet-item__anchor').addClass('active js-added');
					var filterKey = $(this).data('facet-value-name'),
						filterString = $(this).data('filter-string');
					if (filterKey === undefined){
						filterKey = clickedFacet.closest('.facet-item__anchor').find('.facetValueName').text();
					}
					if (filterString === undefined){
						filterString = clickedFacet.closest('.facet-item__anchor').data('filter-string');
					}
					mod.reloadLayerFilterstring[filterKey] = filterString;
				});
				mod.$$('.button-container button').removeClass('disabled').removeAttr('disabled');
			}

            var $facetList  = $target.closest('.facet-group').find('li.facet-item.active');
            var $facetPlpSelectionLabel = $target.closest('.facet-group').find('.plp-filter-search-facets__filter-selection'),
                $facetPlpSelectionNum = $target.closest('.facet-group').find('.plp-filter-search-facets__filter-selection-number'),
                $facetListLabelAll = $target.closest('.facet-group').find('.plp-filter-search-facets__filter-all-label input'),
                $facetListResetBtn = $target.closest('.facet-group').find('.facet-reset'),
                $facetListHeaderAllLabel = $target.closest('.facet-group').find('.toggle__mobile-label');

            $facetPlpSelectionNum.html($facetList.length);

            mod.checkResultCountOnFilterSelection();


            if ( $facetList.length > 0 ) {
                $facetListResetBtn.removeClass('hidden');
                $facetPlpSelectionLabel.addClass('open');
                $facetListHeaderAllLabel.addClass('hidden');
                $facetListLabelAll.prop("checked", false);
                $facetListLabelAll.parent().removeClass('active');
            } else {
                $facetListResetBtn.addClass('hidden');
                $facetPlpSelectionLabel.removeClass('open');
                $facetListHeaderAllLabel.removeClass('hidden');
                $facetListLabelAll.prop("checked", true);
                $facetListLabelAll.parent().addClass('active');
            }

		},

        plpApplyFilter: function(e,b) {

            var $facetGroupList = $('#plp-filter-search-facets'),
                facetUrl= $('.search-results.productlist').data('current-query-url') || document.URL,
                $facetItems = $facetGroupList.find('li.facet-item'),
                fullUrl = (window.location.href.indexOf("pageSize=") === -1 ? '' : window.location.href),
                $pageSize;

            if ( window.location.href.indexOf("pageSize=") > 0 ) {
                $pageSize = window.location.href.split("pageSize=")[1].split('&')[0];
            }

            if ($facetItems.length) {

				facetUrl = window.location.pathname;
				if (facetUrl.indexOf('?')===-1) {
                    facetUrl+='?'+$facetGroupList.data('non-filter-parameters') + $facetGroupList.data('all-filter-parameters');
                }

                var $facetCopyUrl = facetUrl,
                	$facetUrlLen = $facetCopyUrl.split('&').length;

                $facetItems.each(function(i) {

                	var filterString = $(this).data('filter-string');

                    if ($(this).find('.facet-item__anchor').hasClass('active')) {

                        facetUrl += '&' + filterString;

                    }

                });

            }

            var originalParams = window.location.search.substr(1).split('&');
            for (var i = 0; i < originalParams.length; i++) {
                var param = originalParams[i];
                if (param.startsWith('filter_categoryCodePathROOT')) {
                   facetUrl += '&' + param;
                }
            }

            var pageSizeParam = ($('body').hasClass('skin-layout-category') ? '?' : '&');
            facetUrl += fullUrl.substring(fullUrl.lastIndexOf(pageSizeParam));

            if ($pageSize > 0) {

            	if ( facetUrl.indexOf("pageSize=") < 0 ) {
                    facetUrl += "&pageSize="+$pageSize;
				}

            }

            var urlObj = Tc.Utils.splitUrl(facetUrl),
                method = facetUrl.length > 2047 ? 'POST' : 'GET';

            var sort = this._getUrlParam(document.URL, 'sort');

            if (sort !== undefined && sort !== false && sort !== 'false') {
                urlObj.get.sort = sort;
            }

            sessionStorage.setItem('PlpProductListRefreshfrom', 'applyplpfilter');

            if($('body').hasClass('skin-layout-product-family')) {
				urlObj.base = window.location.pathname;
			}

            Tc.Utils.postForm(urlObj.base,urlObj.get,method,true);

        },

		// Utils

		_getUrlParam: function (url, param) {

			if (url === undefined) {
				return true;
			}

			var urlAttributes=[];
			if (url.indexOf('?')!==-1) {
				urlAttributes = url.split("?");
			} else {
				urlAttributes[1] = url;
			}

			if(urlAttributes[1] !== undefined){
				var vars = urlAttributes[1].split("&");
				for (var i = 0; i < vars.length; i++) {
					var pair = vars[i].split("=");
					if (pair[0] == param) {
						return pair[1];
					}
				}
			}
			return(false);
		}
	});

})(Tc.$);
