(function ($) {

    Tc.Module.ZeroResults = Tc.Module.extend({

        init: function ($ctx, sandbox, id) {
            // call base constructor
            this._super($ctx, sandbox, id);
        },

        on: function (callback) {
            callback();
        },

        after: function () {

        }

    });

})(Tc.$);
