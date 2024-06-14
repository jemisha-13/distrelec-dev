(function($) {

	/**
	 * CheckoutAddressList Skin PickupStore implementation.
	 *
	 */
	Tc.Module.AddressList.Shipping = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {

			parent.on(callback);

			this.addressObjectToFilter = [];

			this.onAddressFilterTermChange = $.proxy(this, 'onAddressFilterTermChange');
            this.onAddressFilterClear = $.proxy(this, 'onAddressFilterClear');

			this.addressesToFilter = this.$$('.address-list .mod-address');

			// subscribe to connector channel/s
			this.sandbox.subscribe('addressListShipping', this);
		};

		//
		// Event is fired after user typed a filter term into the search field
		this.onAddressFilterTermChange = function(data){

			var mod = this
				,filterTerm = data.filterTerm
				,filterTermArray = filterTerm.split(" ")
			;

			var foundMatchInWholeList = false;

			// create search object, if it was not created previously
			if(mod.addressObjectToFilter.length === 0){
				$.each(mod.addressesToFilter, function(index, address){
					var addressObject = $(address).find('.box-address').data();
					mod.addressObjectToFilter[index] = {};
					mod.addressObjectToFilter[index].concatValueString = "";
					mod.addressObjectToFilter[index].domSelector = null;
					for (var key in  addressObject) {
						if (addressObject.hasOwnProperty(key)) {
							var val = addressObject[key];
							if(key !== 'addressId'){
								mod.addressObjectToFilter[index].concatValueString += val+" ";
							}
							else{
								mod.addressObjectToFilter[index].domSelector = $(address);
							}
						}
					}
				});
			}

            $.each(mod.addressObjectToFilter, function(index, addressObject){
                var foundMatchInAddress = true;

                $.each(filterTermArray, function(index, filterTerm){
                    if (addressObject.concatValueString.toLowerCase().indexOf(filterTerm.toLowerCase()) < 0){
                        foundMatchInAddress = false;
                    }
                });

                if(foundMatchInAddress){
                    addressObject.domSelector.removeClass('hidden');
                    foundMatchInWholeList = true;
                }
                else{
                    addressObject.domSelector.addClass('hidden');
                }
            });

            if(foundMatchInWholeList){
                this.$$('.no-match').addClass('hidden');
            }
            else{
                this.$$('.no-match').removeClass('hidden');
            }

		};

        this.onAddressFilterClear = function(data){
			var mod = this;
			mod.addressesToFilter.removeClass('hidden');
			mod.$$('.no-match').addClass('hidden');
        };
	};

})(Tc.$);
