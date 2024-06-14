(function($) {

	/**
	 * Productlist plpHelpPrompts Skin implementation for the module Productlist plp help prompts.
	 *
	 */
	Tc.Module.Productlist.Structure = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {

			parent.on(callback);

            var self = this;

            $('.facet-item.-type-checkbox').click(function(){
                $(this).toggleClass('activeFilter');
                self.dataLayerToggles($(this));
            });


            this.checkUrl(window.location.href);

		};

		this.checkUrl = function(url) {
		    if(url.indexOf('&pageSize') !== -1 || url.indexOf('?pageSize') !== -1) {
                $('meta[name=robots]').attr("content", "noindex nofollow");
            }
        };

		this.dataLayerToggles = function(e) {
            if (e.data('filter-string').indexOf('_InStock') !== -1) {
               this.addToFilter(e, e.data('filter-string'));
            } else {
                this.callTranslationService(e);
            }
        };

		this.callTranslationService = function(e) {
            var me = this;

		    $.ajax({
                url: '/classification/translation',
                type: 'GET',
                dataType: "json",
                data: {
                    facetGroupName: e.data('facet-group-name'),
                    facetValueName: e.data('facet-value-name')
                },
                contentType: "application/json",
                success: function (data) {
                    var filterString = "filter_" + data.facetGroupName + "=" + data.facetValueName;
                    me.addToFilter(e, filterString);
                }
            });
        };

        this.addToFilter = function(e, filterString) {
            if (e.hasClass('activeFilter')) {
                if (digitalData.page.pageInfo.filtersAdded.indexOf(filterString) === -1 ) {
                    digitalData.page.pageInfo.filtersAdded.push(filterString);
                }
            } else {
                if (digitalData.page.pageInfo.filtersRemoved.indexOf(filterString) === -1 ) {
                    digitalData.page.pageInfo.filtersRemoved.push(filterString);
                }
            }
        };

		this.plpProductListScrollToTop = function (_ele) {

            $("html, body").stop().animate({
                scrollTop: _ele.offset().top - 100
            }, 500);

        };

	};

})(Tc.$);
