
	////////////////////////////////////////////////////////////////////////////////////
	//
	// Get all Form Data from a Form
	//
	// usage see:
	// 	- mod-lightbox-share-email
	//  - mod-lightbox-quotation
	//	...
	//
	////////////////////////////////////////////////////////////////////////////////////

	Tc.Utils.getFormData = function ($form) {

		var postData = {},
			$ipt = $form.find('.field'),
			$chk = $form.find('input[type=checkbox]'),
			$cpt =  $form.find('#captchaAnswer');

		// Inputfields
		$ipt.each(function () {
			var name = $(this).attr('name'),
				value = $(this).val();

			if (name !== '' && name !== undefined) {
				postData[name] = value;
			}
		});

		// Checkboxes
		$chk.each(function () {
			if ($(this).is(':checked')) {
				var name = $(this).attr('name'),
					value = $(this).val();

				if (name !== '' && name !== undefined && name !== 'undefined') {
					postData[name] = value;
				}
			}
		});

		// captcha
		postData['captchaAnswer'] = $cpt.val();


		return postData;
	};