(function ($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class ProductlistSwitch
	 * @extends Tc.Module
	 */
	Tc.Module.ProductlistSwitch = Tc.Module.extend({

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

			this.$btnSwitch = $('.btn-switch', $ctx);

			this.onSwitchListViewTypeButtonClick = $.proxy(this, 'onSwitchListViewTypeButtonClick');
			this.onProductListSwitchCallback = $.proxy(this, 'onProductListSwitchCallback');
			this.onLoadProductsForFacetSearchCallback = $.proxy(this, 'onLoadProductsForFacetSearchCallback');

			// call base constructor
			this._super($ctx, sandbox, id);

			this.sandbox.subscribe('productlist', this);
			this.sandbox.subscribe('facetActions', this); // for updating url
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function (callback) {
			var $ctx = this.$ctx,
				self = this;

			self.$btnSwitch.on('click', self.onSwitchListViewTypeButtonClick);

			callback();
		},

		onSwitchListViewTypeButtonClick: function (ev) {
			var clickedButton = $(ev.target).closest('button')
				,useTechnicalView = false
				,useStandardView = false
				,useIconView = false
				,mod = this
			;

			// standard & technical view: Not rendered by ajax any more; reloading entire page. 
			if(!clickedButton.hasClass('active')){
				
				clickedButton.addClass('active').siblings('.btn').removeClass('active');

				if (clickedButton.hasClass('btn-switch-technical')) {
					useTechnicalView = true;
				}
				if (clickedButton.hasClass('btn-switch-standard')) {
					useStandardView = true;
				}
				if (clickedButton.hasClass('btn-switch-icon')) {
					useIconView = true;
				}				

				mod.$btnSwitch.addClass('disabled').attr('disabled', 'disabled');

				var  $backenddata = $('#backenddata');
				
				$('.shopsettings', $backenddata).data('use-technical-view', useTechnicalView);
				$('.shopsettings', $backenddata).data('use-standard-view', useStandardView);
				$('.shopsettings', $backenddata).data('use-icon-view', useIconView);

				this.fire('productListSwitch', { currentUrl: this.$ctx.data('current-url'), 
						useTechnicalView: useTechnicalView,
						useStandardView: useStandardView,
						useIconView: useIconView
						}, 
						['productlist']);
				//location.reload();
			}


		},

		onProductListSwitchCallback: function (data) {
			this.$btnSwitch.removeClass('disabled').removeAttr('disabled');
		},

		// store new url since url in browser bar is not getting updated in IE8/IE9
		onLoadProductsForFacetSearchCallback: function (data) {

			if (data.status) {
				this.$ctx.data('current-url', data.currentQueryUrl);
			}
			this.$btnSwitch.removeClass('disabled').removeAttr('disabled');
		}
	});

})(Tc.$);
