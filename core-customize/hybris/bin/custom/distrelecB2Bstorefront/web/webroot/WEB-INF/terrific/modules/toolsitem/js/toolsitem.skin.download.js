(function($) {

	/**
	 * Download Skin implementation for the module Toolsitem.
	 *
	 * @author Remo Brunschwiler
	 * @namespace Tc.Module.Default
	 * @class Basic
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.Toolsitem.Download = function (parent) {

		this.on = function (callback) {
			var self = this;
			this.onClickIcoDownload = $.proxy(this, 'onClickIcoDownload');
			this.$ctx.on('click', '.ico-download', this.onClickIcoDownload);


			// special treatment for share and print tools (#DISTRELEC-1804)
			this.$icoPopoverSpecial = $('.ico.popover-special', this.$ctx);
			this.$icoPopoverSpecial.popoverFromHtml({
				placement: 'top'
			}).on('click.toolsitemDownload', function(e) {
				e.preventDefault();
			});

			// document delegate click handler: close popover unless click target matches
			$(document).on('click.documentToolsitemDownload', function(ev) {
				if (!$(self.$icoPopoverSpecial).is(ev.target) && $(self.$icoPopoverSpecial).has(ev.target).length === 0 && $('.popover').has(ev.target).length === 0) {
					$(self.$icoPopoverSpecial).popover('hide');

					return;
				}
			});


			// calling parent method
			parent.on(callback);
		};

		this.after = function () {
			// calling parent method
			parent.after();

		};

	};

})(Tc.$);
