(function ($) {

	/**
	 * Review Skin implementation for the module CartList.
	 *
	 * @author Céline Müller
	 * @namespace Tc.Module.CartList
	 * @class Basic
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.CartList.Review = function (parent) {

		this.on = function (callback) {
			
			var self = this,
				$ctx = this.$ctx;
			
			//DISTRELEC-8277
			var $articles = $('.skin-cart-list-item-review.row');
			var $articlesNotBom = $('.skin-cart-list-item-review.row').not('.bom').not('.sub-item');
			var $notAvailables = $('.is-not-available');
			
			if($articlesNotBom.length === $notAvailables.length ){
				
				//hide availability information of all products
				$articles.find('.cell-availability').find('table').css('visibility', 'hidden');
				
				//hide 'Availability' header
				$('.cart-list-header').find('.cell-availability').css('visibility', 'hidden');
			}

			parent.on(callback);
		};		
		
	};

})(Tc.$);
