(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Cookie-notice
	 * @extends Tc.Module
	 */
	Tc.Module.CookieNotice = Tc.Module.extend({

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
		init: function($ctx, sandbox, id) {
			// call base constructor
			this._super($ctx, sandbox, id);

			// Do stuff here
			//...
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function(callback) {
			var $ctx = this.$ctx,
				self = this;

            // Click on a Button
            $ctx.on('click', '.btn', function (e) {
                var $target = $(e.target);

                if ($target.hasClass('btn-ok')) {
                    $.ajax({
                        url: '/_s/cookieMessageConfirmed',
                        type: 'post',
                        success: function (data, textStatus, jqXHR) {
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                        }
                    });
                }
                // Anyway: Remove itself
                {
                    self.removeModule();
                    e.preventDefault();
                }
            });

			callback();
		},

		 removeModule: function() {
            var $ctx = this.$ctx,
                self = this;

            // stop and unregister the module itself
            self.sandbox.removeModules([self]);

            // remove it from the DOM
            self.$ctx.remove();

            return false;
        },

		/**
		 * Hook function to trigger your events.
		 *
		 * @method after
		 * @return void
		 */
		after: function() {
			// Do stuff here or remove after method
			//...
		}

	});

})(Tc.$);
