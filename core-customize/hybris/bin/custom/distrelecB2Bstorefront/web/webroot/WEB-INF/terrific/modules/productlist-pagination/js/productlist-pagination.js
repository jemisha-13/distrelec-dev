(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class ProductlistPagination
	 * @extends Tc.Module
	 */
	Tc.Module.ProductlistPagination = Tc.Module.extend({

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
		init: function($ctx, sandbox, id) {
			// call base constructor
			this._super($ctx, sandbox, id);

			// subscribe to connector channel/s
			this.sandbox.subscribe('productlist', this);
			this.sandbox.subscribe('myaccount', this);
			this.sandbox.subscribe('facetActions', this);
			this.sandbox.subscribe('windowHistoryEvents', this);

			this.bindSelectPage = $.proxy(this, 'bindSelectPage');
			this.bindChangeEventListener = $.proxy(this, 'bindChangeEventListener');
			this.onPaginationChangeCallback = $.proxy(this, 'onPaginationChangeCallback');
			this.onPaginationWindowsHistoryPopStateEvent = $.proxy(this, 'onPaginationWindowsHistoryPopStateEvent');
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function(callback) {
			var $ctx = this.$ctx,
				self = this,
				$select = $ctx.find('.js-page-size'),
                $plpViewlistDropdownBtn = $ctx.find('.plp-viewlist-dropdown__dropbtn');

			// Lazy Load SelectBoxIt Dropdown
			if(!Modernizr.touch && ! Modernizr.isie7 && ! Modernizr.iseproc) {
				Modernizr.load([{
					load: '/_ui/all/cache/dyn-jquery-selectboxit.min.js',
					complete: function () {
						$select.selectBoxIt({
							autoWidth : false
						});
					}
				}]);
			}

			$select.on('change', self.bindChangeEventListener);
			this.$$('.js-page-link').on('click', this.bindSelectPage);

            if (typeof(Storage) !== "undefined") {
                var plpProductListDetailView = localStorage.getItem("PlpProductListDetailView");
                var plpProductLeftFilters = localStorage.getItem("plpProductLeftFilters");

                if ( plpProductListDetailView == 'true') {
                    self.setPlpProductListDetailView(true);
                } else {
                    self.setPlpProductListDetailView(false);
                }

				if ( plpProductLeftFilters == 'true') {
					self.setPlpProductListFilters(true);
				} else {
					self.setPlpProductListFilters(false);
				}

            }

			var $plpViewlistDropdown = $('.plp-viewlist-dropdown');

            $plpViewlistDropdownBtn.on('click', function(ev){

                if ( $plpViewlistDropdown.hasClass('active') ) {
                    $plpViewlistDropdown.removeClass('active');
				} else  {
                    $plpViewlistDropdown.addClass('active');
				}
            });

            $('.plp-viewlist-dropdown__content__compactbtn').on('click', function(ev){
                self.setPlpProductListDetailView(false);
                $('.plp-viewlist-dropdown').removeClass('active');
				$('#plp-viewlist-compactbtn').attr( 'checked', true );
				if (typeof(Storage) !== "undefined") {
                    localStorage.setItem("PlpProductListDetailView", "false");
                }
            });

            $('.plp-viewlist-dropdown__content__detailedbtn').on('click', function(ev){
                self.setPlpProductListDetailView(true);
                $('.plp-viewlist-dropdown').removeClass('active');
				$('#plp-viewlist-detailedbtn').attr( 'checked', true );
                if (typeof(Storage) !== "undefined") {
                    localStorage.setItem("PlpProductListDetailView", "true");
                }
            });

			$('.plp-viewlist-dropdown__content__sidebarbtn').on('click', function(ev){
				self.setPlpProductListFilters(false);
				$('.plp-viewlist-dropdown').removeClass('active');
				if (typeof(Storage) !== "undefined") {
					localStorage.setItem("plpProductLeftFilters", "false");
				}
			});

			$('.plp-viewlist-dropdown__content__topbtn').on('click', function(ev){
				self.setPlpProductListFilters(true);
				$('.plp-viewlist-dropdown').removeClass('active');
				if (typeof(Storage) !== "undefined") {
					localStorage.setItem("plpProductLeftFilters", "true");
				}
			});

			$('html').on('click', 'body', function(e){

				if ($plpViewlistDropdown.has(e.target).length <= 0){
					$plpViewlistDropdown.removeClass('active');
				}
			});

			callback();
		},

        setPlpProductListDetailView: function(_plpProductListView) {

            if(_plpProductListView){
                $('.plp-filter-products__product__info__detailed').addClass('showdetailview');
                $('.plp-filter-products__product__right__toggle').addClass('showdetailview');
				$('.plp-filter-products__product__right__show-details').addClass('hide');
				$('.plp-filter-products__product__right__hide-details').removeClass('hide');
                $('.plp-pagination-wrapper__product-action-bar__view-toggle').addClass('active');
				$('#plp-viewlist-detailedbtn').attr( 'checked', true );
				digitalData.page.pageInfo.viewType="detailed view";

            } else {
                $('.plp-filter-products__product__info__detailed').removeClass('showdetailview');
                $('.plp-filter-products__product__right__toggle').removeClass('showdetailview');
				$('.plp-filter-products__product__right__show-details').removeClass('hide');
				$('.plp-filter-products__product__right__hide-details').addClass('hide');
                $('.plp-pagination-wrapper__product-action-bar__view-toggle').removeClass('active');
				$('#plp-viewlist-compactbtn').attr( 'checked', true );

				digitalData.page.pageInfo.viewType="compact view";
            }

        },
		setPlpProductListFilters: function(_plpLeftFilterView) {

			if(_plpLeftFilterView === false){
				$('#plp-viewlist-sidebarbtn').attr( 'checked', true );
				$('.plp-content__nav-filters').attr('id', 'left-filter-nav');
				$('.mod-categorynav').addClass('skin-categorynav-plp--active');
				$('.skin-layout-product-list').addClass('sidebar-filters');
				$('.skin-layout-store').addClass('sidebar-filters');
				if($('.sidebar-applied-filters').hasClass('hidden')) {
					$('.sidebar-applied-filters').removeClass('hidden');
				}
				var paginationOffset = document.querySelector('.plp-content__nav-filters');
				window.scrollTo(0, paginationOffset);

			} else {
				$('#plp-viewlist-topbtn').attr( 'checked', true );
				$('.plp-content__nav-filters').attr('id', 'top-filter-nav');
				$('.skin-layout-product-list').removeClass('sidebar-filters');
				$('.skin-layout-store').removeClass('sidebar-filters');
				$('.mod-categorynav').removeClass('skin-categorynav-plp--active');
			}
			$('.plp-content__nav-filters').removeClass('hidden');

		},

		//Products per page
		bindChangeEventListener: function(ev){
			var self = this,
				selectedValue = $(ev.target).find(":selected").val(),
				$activePage = this.$$('.js-page-link.active'),
				pageNr = $activePage.data('page-nr');

			this.fire('paginationChange', {page: pageNr, pageSize: selectedValue }, ['myaccount']);
			this.fire('paginationChange', {page: pageNr, pageSize: selectedValue }, ['productlist']);

            sessionStorage.setItem('PlpProductListRefreshfrom', 'pagesize');
		},

		// Pagination
		bindSelectPage: function(ev){
			ev.preventDefault();

			var self = this,
				$clickedLink = $(ev.delegateTarget),
				pageNr = $clickedLink.data('page-nr'),
				selectedValue = this.$$(".js-page-size :selected").val();

			if ($('.skin-layout-account').length) {
			    var $theForm = $('.mod-account-list-filter form');
			    
			    if ($theForm.length === 1) {
			    	if (!$theForm.is('#quotationHistoryForm')) {pageNr--;} // Quotation page is 1-based, others 0-based
			    	$theForm.find('input#page').val(pageNr);
			    	$theForm.find('input#pageSize').val(selectedValue);
			    	$theForm.submit();
			    } else {
			    	$clickedLink.unbind('click').click();
			    }

			} else {
				this.fire('paginationChange', {page: pageNr, pageSize: selectedValue }, ['myaccount']);
				this.fire('paginationChange', {page: pageNr, pageSize: selectedValue }, ['productlist']);
	
				window.scrollTo(0, 0);
			}

            sessionStorage.setItem('PlpProductListRefreshfrom', 'pagination');
		},

		onPaginationChangeCallback: function (data) {
			var paginationTemp = "{{? it.showPrevButton == \"true\" }}" +
                "<a href=\"{{= it.prevUrl}}\" class=\"btn btn-left js-page-link\" data-page-nr=\"{{= it.currentPage - 1}}\"><i></i></a>" +
                "{{?}}\n" +
                "<ul class=\"pagination\">" +
                "{{? it.numberOfPages <= 5}}" +
                "{{~it.pages :item:id}}" +
                "<li><a href=\"#\" class =\"js-page-link{{? item.pageNr == it.currentPage}} active{{?}}\" data-page-nr=\"{{= item.pageNr}}\">{{= item.pageNr}}</a></li>" +
                "{{~}}" +
                "{{?}}" +
                "{{? it.numberOfPages > 5}}" +
                "{{? it.currentPage <= 3 }}" +
                "{{~it.pages :item:id}}" +
                "{{? item.pageNr <= 4}}" +
                "<li><a href=\"#\" class =\"js-page-link{{? item.pageNr == it.currentPage}} active{{?}}\" data-page-nr=\"{{= item.pageNr}}\">{{= item.pageNr}}</a></li>" +
                "{{?}}" +
                "{{~}}" +
                "<li class=\"dots\">...</li>" +
                "<li><a href=\"{{= it.lastUrl}}\" data-page-nr=\"{{= it.numberOfPages}}\" class =\"js-page-link\">{{= it.numberOfPages}}</a></li>" +
                "{{?? it.currentPage >= it.numberOfPages - 2 }}" +
                "<li><a href=\"{{= it.firstUrl}}\" data-page-nr=\"1\" class=\"js-page-link\">1</a></li>" +
                "<li class=\"dots\">...</li>" +
                "{{~it.pages :item:id}}" +
                "{{? item.pageNr >= it.numberOfPages - 3}}" +
                "<li><a href=\"#\" class =\"js-page-link{{? item.pageNr == it.currentPage}} active{{?}}\" data-page-nr=\"{{= item.pageNr}}\">{{= item.pageNr}}</a></li>" +
                "{{?}}" +
                "{{~}}" +
                "{{??}}" +
                "{{? it.numberOfPages > 6}}\n" +
                "<li><a href=\"{{= it.firstUrl}}\" data-page-nr=\"1\" class =\"js-page-link\">1</a></li>" +
                "<li class=\"dots\">...</li>\n" +
                "{{~it.pages :item:id}}" +
                "{{? item.pageNr >= it.currentPage - 1 && item.pageNr <= it.currentPage + 1}}" +
                "<li><a href=\"#\" class =\"js-page-link{{? item.pageNr == it.currentPage}} active{{?}}\" data-page-nr=\"{{= item.pageNr}}\">{{= item.pageNr}}</a></li>\n" +
                "{{?}}\n" +
                "{{~}}\n" +
                "<li class=\"dots\">...</li>" +
                "<li><a href=\"{{= it.lastUrl}}\" data-page-nr=\"{{= it.numberOfPages}}\" class =\"js-page-link\">{{= it.numberOfPages}}</a></li>\n" +
                "{{?}}" +
                "{{?}}" +
                "{{?}}" +
                "</ul>" +
                "{{? it.showNextButton == \"true\" }}\n" +
                "<a href=\"{{= it.nextUrl}}\" class=\"btn btn-right js-page-link\" data-page-nr=\"{{= it.currentPage + 1}}\"><i></i></a>" +
                "{{?}}",
				paginationTemplate = doT.template(paginationTemp);
			var $pagination = $(paginationTemplate(data.pagination));

			this.$$('.pagination-wrapper').empty().append($pagination);

			this.$$('.js-page-link').on('click', this.bindSelectPage);

		},

		onPaginationWindowsHistoryPopStateEvent: function (popstateEvent) {
			var loadProductsForPaginationSearch = {};

			// get previous page and pageSize from history
			var url = window.location.href,
				urlObject = Tc.Utils.splitUrl(url),
				pageNr = urlObject.get.page,
				pageSize = urlObject.get.pageSize;

			this.$$('.ajax-action-overlay').show();

			loadProductsForPaginationSearch.isPopstateEvent = true;
			loadProductsForPaginationSearch.page = pageNr;
			loadProductsForPaginationSearch.pageSize = pageSize;

			this.fire('paginationChange', loadProductsForPaginationSearch, ['productlist']);
		},

		/**
		 * Hook function to trigger your events.
		 *
		 * @method after
		 * @return void
		 */
		after: function() {
			// initialize addWindowPopstateEvent
			this.fire('addWindowPopstateEvent', ['windowHistoryEvents']);
			
			//DISTRELEC-10345: Hide manufacturerTopPosition div, if it doesn't contain anything.
			if ($('.manufacturerTopPosition').children().length === 0){
				$('.manufacturerTopPosition').remove();
			}
			
			
		}

	});

})(Tc.$);
