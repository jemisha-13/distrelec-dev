(function($) {

    /**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @extends Tc.Module
	 */
    Tc.Module.UserChannelToggle = Tc.Module.extend({

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

            // define variables
            this.userState = '';
            this.userLoggedIn = false;
            this.currentCountry = $('#backenddata .shopsettings').data('country');

            // find elements
            this.toggle = $('.js-toggle');
            this.label = $('.js-label');
            this.indicatorLeft = $('.js-indicator__left');
            this.indicatorRight = $('.js-indicator__right');

            // set the toggle texts
            this.businessText = this.label.data('business');
            this.personalText = this.label.data('personal');

            // call function to setup the initial state of the toggle
            this.setInitialState();
        },

        /**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
        on: function(callback) {
            var mod = this;

            // when user is logged in the toggle is static and displays the relevant channel
            if(this.toggle.hasClass('active')) {
                this.toggle.click(function() {
                    // toggle the userState
                    mod.userState = (mod.userState === 'B2B') ? 'B2C' : 'B2B';
    
                    // toggle styling and text based on selection
                    mod.toggle.toggleClass('personal');
                    mod.indicatorLeft.toggleClass('hidden');
                    mod.indicatorRight.toggleClass('hidden');
    
                    if(mod.userState === 'B2B') {
                        mod.label.text(mod.businessText);
                    }
                    else {
                        mod.label.text(mod.personalText);
                    }
    
                    // update select channel value and submit shopsettings form
                    $('#select-channel').val(mod.userState);

                    // when toggle has been selected on export shop use save button logic for alternative endpoint
                    // otherwise submit form as usual
                    if(mod.currentCountry === 'EX') {
                        $('.js-toggle-hook').click();
                    }
                    else {
                        $('#command').submit();
                    }
                });
            }

            callback();
        },

        /**
         * Function to set the initial state of the toggle button
         * 
         * @method setInitialState
         * @return void
         */
        setInitialState: function() {
            // get initial channel and logged in state
            this.isoCode = $('#backenddata .shopsettings').data('country');
            this.userState = $('#backenddata .shopsettings').data('channel');
            this.userLoggedIn = $('#backenddata .usersettings').data('login');            

            // if user is logged in hide indicators and make button static. show indicators if user not logged in
            if(this.isoCode === 'FR') {
                this.toggle.removeClass('active');
                this.indicatorLeft.addClass('hidden');
                this.indicatorRight.addClass('hidden');  
            }
            else if(this.userLoggedIn !== true) {
                this.toggle.addClass('active');

                if(this.userState === 'B2B') {
                    this.indicatorLeft.removeClass('hidden');
                    this.indicatorRight.addClass('hidden');
                }
                else {
                    this.indicatorLeft.addClass('hidden');
                    this.indicatorRight.removeClass('hidden');
                }
            }
            else {
                this.toggle.removeClass('active');
                this.indicatorLeft.addClass('hidden');
                this.indicatorRight.addClass('hidden');   
            }

            this.toggle.removeClass('hidden');

            // based on user state set styling/ text for button
            if (this.userState === 'B2B') {
                this.toggle.removeClass('personal');
                this.label.text(this.businessText);
            }
            else {
                this.toggle.addClass('personal');
                this.label.text(this.personalText);
            }
        }
    });

})(Tc.$);