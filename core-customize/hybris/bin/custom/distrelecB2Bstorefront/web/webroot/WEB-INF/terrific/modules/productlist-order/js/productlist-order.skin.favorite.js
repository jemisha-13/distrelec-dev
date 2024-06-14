(function($) {

	/**
	 * Productlist Order Skin Favorite implementation for the module Productlist Order.
	 *
	 */
	Tc.Module.ProductlistOrder.Favorite = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {

			parent.on(callback);

			this.onToggleOrderDropdownDisabledState = $.proxy(this.onToggleOrderDropdownDisabledState, this);

			// subscribe to connector channel/s
			this.sandbox.subscribe('favoritelist', this);

			var $select = this.$ctx.find('#select-productlist-order');
			$select.off('change').on('change', this.bindChangeEventListener);
		};

		this.bindChangeEventListener = function(ev){
			ev.preventDefault();
			var selectedValue = $(ev.target).find(":selected").val()
				,url = Tc.Utils.splitUrl(document.URL)
			;

			// if there is already a sorting in the url, remove it
			var urlParts = url.base.split("/");
			if(urlParts[urlParts.length-1].toLowerCase().indexOf('asc') > -1 || urlParts[urlParts.length-1].toLowerCase().indexOf('desc') > -1){
				url.base = url.base.replace("/"+urlParts[urlParts.length-1], '');
			}

			url.base = url.base + "/" + selectedValue;
			location.href = url.base;
		};

		this.onToggleOrderDropdownDisabledState = function(data){
			var $select = this.$ctx.find('#select-productlist-order');
			var selectBox = $select.data("selectBox-selectBoxIt");

			// only use plugin method, if plugin was initialized
			if(!Modernizr.touch && ! Modernizr.isie7 && ! Modernizr.iseproc) {
				if(data.disabled){
					selectBox.disable();
				}
				else{
					selectBox.disable();
				}
			}
			else{
				if(data.disabled){
					$select.attr('disabled', 'disabled').addClass('disabled');
				}
				else{
					$select.removeAttr('disabled').removeClass('disabled');
				}
			}

		};

	};

})(Tc.$);
