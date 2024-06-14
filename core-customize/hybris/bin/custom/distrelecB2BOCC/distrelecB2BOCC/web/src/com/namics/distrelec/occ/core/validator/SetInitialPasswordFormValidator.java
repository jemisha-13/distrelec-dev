package com.namics.distrelec.occ.core.validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.namics.distrelec.occ.core.v2.forms.SetInitialPasswordForm;

public class SetInitialPasswordFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return SetInitialPasswordForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        final SetInitialPasswordForm setInitialPasswordForm = (SetInitialPasswordForm) target;

        final String pwd = setInitialPasswordForm.getPassword();
        boolean passwordInvalid = StringUtils.isBlank(pwd) || StringUtils.length(pwd) < 6 || StringUtils.length(pwd) > 255;
        if (passwordInvalid) {
            errors.rejectValue("password", "updatePwd.pwd.invalid");
        }

        final String checkPwd = setInitialPasswordForm.getCheckPassword();
        if (StringUtils.isBlank(checkPwd)) {
            errors.rejectValue("checkPassword", "updatePwd.checkPwd.invalid");
        }

        if (!passwordInvalid && StringUtils.isNotBlank(checkPwd) && !StringUtils.equals(pwd, checkPwd)) {
            errors.rejectValue("checkPassword", "validation.checkPwd.equals");
        }

        if (StringUtils.isBlank(setInitialPasswordForm.getToken())) {
            errors.rejectValue("token", "setInitialPwd.token.invalid");
        }
    }
}
