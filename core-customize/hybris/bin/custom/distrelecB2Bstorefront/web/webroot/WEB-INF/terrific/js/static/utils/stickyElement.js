
/**
 * Removes an item from an array
 *
 *  @param object {object|array}
 *  return {object|array}
 */

Tc.Utils.triggerSticky = function ($ctx) {
	// Init only for larger screens
	if (Tc.Utils.MinScreenSizeForSticky()) {
		var $stickySidebar = $('.js-sticky-sidebar', $ctx);

		if ($stickySidebar.length) {
			Tc.Utils.calculateSticky($stickySidebar);
		}
	}
};

Tc.Utils.bindSticky = function ($ctx) {
	// Init only for larger screens
	if (Tc.Utils.MinScreenSizeForSticky()) {
		var $stickySidebar = $('.js-sticky-sidebar', $ctx);

		if ($stickySidebar.length) {
			var $window = $(window);

			// Since we are adding "fixed" position, we need to add also width to element so it dont stretch
			$stickySidebar.css({
				'width': $stickySidebar.width()
			});

			$window.scroll(function() {
				Tc.Utils.calculateSticky($stickySidebar);
			});

			// Execute sticky calculation on page load after all elements are in place
			setTimeout(function () {
				Tc.Utils.calculateSticky($stickySidebar);
			}, 50);
		}
	}
};

Tc.Utils.calculateSticky = function ($stickySidebar) {
	// Init only for larger screens
	if (Tc.Utils.MinScreenSizeForSticky()) {
		var windowTop = $(window).scrollTop();
		var $stickyStart = $('.js-sticky-sidebar-start');
		var stickyStartOffset = $stickyStart.offset();
		var stickyStartOffsetTOP = stickyStartOffset.top;
		var $stickyStop = $('.js-sticky-sidebar-stop');

		// Height of main header which is fixed to top + small space 10px
		var fixedHeaderHeight = $('.js-sticky-sidebar-fixed-header').outerHeight() + 10;

		var stickySidebarHeight = $stickySidebar.outerHeight();
		var startSticky = stickyStartOffsetTOP - fixedHeaderHeight;

		// Element which indicates where sticky needs to STOP
		var stickyStopOffset = $stickyStop.offset();
		var stickyStopOffsetTOP = stickyStopOffset.top;

		// When sticky is on top (sticky is active)
		if (windowTop > startSticky) {
			// Find bottom position of sticky
			var stickyBottom = fixedHeaderHeight + stickySidebarHeight + windowTop;

			// If sticky is active and moving down
			// Else sticky should remain above STOP point
			if (stickyStopOffsetTOP > stickyBottom) {
				$stickySidebar.css({
					'position': 'fixed',
					'top': fixedHeaderHeight
				});
			} else {
				var diff = stickyBottom - stickyStopOffsetTOP;

				$stickySidebar.css({
					'position': 'fixed',
					'top': fixedHeaderHeight - diff
				});
			}

		} else {
			// When sticky is not active
			if (windowTop <= startSticky) {
				$stickySidebar.css({
					'position': 'relative',
					'top': 'auto'
				});
			}
		}
	}
}

// Helper function which returns state if sticky should be triggered or not
Tc.Utils.MinScreenSizeForSticky = function () {
	return $(window).width() > 991;
};
