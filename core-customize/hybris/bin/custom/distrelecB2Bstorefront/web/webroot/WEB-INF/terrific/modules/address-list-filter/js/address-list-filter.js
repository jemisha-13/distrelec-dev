(function($) {

	Tc.Module.AddressListFilter = Tc.Module.extend({

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

			// subscribe to connector channel/s
			this.sandbox.subscribe('addressListShipping', this);
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
			this.$$('.address-list-filter').on('keydown', function(ev){
                setTimeout(function () {
                    var filterTerm = $(ev.target).val();

                    if(filterTerm === ''){
                        mod.$$('.filter-icon.search').removeClass('hidden');
                        mod.$$('.filter-icon.clear').addClass('hidden');

                        mod.fire('addressFilterClear', { }, ['addressListShipping']);
                    }
                    else if(ev.keyCode == 27){
                        // esc key, clear filter
                        $(ev.target).val('');

                        mod.fire('addressFilterClear', { }, ['addressListShipping']);
                    }
                    else if(!ev.altKey && !ev.ctrlKey && !ev.shiftKey){
                        mod.$$('.filter-icon.search').addClass('hidden');
                        mod.$$('.filter-icon.clear').removeClass('hidden');

                        mod.fire('addressFilterTermChange', { filterTerm: filterTerm }, ['addressListShipping']);
                    }
                }, 10);
			});

			this.$$('.filter-icon.clear').on('click', function(ev){
				mod.$$('.address-list-filter').val('');
				mod.$$('.filter-icon.search').removeClass('hidden');
				mod.$$('.filter-icon.clear').addClass('hidden');
                mod.fire('addressFilterClear', { }, ['addressListShipping']);
			});

			callback();
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
