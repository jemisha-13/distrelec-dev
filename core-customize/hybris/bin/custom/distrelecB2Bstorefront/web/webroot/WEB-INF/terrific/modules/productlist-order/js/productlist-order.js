(function($) {

    /**
     * Module implementation.
     *
     * @namespace Tc.Module
     * @class ProductlistOrder
     * @extends Tc.Module
     */
    Tc.Module.ProductlistOrder = Tc.Module.extend({

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

	        this.bindChangeEventListener = $.proxy(this.bindChangeEventListener, this);
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
                self = this,
                $selectCustom,
                $select = $ctx.find('#select-productlist-order'),
                $pretext
			;

			$selectedItem = $ctx.find('[selected]').val();

			// Lazy Load SelectBoxIt Dropdown
			if(!Modernizr.touch && ! Modernizr.isie7 && ! Modernizr.iseproc) {
				Modernizr.load([{
					load: '/_ui/all/cache/dyn-jquery-selectboxit.min.js',
					complete: function () {
						$select.selectBoxIt({
							autoWidth : false,
							defaultText: $select.data('pretext') + $selectedItem
						});

						$selectCustom = $ctx.find('#select-productlist-orderSelectBoxIt');

                        $selectCustom.find('.selectboxit-pretext').remove();

                        if ( $('#plp-filter-product-list').length < 1  )  {

                            $selectCustom.append('<span class="selectboxit-pretext">' + $select.data('pretext') + '</span>');

                            $pretext = $selectCustom.find('.selectboxit-pretext');

                            $selectCustom.find('.selectboxit-text').css('padding-left', Tc.Utils.widthAll($pretext)+4);
                        }

					}
				}]);
			}

            $select.on('change', self.bindChangeEventListener);
            callback();

        },

	    bindChangeEventListener: function(ev){
		    var selectedValue = $(ev.target).find(":selected").val().split(':'),
			    urlObj = Tc.Utils.splitUrl(document.URL);

			// Prepare "sort" get param
		    if (urlObj.get === undefined) {
				urlObj.get = {sort: ""};
		    } else if (urlObj.get.q === undefined) {
				urlObj.get.sort = "";
		    }

			// Add "sort" and "page" get parameter
			urlObj.get.sort = selectedValue[0] + ':' + selectedValue[1];
		    urlObj.get.page = 1; // on sort event, user should be directed to first result-page

		    location.href = Tc.Utils.joinUrl(urlObj);

            sessionStorage.setItem('PlpProductListRefreshfrom', 'sort');
	    }
    });

})(Tc.$);
