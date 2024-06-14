(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Toolsitem
	 * @extends Tc.Module
	 */
	Tc.Module.Toolsitem = Tc.Module.extend({

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
			this.$icoPopover = $('.ico.popover-toggle,.ico.popover-hover', $ctx);
			this.$icoToggle = $('.ico.ico-toggle', $ctx);
			this.$icoPopoverHover = $('.ico.popover-hover', $ctx);
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

			self.$icoPopover.each(function () {
				$(this).popoverFromHtml({
					placement: 'top'
				}).click(function(e) {
						e.preventDefault();
				});
			});

			self.$icoPopoverHover.each(function () {
				$(this)
					.popover({
						placement: 'top'
					})
					.popover('toggle')
					.off('click')
					.on('click', function (e) {
						$(this).popover('show');
					});
			});


			self.$icoToggle.off('click.Toolsitem').on('click.Toolsitem', function (e) {
				e.preventDefault();
				$(this).toggleClass('active');
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
			// Do stuff here or remove after method
			//...
		}

	});


	// helper: use data-content-id for popover content
	$.fn.popoverFromHtml = function(options) {
		var $item = $(this);
		
		var titleSaved = $item[0].title;
		
		options.content = $('#' + $item.data('content-id')).html();
		options.html = true;
		
		$item.popover(options);
		
		//DISTRELEC-4309. popover function removes the title
		this[0].title = titleSaved;

		return this;
	};

})(Tc.$);
