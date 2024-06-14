(function($) {

	/**
	 * This module implements the accordion content skin download on the product detail page
	 *
	 * @namespace Tc.Module
	 * @class DetailAccordionContent
	 * @skin Download
	 * @extends Tc.Module
	 */
	Tc.Module.DetailAccordionContent.Download = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {
			// calling parent method
			parent.on(callback);

			var $ctx = this.$ctx,
				self = this;

			this.$popoverToggle = this.$$('.popover-toggle');
			this.$popoverLanguages = this.$$('.popover-languages');

			this.$popoverToggle
				.popover({
					placement: 'bottom',
					content: this.$popoverLanguages.html(),
					html: true
				})
				.on('click', function(e) {
					e.preventDefault();
				});


			// document delegate click handler: close popover unless click target matches (#DISTRELEC-1804)
			$(document).on('click.accordiondownload', function(ev) {
				if (!$(self.$popoverToggle).is(ev.target) && $(self.$popoverToggle).has(ev.target).length === 0 && $('.popover').has(ev.target).length === 0) {
					$(self.$popoverToggle).popover('hide');

					return;
				}
			});

			callback();// ?
		};


		/**
		 * Hook function to trigger your events.
		 *
		 * @method after
		 * @return void
		 */
		this.after = function () {
			// calling parent method
			parent.after();
		};

	};

})(Tc.$);
