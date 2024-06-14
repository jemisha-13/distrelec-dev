(function($) {

	/**
	 * Productlistpage Skin Search Filter implementation for the module Productlist Search.
	 *
	 */
	Tc.Module.Facets.PlpFilterSearch = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */

         this.on = function (callback) {

			parent.on(callback);
		};

        var  $showHideFiltersBtn = $('.productlistpage__show-hide-filter'),
        $showFiltersText = $showHideFiltersBtn.data('show-text'),
        $hideFiltersText = $showHideFiltersBtn.data('hide-text');

        function setFilterShowHideStatus() {

            var $appliedFilterLength = $('.plp-filter-controllbar__applied-filter-item').length,
                $isFlitersHide = $('.productlistpage__filter-view-search').hasClass('hide'),
                $isFliterControlBar = $('.productlistpage__filter-action-bar');

            if ( $appliedFilterLength < 1 && $isFlitersHide) {
                $isFliterControlBar.addClass('hidden');
            } else {
                $isFliterControlBar.removeClass('hidden');
            }

        }

        $showHideFiltersBtn.on('click', function (e,b) {

            if ( $('.productlistpage__filter-view-search').hasClass('hide') ) {
                $(this).html($hideFiltersText);
                $(this).attr('data-aalinktext', 'minimise filters');
                $(this).attr('data-aainteraction', 'minimise filters');
                $('.productlistpage__filter-view-search').removeClass('hide');
                localStorage.setItem("PlpFliterViewSearch", "true");
            } else {
                $(this).html($showFiltersText);
                $(this).attr('data-aalinktext', 'show filters');
                $(this).attr('data-aainteraction', 'show filters');
                $('.productlistpage__filter-view-search').addClass('hide');
                localStorage.setItem("PlpFliterViewSearch", "false");
            }

           setFilterShowHideStatus();

        });
        
        $('.facet-group__title').on('click', function(){

            $(this).next(".facet-group__list").toggleClass('facet-group__list--active');
            $(this).toggleClass('facet-group__title--active');

        });

        $('.mod-categorynav__wrapper__title').on('click', function(){
            $(this).parent().toggleClass('skin-categorynav-plp--active');
        });

        $('.facet-show-more').on('click', function(e){

            $(this).parent().siblings(".plp-filter-search-facets__filter-facet-list").toggleClass('plp-filter-search-facets__filter-facet-list--active');

            if( $(this).children('.sidebar-show-less').hasClass('hidden')){
                $(this).children('.sidebar-show-less').removeClass('hidden');
                $(this).children('.sidebar-show-more').addClass('hidden');
            } else {
                $(this).children('.sidebar-show-less').addClass('hidden');
                $(this).children('.sidebar-show-more').removeClass('hidden');
            }

        });

	};

})(Tc.$);
