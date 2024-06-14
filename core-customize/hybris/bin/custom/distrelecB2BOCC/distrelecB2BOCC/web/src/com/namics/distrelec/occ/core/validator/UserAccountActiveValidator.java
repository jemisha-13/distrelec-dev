/**
 *
 */
package com.namics.distrelec.occ.core.validator;

import de.hybris.platform.commercewebservicescommons.dto.user.UserWsDTO;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.namics.distrelec.b2b.facades.user.ws.dto.RegisterB2BEmployeeWsDTO;


/**
 * @author ajinkya.patil
 *
 */
public class UserAccountActiveValidator implements Validator
{

	@Override
	public boolean supports(final Class<?> clazz)
	{
		return RegisterB2BEmployeeWsDTO.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors)
	{
		final UserWsDTO userWsDTO = (UserWsDTO) target;

		if (!userWsDTO.getOrgUnit().getActive())
		{
			errors.rejectValue("active", "httpHandlers.accountDeactivated");
		}

	}
}
