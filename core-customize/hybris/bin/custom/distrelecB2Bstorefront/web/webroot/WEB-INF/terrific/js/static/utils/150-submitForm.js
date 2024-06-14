/**
 * Submit form which contains radio button inputs.
 * All inputs will be disabled to not be contained in a POST request apart from the csrf attribute.
 */

Tc.Utils.disableAndSubmitForm = function(form, actionUrl) {
	form.find('label, input:not([name="_csrf"])').addClass('disabled').attr('disabled', 'disabled');
	form.attr('action', actionUrl).submit();
};
