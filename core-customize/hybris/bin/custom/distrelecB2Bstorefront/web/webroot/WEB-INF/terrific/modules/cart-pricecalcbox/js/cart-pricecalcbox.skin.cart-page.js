(function($) {
	Tc.Module.CartPricecalcbox.CartPage = function (parent) {

		this.on = function (callback) {

			var mod = this
				,shopSettings = $('#backenddata .shopsettings').data()
				,usersettings = $('#backenddata .usersettings')
				,usersettingsData = $('#backenddata .usersettings').data()
				,ociAribaType = ""
			;

			if (usersettings.length > '0' && usersettingsData.role === 'oci') {
				ociAribaType = "oci";
			}
			else if (usersettings.length > '0' && usersettingsData.role === 'ariba') {
				ociAribaType = "ariba";
			}

			if(ociAribaType !== ""){
				this.$$('.btn-checkout').on('click', function(ev) {
					// event goes to cart-list module which gathers the data
					mod.fire('checkoutOCIAribaClick');
				});
			}

			$('.calc-box-total-btn .btn-holder').click(function(){
				$(this).toggleClass('active');
				$('.calc-box.calc-box-total').toggleClass('active');
				$('.calc-box.calc-box-subtotal').toggleClass('active');
				$('.calc-box.calc-box-details').toggleClass('active');
				$('.voucher-holder').toggleClass('active');
			});

			parent.on(callback);

		};
	};

})(Tc.$);

