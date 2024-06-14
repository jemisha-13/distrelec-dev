(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @extends Tc.Module
	 */
	Tc.Module.FreightCost = Tc.Module.extend({

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
			this._super($ctx, sandbox, id);
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function(callback) {

			getShippingCosts();

            function getShippingCosts() {

                $.ajax({
                    url: '/shipping-costs',
                    dataType: 'html',
                    contentType: 'application/html',

                    success: function (data) {
                        var html = $.parseHTML(data);

                        if (html !== null && html.length) {
                            var $shippingCostsLink = $('.mod-freight-cost__options__link');
                            var $shippingContent = $('.mod-freight-cost__pricing .pricing-content');
                            $shippingContent.html(html);

                            $shippingCostsLink.on('click', function (ev) {
                                ev.preventDefault();
                            });

                            $shippingCostsLink.mouseover(function(ev) {
                                ev.preventDefault();
                                var toElem = ev.relatedTarget || ev.toElement;

                                if (toElem.className != 'btn'){
                                    $('.mod-freight-cost__pricing').css('visibility', 'visible');
                                }

                            });

                            $shippingCostsLink.mouseout(function(ev) {
                                ev.preventDefault();
                                var toElem = ev.relatedTarget || ev.toElement;

                                if (toElem.className != 'btn'){
                                    $('.mod-freight-cost__pricing').css('visibility', 'hidden');
                                }

                            });
                        }
                    }

                });

            }

			callback();
		}
	});

})(Tc.$);
