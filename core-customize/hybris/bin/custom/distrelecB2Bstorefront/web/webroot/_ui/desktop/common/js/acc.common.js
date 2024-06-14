var ACC = {};

ACC.common = {
	bindAll: function() {
		ACC.common.bindToUiCarouselLink();
	},

	bindToUiCarouselLink: function() {
		$("ul.carousel > li a").colorbox();
	}
};

$(document).ready(function() {
	ACC.common.bindAll();
});
