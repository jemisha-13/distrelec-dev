(function ($) {

	Tc.Module.Hotjar = Tc.Module.extend({

		init: function ($ctx, sandbox, id) {

			// call base constructor
			this._super($ctx, sandbox, id);

			// subscribe to connector channel/s
			this.sandbox.subscribe('hotjar', this);
		},

		on: function (callback) {
			callback();
		},

		// Hotjar

		onHotjarEvent: function(myHotjarObj) {
			// TODO
		},

		// Check if event is activated
		isActivated: function(linkId) {
			// TODO
			return true;
		}
	});

})(Tc.$);
