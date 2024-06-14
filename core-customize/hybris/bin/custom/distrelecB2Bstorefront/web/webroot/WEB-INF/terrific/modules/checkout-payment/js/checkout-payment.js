(function ($) {

    Tc.Module.CheckoutPayment = Tc.Module.extend({

        init: function ($ctx, sandbox, id) {
            this._super($ctx, sandbox, id);
        },

        on: function (callback) {

            // Add Ensighten Code Here
        	if( null !== digitalData && null !== digitalData.page && null !== digitalData.page.pageInfo && null !== digitalData.page.pageInfo.error && undefined !== digitalData.page.pageInfo.error && null !== digitalData.page.pageInfo.error.errorPageType  && digitalData.page.pageInfo.error.errorPageType === 'payment error page' )
			{
				Bootstrapper.ensEvent.trigger("payment error");
			}
            callback();

        }

    });

})(Tc.$);
