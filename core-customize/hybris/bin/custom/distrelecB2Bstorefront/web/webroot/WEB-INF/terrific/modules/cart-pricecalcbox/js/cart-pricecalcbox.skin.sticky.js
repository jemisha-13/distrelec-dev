(function($) {
	Tc.Module.CartPricecalcbox.Sticky = function (parent) {

		this.on = function (callback) {
			parent.on(callback);

		};

		this.after = function (callback) {
			parent.after(callback);

			// Sticky header via jQuery Plugin "waypoints"
			// Removed by DISTRELEC-6139
			/*
			this.$ctx.waypoint('sticky', {
				stuckClass: '-stuck',
				offset: 0
			});
			*/
		};

	};

})(Tc.$);

