(function ($) {

	/**
	 * Hero Teaser Module implementation.
	 * 
	 * There are two carousels in this module which are synced:
	 * - the thumbnail carousel 
	 * - the content carousel
	 * 
	 * 
	 *
	 * @namespace Tc.Module
	 * @class Product-image-gallery
	 * @extends Tc.Module
	 */
	Tc.Module.HeroTeaser = Tc.Module.extend({

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
			this.sandbox.subscribe('toolItems', this);

			// bind handlers to module context
			this.initThumbnailCarousel = $.proxy(this, 'initThumbnailCarousel');
			this.initContentCarousel = $.proxy(this, 'initContentCarousel');
			this.onThumbnailCarouselClick = $.proxy(this, 'onThumbnailCarouselClick');
			this.onSlide = $.proxy(this, 'onSlide');
			this.reqProdToggleStates = $.proxy(this, 'reqProdToggleStates');
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

			this.$contentCarousel = this.$$('.teaser-content-carousel');
			this.$thumbCarousel = this.$$('.teaser-thumbnails-carousel');

			this.$contentItems = this.$$('.mod-hero-teaser-item', this.$contentCarousel);
			this.$thumbItems = this.$$('.thumbnail', this.$thumbCarousel);

			this.timeoutSeconds = this.$thumbCarousel.data('timeout'); // in seconds
			this.isAutoplay = this.$thumbCarousel.data('autoplay');
			this.visibleItemsCount = this.$thumbCarousel.data('items-visible');
			this.$itemsDisableAutoplay = this.$$('.ico,.thumbnail,.btn-prev,.btn-next');

			this.$btnNext = self.$$('.btn-next');
			this.$btnPrev = self.$$('.btn-prev');

			this.indexCenter = (Math.round(this.visibleItemsCount / 2)) - 1; // Index is zero based

			// Lazy Load caroufredsel
			if(!$.fn.carouFredSel){
				Modernizr.load([{
					load: '/_ui/all/cache/dyn-jquery-caroufredsel.min.js',
					complete: function () {
						self.initContentCarousel();
						self.initThumbnailCarousel();
						self.bindCarouselCustomEvents();
					}
				}]);
			}

			callback();
		},

		initContentCarousel: function () {

            var self = this;

			this.reqProdToggleStates();

			this.$contentCarousel.carouFredSel({
				circular: true,
				infinite: false,
				synchronise: [this.$thumbCarousel, false], // false because we do not want all the options on our other carousel
				auto: {
					play: this.isAutoplay,
					items: 1,
					timeoutDuration: this.timeoutSeconds * 1000,
					delay: this.timeoutSeconds * 1000,
					onAfter: this.onSlide
				},
				prev: {
					button: function() {
						return self.$btnPrev;
					},
					items: 1,
					onBefore: this.onSlide
				},
				next: {
					button: function() {
						return self.$btnNext;
					},
					items: 1,
					onBefore: this.onSlide
				},
				items: {
					minimum: 1,
					visible: 1,
					start: this.indexCenter
				},
				scroll: {
					pauseOnHover: true
				}
			});
		},

		initThumbnailCarousel: function() {
			var self = this;

			// bind thumbnail carousel click
			this.$thumbCarousel.on('click', 'a', this.onThumbnailCarouselClick);

			// add id's to thumbnail elements
			this.$thumbItems.each(function(index, item) {
				$(item).removeClass('current');
				$(item)[0].id = 'teaser-' + self.id + '-' + index;

				if (index == self.indexCenter) {
					$(item).addClass('current');
				}
			});

			this.$thumbCarousel.carouFredSel({
				circular: true,
				infinite: false,
				width: "100%", // needs to be set so that items are center aligned
				align: "center",
				auto: false,
				items: {
					minimum: 1,
					visible: this.visibleItemsCount,
					start: 0
				}
			});
		},

		bindCarouselCustomEvents: function() {

			var self = this;

			// remove autoplay on Click
			this.$itemsDisableAutoplay.on('click', function() {
				self.$thumbCarousel.trigger('configuration', { auto: { play: false } });
				self.$contentCarousel.trigger('configuration', { auto: { play: false } });
			});
		},

		///////////////////////////////////////////////////////////

		onThumbnailCarouselClick: function (ev) {

			ev.preventDefault();

			var position = $(ev.target).closest('a').data('position'),
				$contentCarouselItem = this.$contentItems.eq(position-1)
			;
			this.$contentCarousel.trigger('slideTo', $contentCarouselItem);
		},

		onSlide: function (data) {

			var $visibleItem = $(data.items.visible),
				position = $visibleItem.data('position')
			;

			$.each(this.$thumbItems, function() {
				$(this).removeClass('current');
			});

			this.$thumbItems.eq(position-1).addClass('current');
		},

		///////////////////////////////////////////////////////////

		reqProdToggleStates: function () {
			var  allTeaserElements = this.$$('.teaser-thumbnails .thumbnail')
				,productCodesArray = []
				,self = this
			;

			$.each(allTeaserElements, function (index, teaserElement) {
				if ($(teaserElement).data('product-id') !== '') {
					productCodesArray[index] = $(teaserElement).data('product-id');
				}
			});

			if(productCodesArray.length > 0){
				$.ajax({
					url: '/checkToggles',
					type: 'post',
					data: {
						productCodes: productCodesArray
					},
					success: function (data, textStatus, jqXHR) {
						self.fire('updateToolItemStates', { products: data.products }, ['toolItems']);
					},
					error: function (jqXHR, textStatus, errorThrown) {
					}
				});
			}
		}

	});

})(Tc.$);
