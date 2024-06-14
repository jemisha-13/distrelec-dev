(function ($) {

	Tc.Module.GlobalUtils = Tc.Module.extend({

		init: function ($ctx, sandbox, id) {

			this._super($ctx, sandbox, id);
		},

		on: function (callback) {

			function lazyLoadImages() {
				var observer = new IntersectionObserver(function (entries) {
					for (var x = 0; x < entries.length; x++) {
						var img = entries[x].target;

						if (entries[x].isIntersecting && !img.classList.contains("img_defer")) {
							if (img.getAttribute('data-src')) {
								img.setAttribute('src', img.getAttribute('data-src'));
							}
							observer.unobserve(img);
						}
					}
				});

				var images = [].slice.call(window.document.querySelectorAll('img'));

				for (var i1 = 0; i1 < images.length; i1++) {
				observer.observe(images[i1]);
				}
			}

			function lazyLoadBackgroundImages() {
				var lazyBackgroundObserver = new IntersectionObserver(function (entries) {
					for (var y = 0; y < entries.length; y++) {
						if (entries[y].isIntersecting) {
							entries[y].target.setAttribute('style', "background-image: url(" + entries[y].target.getAttribute('data-bg-src') + ")");
							lazyBackgroundObserver.unobserve(entries[y].target);
						}
					}
				});

				var bckrImages =  document.querySelectorAll(".lazy-background");
				for (var i2 = 0; i2 < bckrImages.length; i2++) {
					lazyBackgroundObserver.observe(bckrImages[i2]);
				}
			}

			function imgDeferrer() {
				var imgDefer = document.querySelectorAll('.img_defer');

				for (var q = 0; q < imgDefer.length; q++) {
					if (imgDefer[q].getAttribute('data-src')) {
						imgDefer[q].setAttribute('src', imgDefer[q].getAttribute('data-src'));
					}
				}
			}

			window.onload = function() {
				imgDeferrer();
				lazyLoadImages();
				lazyLoadBackgroundImages();
			};

			callback();
		}
	});

})(Tc.$);
