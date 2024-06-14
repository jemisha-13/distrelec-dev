(function ($) {

	/**
	 * Gallery Loupe Skin implementation for the module Toolsitem.
	 *
	 * @author Remo Brunschwiler
	 * @namespace Tc.Module.Default
	 * @class Basic
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.Toolsitem.GalleryLoupe = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {

			// subscribe to connector channel/s
			this.sandbox.subscribe('productGallery', this);

			this.onGalleryLoupeClick = $.proxy(this, 'onGalleryLoupeClick');

			var $icoGalleryLoupe = $('.ico-gallery-loupe', this.$ctx);

			$icoGalleryLoupe.on('click', this.onGalleryLoupeClick);

			// calling parent method
			parent.on(callback);
		};

		this.onGalleryLoupeClick = function (ev) {
			this.fire('galleryLoupeClick', ['productGallery']);
		};
	};

})(Tc.$);
