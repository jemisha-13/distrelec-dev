(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class DetailTabs
	 * @extends Tc.Module
	 */
	Tc.Module.DetailTabs = Tc.Module.extend({

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

			this.sandbox.subscribe('anchorLinks', this);
			
			this.$downloadTab = $('.info-tab_download', $ctx);

			this.onAnchorClickEvent = $.proxy(this.onAnchorClickEvent, this);
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
			
			var productCode = self.$downloadTab.data('product-code');

			self.$downloadTab.off('click').on('click', function (data) {
				self.fire('downloadTabClicked', { productCode: productCode }, ['anchorLinks']);
			});

			$('.print-page').click(function (e){
                e.preventDefault();

                // show print dialog
                window.print();
			});

			$('.error-link').click(function (){
				$('.mod-error-feedback .error-report').addClass('pdp-report');
			});

			self.initTabs();
			self.checkAnchorCall(hash);
			
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
		 * Open tab content if this is selected by hash
		 *
		 * @method checkAnchorCall
		 * @return {*}
		 */
		checkAnchorCall: function (hash) {
			if (hash !== undefined && hash !== '') {
				var $this=$('.nav-tabs a[href=#'+hash+']');
				$this.tab('show') ;
				if($this.data('load') && $this.data('load')!=='') {
					$('div.section-info.'+$this.data('load')+' a.btn-show-more').click();
					$this.data('load',false);
				}
			}

			return this;
		},

		initTabs: function() {
			var self = this
				,$tabs = self.$$('.info-tabs a').not('.product-family')
				,isATabSelected = false
				;
			self.$tabs = $tabs;

			// start tabs and handle clicks
			$tabs.tab().on('click', function(ev) {
				var $this = $(this)
					,tabIndex = $this.parent().index()
					;
				ev.preventDefault();
				ev.stopImmediatePropagation();
				if ($this.data('disabled')) {
					return;
				}
				self.$tabs.eq(tabIndex).tab('show');
				if($this.data('load') && $this.data('load')!=='') {
					$('div.section-info.'+$this.data('load')+' a.btn-show-more').click();
					$this.data('load',false);
				}
			});
		}

	});
})(Tc.$);
