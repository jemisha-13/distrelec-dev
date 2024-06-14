(function ($) {

	Tc.Module.MovPopUp = Tc.Module.extend({

		init: function ($ctx, sandbox, id) {
			this._super($ctx, sandbox, id);
		},

		on: function(callback) {
			// TODO FIX NON FIRING OF THIS JS
			// Bootstrapper.ensEvent.trigger("Minimum order warning");
			//
			// $(document).on('click', '.mod-mov-pop-up .mat-button', function() {
			// 	$('.mod-mov-pop-up').removeClass('mod-mov-pop-up--show');
			// });

			callback();
		}

	});

})(Tc.$);