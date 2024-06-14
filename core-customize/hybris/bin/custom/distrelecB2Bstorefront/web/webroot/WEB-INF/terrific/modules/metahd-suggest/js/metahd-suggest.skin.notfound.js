(function($) {

	/**
	 * This module implements the auto-suggest functionality.
	 *
	 * The modules listens to 'search' jQuery events dispatched by the metahd-search module, which also provides the
	 * data needed to render the auto-suggest UI.
	 *
	 * When new data arrives it is digested in a static model.
	 *
	 * @namespace Tc.Module
	 * @class MetahdSuggest
	 * @extends Tc.Module
	 */
    Tc.Module.MetahdSuggest.Notfound = function (parent) {

        this.on = function (callback) {
        	parent.on(callback);

        	$('.search-404 .input-search').click(function (){

                $('body').addClass('search-404-overflow');

			});

        };

	};

})(Tc.$);
