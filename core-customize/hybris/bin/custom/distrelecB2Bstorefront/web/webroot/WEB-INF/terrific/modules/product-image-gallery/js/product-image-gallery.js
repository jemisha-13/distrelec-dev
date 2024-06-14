(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Product-image-gallery
	 * @extends Tc.Module
	 */
	Tc.Module.ProductImageGallery = Tc.Module.extend({

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
			this.sandbox.subscribe('lightboxImage', this);
			this.sandbox.subscribe('productGallery', this);

			// init var's
			this.initializeThumbnailElements = $.proxy(this, 'initializeThumbnailElements');
			this.onThumbnailClick = $.proxy(this, 'onThumbnailClick');// click on thumbs
			this.checkToolsVisibility = $.proxy(this, 'checkToolsVisibility');// custom use for show/hide item check
			this.onSlideAfter = $.proxy(this, 'onSlideAfter');// custom use for tools visibility
			this.onSlideBefore = $.proxy(this, 'onSlideBefore');// custom use for current active element
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function(callback) {

			var self = this;

			this.$galleryThumbnails = this.$$('.carousel-thumbnails');// div holds all thumbs images
			this.$thumbnailItems = this.$$('.item', this.$galleryThumbnails);// all thumbs
			this.centeredItemIndex = (Math.round(this.$thumbnailItems.length / 2)) - 1;// image in the center of available space has always to be the active one. in available space there can be shown a max of 5 images. index depends on available thumbs.

			this.blnMultipleImages = this.$thumbnailItems.length > 1;// set boolean true if more than 1 image is shown

			this.$galleryPrimary = this.$$('.imagery-holder');// div holds all main images, caroufredsel target
			this.$primaryItems = this.$$('.gallery-item', this.$galleryPrimary);// all images, caroufredsel target
			// 'caroufredsel' docu: http://caroufredsel.dev7studios.com/configuration.php

			this.illustrativeToolsItem = this.$$('.skin-toolsitem-information');
			this.loupeToolsItem = this.$$('.skin-toolsitem-gallery-loupe');
			this.manufacturerWrapper = this.$$('.manufacturer-wrap');
			this.promotionLabelWrapper = this.$$('.promo-label-illustrative-image-wrap');
			
			//DISTRELEC-6601
			// Automatically adjust the height of .slideshow-gallery according to scaled-prices height
			var scaledPricesDivHeight = parseInt($('.mod-scaled-prices').css('height').replace(/[^-\d\.]/g, ''));
			this.$$('.slideshow-gallery').height(scaledPricesDivHeight + 138 + 'px');

            $('.zoom-gallery .gallery-side a').on('click touch touchstart', function(e) {
                $('.zoom-gallery .zoom-gallery-slide').removeClass('active');
                $('.zoom-gallery .gallery-side a').removeClass('active');
                $('.zoom-gallery .zoom-gallery-slide[data-slide-id="'+jQuery(this).attr('data-slide-id')+'"]').addClass('active');
                $(this).addClass('active');
                e.preventDefault();
            });

            $('.MagicZoom').on('click', function() {
				window.dataLayer.push({
					event: "imageClick",
					productImage: window.digitalData.product[0].productInfo.productID
				});
			});

			self.$galleryThumbnails.css('margin', '0'); // prop was needed for FOUC, leads to wrong hor. align in chrome => delete it

			callback();
		},

		// update highlighting
		onSlideBefore: function(data) {

			var $visibleItems = $(data.items.visible)
				,$allItems = this.$$('.carousel-thumbnails .item') // dont use collection from callback because it does not contain already copied item from dom
			;

			$allItems.removeClass('current');

			var  newItem = $($visibleItems.eq(0))
				,newItemId = newItem.attr('id')
				;

			$('#' + newItemId).addClass('current');
		},

		// update Tools Visibility
		onSlideAfter: function(data) {

			var $visibleItems = $(data.items.visible);

			var  newItem = $($visibleItems.eq(0))
				,newItemIsIllustrative = newItem.hasClass('illustrative-image')
				,newItemIsVideo = newItem.hasClass('video')
			;

			this.checkToolsVisibility(newItemIsIllustrative, newItemIsVideo);
		},


		/**
		 * Hook function to trigger your events.
		 *
		 * @method after
		 * @return void
		 */
		after: function() {
			var self = this;

			if(this.$galleryPrimary.find('.gallery-item.video').length > 0){
				// load video player, plugin is loaded in document-default.tag
				brightcove.createExperiences();
			}
		}

	});

})(Tc.$);
