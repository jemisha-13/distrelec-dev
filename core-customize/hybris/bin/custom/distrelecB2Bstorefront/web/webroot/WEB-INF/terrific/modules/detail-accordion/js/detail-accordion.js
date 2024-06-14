(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Detailaccordion
	 * @extends Tc.Module
	 */
	Tc.Module.DetailAccordion = Tc.Module.extend({

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
			this.sandbox.subscribe('anchorLinks', this);

			this.animationSpeed = 500;

			this.$btnShow = $('.btn-expand-show', $ctx);
			this.$btnClose = $('.btn-expand-close', $ctx);

			this.$items = $('.detailaccordion', $ctx);

			this.trackAccordionToggleEvents = $.proxy(this.trackAccordionToggleEvents, this);
			this.onAnchorClickEvent = $.proxy(this.onAnchorClickEvent, this);
			this.checkAnchorCall = $.proxy(this.checkAnchorCall, this);
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function(callback) {
			var self = this,
				hash = document.URL.split('#')[1];

			self.accordionHandler().checkAnchorCall(hash);


			callback();
		},

		/**
		 * Listener for the anchor click fire event
		 *
		 * @method onAnchorClickEvent
		 * @return {*}
		 */
		onAnchorClickEvent: function(data){
			this.checkAnchorCall(data.hash);
		},

		/**
		 * Expand Accordeon element if this is selected by hash, otherwise use default
		 *
		 * @method checkAnchorCall
		 * @return {*}
		 */
		checkAnchorCall: function (hash) {
			if (hash !== undefined && hash !== '') {
				var detailAccordeon = this.$$('.anchor[name=' + hash + ']').closest('.detailaccordion');

				if(!detailAccordeon.hasClass('expanded')){
					this.$$('.anchor[name=' + hash + ']').siblings('.detailaccordion-header');
				}
			}
			else {
				// default: open technical and information accordions
				this.$$('.anchor[name=technical], .anchor[name=information]').siblings('.detailaccordion-header');
			}

			return this;
		},

		/**
		*
		 * Bind Handler for the accordion
		 *
		 * @method accordionHandler
		 * @returns {*}
		 */
		accordionHandler: function () {
			var self = this,
				$ctx = this.$ctx,
				checkStatus = function () {
					if ($ctx.find('.detailaccordion.expanded').length > 0) {
						self.$btnShow.hide();
						self.$btnClose.show();
					} else {
						self.$btnShow.show();
						self.$btnClose.hide();
					}
				};

			self.$items.off('click').on('click', '.detailaccordion-header', function () {

				var $head = $(this),
					$item = $head.parent('.detailaccordion');

				if ($item.hasClass('expanded')) {
					self.accordionHide($item, checkStatus);
				} else {
					self.accordionShow($item, checkStatus);
				}

			});

			self.$btnShow.off('click').on('click', function (ev) {



				self.$items.each(function () {
					self.accordionShow($(this));
				});
				self.$btnShow.hide();
				self.$btnClose.show();
			});

			self.$btnClose.off('click').on('click', function (ev) {
				self.$items.each(function () {
					self.accordionHide($(this));
				});
				self.$btnClose.hide();
				self.$btnShow.show();
			});

			return this;
		},

		/**
		 *
		 * show the given accordion element
		 *
		 * @param $item
		 * @param callback
		 * @returns {*}
		 */
		accordionShow: function ($item, callback) {
			var self = this,
				$content = $('.detailaccordion-content', $item),
				$inner = $content.children('.inner'),
				height = $inner.height() + parseInt($inner.css('marginTop')) + parseInt($inner.css('marginBottom'));

			if ($content.height() === 0) {
				if( Modernizr.isie7 && ! Modernizr.iseproc){
					$content.removeClass('hidden');
					$item.addClass('expanded');
					if (typeof (callback) === "function") {
						callback();
					}
				}
				else{
					$content.css({
						height: 0
					}).animate({
						height: height
					}, self.animationSpeed, function () {
						$content.removeAttr('style').css({
							overflow: 'visible'
						});
						$item.addClass('expanded');

						if (typeof (callback) === "function") {
							callback();
						}
					});
				}
			}

			return this;
		},

		/**
		 *
		 * hide the given accordion element
		 *
		 * @param $item
		 * @param callback
		 * @returns {*}
		 */
		accordionHide: function ($item, callback) {
			var self = this,
				$content = $('.detailaccordion-content', $item),
				$inner = $content.children('.inner'),
				height = $inner.height() + parseInt($inner.css('marginTop')) + parseInt($inner.css('marginBottom'));

			if ($content.height() !== 0) {
				if( Modernizr.isie7 && ! Modernizr.iseproc){
					$content.addClass('hidden');
					$item.removeClass('expanded');
					if (typeof (callback) === "function") {
						callback();
					}
				}
				else{
					$content.css({
						height: height,
						overflow: 'hidden'
					}).animate({
						height: 0
					}, self.animationSpeed, function () {
						$item.removeClass('expanded');
						if (typeof (callback) === "function") {
							callback();
						}
					});
				}
			}

			return this;
		}

	});
})(Tc.$);
