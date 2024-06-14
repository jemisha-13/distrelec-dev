(function($) {

	/**
	 * Remove All Skin implementation for the module Toolsitem.
	 *
	 * @author Remo Brunschwiler
	 * @namespace Tc.Module.Default
	 * @class Basic
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.Toolsitem.CompareRemoveAllPlp = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {
			// calling parent method
			parent.on(callback);

			// subscribe to connector channel/s
            this.sandbox.subscribe('metaHDCompare', this);
			this.sandbox.subscribe('comparelist', this);

			var mod = this;

            this.$ico = this.$$('.plp-compare-reset-all');

			mod.$ico.on('click.CompareListItem.Remove', function (ev) {
				ev.preventDefault();

                var _quantity = -1;

                $.ajax({
                    url: '/compare/removeAll',
                    type: 'post',
                    data: {
                    },
                    dataType: 'json',
                    success: function(response) {

                        $('.compare-list-size').text(0);
                        $('.plp-compare__compare-count').html( $('.compare-list-size').text() );
                        $('.ico-compare--plp-compare').removeClass('active');

                        mod.fire('compareChange', {
                            'compareProductsData': response.compareProductsData,
                            'quantityChange': _quantity
                        }, ['metaHDCompare']);

                        setTimeout(function(){

                            $('.skin-toolsitem-compare-popup-plp').removeClass('active');

                        }, 2000);

                    },
                    error: function(jqXHR, textStatus, errorThrown) {
                    }
                });

            });
		};
	};

})(Tc.$);
