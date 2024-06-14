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
	Tc.Module.Facets.ProductFinder = function (parent) {

		this.numCategoriesHidden = 3; // zero based index. 3 = 2 etc.

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {

			parent.on(callback);
		};

		// override parent event to just follow the link with a page reload
		parent.onFacetItemClick = function (ev) {
			location.href = $(ev.target).attr('href');
		};

	};

})(Tc.$);
