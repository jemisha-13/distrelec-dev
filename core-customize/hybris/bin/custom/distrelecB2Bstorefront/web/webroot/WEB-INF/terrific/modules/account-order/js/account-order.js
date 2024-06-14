(function($) {

    /**
     * Module implementation.
     *
     * @namespace Tc.Module
     * @class AccountOrder
     * @extends Tc.Module
     */
    Tc.Module.AccountOrder = Tc.Module.extend({

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
                $select = $ctx.find('#select-account-order'),
                $pretext;

			// Lazy Load SelectBoxIt Dropdown
			if(!Modernizr.touch && !Modernizr.isie7) {
				Modernizr.load([{
					load: '/_ui/all/cache/dyn-jquery-selectboxit.min.js',
					complete: function () {
						$select.selectBoxIt({
							autoWidth : false,
							defaultText: $select.data('pretext')
						});

						$selectCustom = $ctx.find('#select-account-orderSelectBoxIt');

						$selectCustom.find('.selectboxit-pretext').remove();
						$selectCustom.append('<span class="selectboxit-pretext">' + $select.data('pretext') + '</span>');

						$pretext = $selectCustom.find('.selectboxit-pretext');

						$selectCustom.find('.selectboxit-text').css('padding-left', Tc.Utils.widthAll($pretext)+4);

						$select = $selectCustom;
					}
				}]);
			}

	        $select.on('change', this.bindChangeEventListener);

            callback();
        },

	    bindChangeEventListener: function(ev){
		    var selectedValue = $(ev.target).find(":selected").val(),
		    $theForm = $('.mod-account-list-filter form');
		    
		    if ($theForm.length === 1) {
		    	$theForm.find('input#sort').val(selectedValue);
		    	$theForm.submit();
		    } else {
			    selectedValue =  selectedValue.split(':'),
					urlObj = Tc.Utils.splitUrl(document.URL);
	
			    if (urlObj.get === undefined) {
					urlObj.get = {sort: ""};
			    } else if (urlObj.get.q === undefined) {
					urlObj.get.sort = "";
			    }
	
				urlObj.get.sort = selectedValue[0] + ':' + selectedValue[1];
	
			    location.href = Tc.Utils.joinUrl(urlObj);
		    }
	    }
    });

})(Tc.$);
