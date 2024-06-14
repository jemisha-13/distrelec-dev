(function($) {

	/**
	 * Facet Skin implementation for the module Categorynav.
	 *
	 * @author Remo Brunschwiler
	 * @namespace Tc.Module.Default
	 * @class Basic
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.Categorynav.Facets = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {
			
			var $ctx = this.$ctx;

			this.expandedCategories = 0;
			this.totalCategories = parseInt($('.categories-wrapper', $ctx).data('total-categories'));

			// Subscribe to channels
			this.sandbox.subscribe('facetActions', this);

			this.onLoadProductsForFacetSearch = $.proxy(this, 'onLoadProductsForFacetSearch');
			this.onLoadProductsForFacetSearchCallback = $.proxy(this, 'onLoadProductsForFacetSearchCallback');
			
			this.onMouseOverParentCategory = $.proxy(this, 'onMouseOverParentCategory');
			this.onExpandCollapseAllClick = $.proxy(this, 'onExpandCollapseAllClick');

			this.onFilteredCategoryClick = $.proxy(this, 'onFilteredCategoryClick');

			$(function() {
				var timeoutId;

				if (timeoutId) {
					window.clearTimeout(timeoutId);
					timeoutId = null;
				}

			});

            var catNavLink = $('.skin-categorynav-facets .mod-categorynav__wrapper__item__header > a');

            catNavLink.on('click', function (e) {
                sessionStorage.removeItem('PlpProductListRefreshfrom');
            });

			parent.on(callback);
			
			
		};

		this.onLoadProductsForFacetSearch = function(data) {
			this.$$('.ajax-action-overlay').show();
		};

		this.onLoadProductsForFacetSearchCallback = function(data) {
			this.$$('.ajax-action-overlay').hide();

			if(data.categoriesCount > 0){
				var categoryNavTemplate = doT.template(this.$$('#tmpl-category-nav').html());
				var categoryHtml = categoryNavTemplate(data);

				this.$ctx.find('.category-container').empty();
				this.$ctx.find('.category-container').append(categoryHtml);
				this.$ctx.show();
			}
			else{
				this.$ctx.hide();
				this.$ctx.find('.category-container').empty();
			}
		};

		this.onFilteredCategoryClick = function(ev) {
			var $a = $(ev.target).closest('a');

			if ($a.attr('href').length > 1900) {
				ev.preventDefault();
				var urlObject = Tc.Utils.splitUrl($a.attr('href'));
				Tc.Utils.postForm(urlObject.base,urlObject.get,'POST',true);

			}

		};

	};

})(Tc.$);
