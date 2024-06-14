(function ($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Lightbox-image
	 * @extends Tc.Module
	 */
	Tc.Module.LightboxImage = Tc.Module.extend({

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
		init: function ($ctx, sandbox, id) {
			// call base constructor
			this._super($ctx, sandbox, id);

			// subscribe to connector channel/s
			this.sandbox.subscribe('lightboxImage', this);

			// init jQuery var's
			this.$modal = $('.modal', $ctx);
			this.$modalBd = $('.bd', $ctx);
			this.$image = $('.image', this.$modalBd);
			this.$next = $('.next', this.$modalBd);
			this.$prev = $('.prev', this.$modalBd);
			this.$title = $('.title', this.$modalBd);
			this.$imagesCounter = $('.images-counter', this.$modalBd);

			// init var's

			// imageStack(:object): stack with all image attributes
			this.imageStack = {};
			// duration: animation duration
			this.duration = 500;
			// currentIndex: the current displayed image index
			this.currentIndex = 0;
			// prevNextAllow: while image is loading this variable is set to false to prevent clicking
			this.prevNextAllow = true;
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function (callback) {
			var self = this;

			self.$next.on('click', function () {
				if (self.prevNextAllow) {
					self.onShowImageNext();
				}
			});

			self.$prev.on('click', function () {
				if (self.prevNextAllow) {
					self.onShowImagePrev();
				}
			});

			self.$modal.on('hidden', function () {
				self.removeKeyEventHandler();
			});

			callback();
		},

		/**
		 *
		 * @method onOpenModal
		 *
		 * @param data
		 */
		onOpenModal: function (data) {
			this.buildImageStack(data.$content);
			this.checkNextPrevButtonState();
			this.attachKeyEventHandler();
			this.onShowImageNum(parseInt(data.$clicked.find('img').data('index')));
		},

		/**
		 *
		 * @method buildImageStack
		 *
		 * @param $content
		 * @returns {*}
		 */
		buildImageStack: function ($content) {
			var self = this,
				$img = $content.find('.images-in-lightbox');

			self.imageStack = {};

			$img.each(function (i) {

				var title = $(this).data('title'),
					subtitle = $(this).data('subtitle');

				if (title === undefined) {
					title = '';
				}

				if (subtitle === undefined) {
					subtitle = '';
				}

				self.imageStack[i] = {
					src: $(this).data('lightbox'),
					title: title,
					id: 'img-' + i,
					isMagic360: $(this).data('is-magic-360'),
					rows: $(this).data('m360-rows'),
					cols: $(this).data('m360-cols'),
					pattern: $(this).data('lightbox-pattern'),
					magnifierPath: $(this).data('lightbox-magnifier'),
					rightClick: $(this).data('m360-right-click'),
					autoSpin: $(this).data('m360-auto-spin'),
					autoSpinSpeed: $(this).data('m360-autospin-speed'),
					reverseColumn: $(this).data('m360-reverse-column'),
					speed: $(this).data('m360-speed'),
					subtitle: subtitle
				};
			});

			self.imageStack.length = $img.length;

			return this;
		},

		/**
		 *
		 * @method checkNextPrevButtonState
		 *
		 * @param index
		 * @returns {*}
		 */
		checkNextPrevButtonState: function () {
			// Hide nav Buttons if there is only one image
			if (this.imageStack.length === 1) {
				this.$prev.hide();
				this.$next.hide();
			}
		},

		/**
		 *
		 * @method attachKeyEventHandler
		 *
		 * @param index
		 * @returns {*}
		 */
		attachKeyEventHandler: function () {
			var self = this;
			this.$ctx.off('keyup').on('keyup', function (ev) {
				if (ev.keyCode === 37) {
					if (self.prevNextAllow) {
						self.onShowImagePrev();
					}
				}
				else if (ev.keyCode === 39) {
					if (self.prevNextAllow) {
						self.onShowImageNext();
					}
				}
			});
		},

		/**
		 *
		 * @method removeKeyEventHandler
		 *
		 * @param index
		 * @returns {*}
		 */
		removeKeyEventHandler: function () {
			this.$ctx.off('keyup');
		},

		/**
		 *
		 * @method onShowImageNum
		 *
		 * @param index
		 * @returns {*}
		 */
		onShowImageNum: function (index) {

			var self = this,
				obj = {},
				showModal = function () {
					if (self.$modal.css('display') === 'none') {
						self.$title.html('');
						self.$modal.modal();
					}
				},
				loadImage = function () {

					self.prevNextAllow = false;
					self.$title.html('<h2>' + obj.title + '</h2><p>' + obj.subtitle + '</p>');

					if (obj.isMagic360) {
						self.$image.html('<a class="Magic360" id="' + obj.id + '" ' +
							'data-magic360-options="' +
							'rows: ' + obj.rows + '; ' +
							'columns: ' + obj.cols + '; ' +
							'filename: ' + obj.pattern + '; ' +
							'right-click: ' + obj.rightClick + '; ' +
							'autospin:' + obj.autoSpin + '; ' +
							'autospin-speed:' + obj.autoSpinSpeed + '; ' +
							'reverse-column:' + obj.reverseColumn + '; ' +
							'speed:' + obj.speed + '; " ' +
							'href="' + obj.magnifierPath + '" ><img src="' + obj.src + '" /></a>'
						);

						Magic360.start(obj.id);
					}
					else {
						self.$image.html('<img src="' + obj.src + '" />');
					}

					self.prevNextAllow = true;

				},
				setPrevNext = function () {

					if (self.currentIndex === 0) {
						self.$prev.attr("disabled", "disabled");
						self.$next.removeAttr("disabled");
					} else if (self.currentIndex === self.imageStack.length - 1) {
						self.$next.attr("disabled", "disabled");
						self.$prev.removeAttr("disabled");
					} else {
						self.$prev.removeAttr("disabled");
						self.$next.removeAttr("disabled");
					}
				};

			if (index !== undefined && self.imageStack[index] !== undefined && self.imageStack[index].src !== undefined) {

				obj = self.imageStack[index];
				self.currentIndex = index;
				self.$imagesCounter.html('<p>' + (index + 1) + ' / ' + self.imageStack.length + '</p>');
				showModal();
				loadImage();
				setPrevNext();
			}

			return this;
		},

		/**
		 *
		 * @method onShowImageNext
		 *
		 */
		onShowImageNext: function () {
			this.onShowImageNum(this.currentIndex + 1);
		},

		/**
		 *
		 * @method onShowImagePrev
		 *
		 */
		onShowImagePrev: function () {
			this.onShowImageNum(this.currentIndex - 1);
		},

		/**
		 * Hook function to trigger your events.
		 *
		 * @method after
		 * @return void
		 */
		after: function () {
			// Do stuff here or remove after method
			//...
		}

	});

})(Tc.$);
