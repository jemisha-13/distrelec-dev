(function ($) {

	/**
	 * Hero Teaser Pure Module implementation.
	 *
	 * There is one carousel in this module which is synced:
	 * - the content carousel
	 *
	 * @namespace Tc.Module
	 * @class hero-teaser-pure
	 * @extends Tc.Module
	 */
	Tc.Module.HeroTeaserPure = Tc.Module.extend({

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

			this.sandbox.subscribe('heroTeaserVideo', this); // for 9.2 rotator & carousel tracking
			this.sandbox.subscribe('heroTeaserPure', this);
			this.sandbox.subscribe('lightboxVideo', this);
			
			// bind handlers to module context
			this.initDotNavigation = $.proxy(this, 'initDotNavigation');
			this.initContentCarousel = $.proxy(this, 'initContentCarousel');
			this.checkForVideoItem = $.proxy(this, 'checkForVideoItem');
			this.onDotNavigationClick = $.proxy(this, 'onDotNavigationClick');
			this.onSlideBefore = $.proxy(this, 'onSlideBefore');
			this.onVideoStarted = $.proxy(this, 'onVideoStarted');
			this.onVideoCompleted = $.proxy(this, 'onVideoCompleted');
			
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
			this.$dotWrapper = this.$$('.teaser-dot-wrapper');
			this.$teaserContent = $('.mod-hero-teaser-pure');

			this.$contentItems = this.$$('.teaser-item', this.$contentCarousel);
			this.$dotItems = this.$$('.dot', this.$dotWrapper);

			this.timeoutSeconds = this.$dotWrapper.data('timeout'); // in seconds
			// autoplay=false does not work in certain cases, that's why we just set the autoplay duration to 1hour
			this.tempTimeout = 3600000;
			this.isAutoplay = this.$dotWrapper.data('autoplay');
			this.visibleItemsCount = this.$dotWrapper.data('items-visible');
			this.$itemsDisableAutoplay = this.$$('.ico,.dot');

			this.$teaserItem = this.$$('.teaser-item');
			
			this.indexStart = 0; // Index is zero based, carousel should start on 1st item
			
			self.$btnNext = self.$$('#scroll-next');
			self.$btnPrev = self.$$('#scroll-prev');

			// Lazy Load caroufredsel
			if(!$.fn.carouFredSel){
				Modernizr.load([{
					load: '/_ui/all/cache/dyn-jquery-caroufredsel.min.js',
					complete: function () {
						self.initContentCarousel();
						self.initDotNavigation();
						self.bindCarouselCustomEvents();
					}
				}]);
			}
			
			this.adjustArrows();
			
			self.$teaserContent.waypoint(function(ev) {
				$item = $(this);
				var isVisible = self.isFullyVisible($item);
				if (!isVisible){
					self.$contentCarousel.trigger('pause', true);
				}
				else{
					self.$contentCarousel.trigger('resume');
				}
			});				
			
			this.$teaserContent.on('mouseover', function(ev){
				ev.preventDefault();
				$('.arrow_prev-teaser').css('visibility', 'visible');
				$('.arrow_next-teaser').css('visibility', 'visible');
			});
			
			this.$teaserContent.on('mouseout', function(ev){
				ev.preventDefault();
				$('.arrow_prev-teaser').css('visibility', 'hidden');
				$('.arrow_next-teaser').css('visibility', 'hidden');
			});			

			
			self.$btnPrev.on('click', function(ev) {
				ev.preventDefault();
				self.onTeaserContentPrev(ev);
			});
			
			self.$btnNext.on('click', function(ev) {
				ev.preventDefault();
				self.onTeaserContentNext(ev);
			});				

			self.$teaserItem.on('click', function(ev) {
				var youtubeId = ev.currentTarget.attributes['data-youtubeid'].nodeValue;
				if (youtubeId !== ''){
					ev.preventDefault();
					self.fire('showLightboxVideo', {
						youtubeId: youtubeId
					}, ['lightboxVideo']);
				}
			});	
		

			callback();
		},

		initContentCarousel: function () {

			var self = this;

			this.$contentCarousel.carouFredSel({
				circular: true,
				infinite: false,
				debug: true,
				onCreate: function(data) {
					var $currentItem = $(data.items[0]);
					self.checkForVideoItem($currentItem);
				},
				auto: {
					play: this.isAutoplay,
					items: 1,
					timeoutDuration: this.timeoutSeconds * 1000,
					delay: this.timeoutSeconds * 1000,
					onBefore: this.onSlideBefore,
					onAfter: function(data) {
						var $currentItem = $(data.items.visible);
						self.checkForVideoItem($currentItem);
					}
				},
				prev: {
					items: 1,
					onBefore: this.onSlideBefore,
					onAfter: function(data) {
						var $currentItem = $(data.items.visible);
						self.checkForVideoItem($currentItem);
					}
				},
				next: {
					items: 1,
					onBefore: this.onSlideBefore,
					onAfter: function(data) {
						var $currentItem = $(data.items.visible);
						self.checkForVideoItem($currentItem);
					}
				},
				items: {
					minimum: 1,
					visible: 1,
					start: this.indexStart
				},
				scroll: {
					pauseOnHover: true
				}
			});
		},
		
		
		adjustArrows: function(){
			
			var $teaserContent = $('.teaser-content');
			var $rightArrow = $('.arrow_next-teaser');
			
			if ($teaserContent.hasClass('twoThird')){
				$rightArrow.addClass('twoThirdArrow');
			}
		},
		
		
		isFullyVisible: function(elem) {
			 var off = elem.offset();
			  var et = off.top;
			  var el = off.left;
			  var eh = elem.height(); 
			  var ew = elem.width();
			  var wh = window.innerHeight;
			  var ww = window.innerWidth;
			  var wx = window.pageXOffset;
			  var wy = window.pageYOffset;
			  return (et >= wy && el >= wx && et + eh <= wh + wy && el + ew <= ww + wx);
		},		

		initDotNavigation: function() {
			var self = this;

			// bind dot navigation click
			this.$dotWrapper.on('click', 'a', this.onDotNavigationClick);

			// add id's to dot elements
			this.$dotItems.each(function(index, item) {
				$(item).removeClass('current');
				$(item)[0].id = 'teaser-' + self.id + '-' + index;

				if (index == self.indexStart) {
					$(item).addClass('current');
				}
			});
		},

		bindCarouselCustomEvents: function() {

			var self = this;

			// remove autoplay on Click
			this.$itemsDisableAutoplay.on('click', function() {
				self.$contentCarousel.trigger('configuration', { auto: { play: false } });
				self.isAutoplay = false;
			});
		},

		checkForVideoItem: function($currentItem) {
			if($currentItem.hasClass('video')){
				if($currentItem.data('video-autoplay')){
					this.$contentCarousel.trigger('configuration', { auto: { timeoutDuration: this.tempTimeout } });
					this.fire('startVideo', { videoId: $currentItem.data('video-id') }, ['heroTeaserVideo']);
				}
			}
		},

		///////////////////////////////////////////////////////////

		onDotNavigationClick: function (ev) {
			ev.preventDefault();

			var position = $(ev.target).closest('a').data('position'),
				$contentCarouselItem = this.$contentItems.eq(position-1)
				;
			this.$contentCarousel.trigger('slideTo', $contentCarouselItem);
		},

		onSlideBefore: function (data) {
			var $nextVisibleItem = $(data.items.visible),
				$currentItem = $(data.items.old),
				position = $nextVisibleItem.data('position')
				;
			
			// if the current item is a video, send a pause event
			if($currentItem.hasClass('video')){
				this.fire('pauseVideo', { videoId: $currentItem.data('video-id') }, ['heroTeaserVideo']);
			}

			$.each(this.$dotItems, function() {
				$(this).removeClass('current');
			});

			this.$dotItems.eq(position-1).addClass('current');
		},

		/**
		 * Event when the video started with playing, needed for when the user starts a non autoplay video
		 *
		 * @method onVideoStarted
		 *
		 * @param data
		 */
		onVideoStarted: function (data) {
			if(this.isAutoplay){
				this.$contentCarousel.trigger('configuration', { auto: { timeoutDuration: this.tempTimeout } });
			}
		},

		/**
		 * Event when the video completed with playing
		 *
		 * @method onVideoCompletedthis.$contentCarousel.trigger(
		 *
		 * @param data
		 */
		onVideoCompleted: function (data) {
			if(this.isAutoplay){
				this.$contentCarousel.trigger('configuration', { auto: { timeoutDuration: this.timeoutSeconds * 1000 } });
			}
		},
		

		/**
		 * Event when the user clicks on Prev
		 *
		 * @method onTeaserContentPrev
		 *
		 * @param data
		 */
		onTeaserContentPrev: function () {
			this.$contentCarousel.trigger('prev');
		},	
		
		/**
		 * Event when the user clicks on Next
		 *
		 * @method onTeaserContentNext
		 *
		 * @param data
		 */
		onTeaserContentNext: function () {
			this.$contentCarousel.trigger('next');
		}

	});

})(Tc.$);
