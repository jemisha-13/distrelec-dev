(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Metahd
	 * @extends Tc.Module
	 */
	Tc.Module.Metahd = Tc.Module.extend({
		compareHiddenClass : 'compare-hidden',

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
			this.sandbox.subscribe('stickyHeader', this);
			this.sandbox.subscribe('metaHDCompare', this);

			this.isFirstClick = true;
			this.lastClickedElement = "";
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function(callback) {
			var $ctx = this.$ctx,
				self = this;

			// Close Button Click on Meta HD Popovers
			$ctx.on('click', '.btn-close', $.proxy(this, 'closeFlyout'));

			if(Modernizr.touch){
				$ctx.find('.mod-metahd-item').on('click', '.menuitem', function(ev){
					var $parent = $(ev.target).closest('.mod-metahd-item');

					if(window.outerWidth >= 992)  {
						// only follow link on the second click on the same item
						if(self.isFirstClick || self.lastClickedElement[0] !== $parent[0]){
							ev.preventDefault();

							// add hover class to open the dropdown on click
							if ($parent.not('.hover')) {
								$parent.addClass('hover');
							}

							self.isFirstClick = false;
							self.lastClickedElement = $parent;
						}
					}

				});
			}


			// delegate module handler: hover metahd
			var $hoverItems = $ctx.find('.mod-metahd-item');

			$hoverItems.hoverIntent(function() {
				var $t = $(this);

				if ($t.not('.hover')) {
					$t.addClass('hover');
				}

				// When "Account" or "Cart" dropdown has been opened, close the "Main nav" and "Shop settings" dropdowns (Only one dropdown open - DISTRELEC-24252)
				if ($t.hasClass('skin-metahd-item-account') || $t.hasClass('skin-metahd-item-cart')) {
					// Close "Shop settings" dropdown (Only one dropdown open - DISTRELEC-24252)
					$('.flyout-settings').addClass('hidden');
					// Close "Main nav" dropdown (Only one dropdown open - DISTRELEC-24252)
					var $openedNavItem = $('ul.l1 > li.hover');
					$('ul.l1 > li.hover').removeClass('hover');
					$('ul.l2', $openedNavItem).addClass('hidden');
				}
			}, function() {
				var $t = $(this);

				if ($t.is('.hover')) {
					if (!$t.is('.clicked')){
						$t.removeClass('hover');
					}
				}
			});


			callback();
		},

		/**
		 * Hook function to trigger your events.
		 *
		 * @method after
		 * @return void
		 */
		after: function() {
		},

		closeFlyout: function(ev) {
			ev.preventDefault();
			this.isFirstClick = true;
			$(ev.target).closest('.mod-metahd-item').removeClass('hover');
		},

		onSetVisibleState: function(data) {
			var that = this;
			if(data.visible === true) {
				this.$ctx.find('.item-list').removeClass(that.compareHiddenClass);
			}
			else {
				this.$ctx.find('.item-list').addClass(that.compareHiddenClass);
			}
		}
	});

})(Tc.$);
