package com.namics.distrelec.b2b.core.interceptor.util;

import com.namics.distrelec.b2b.core.interceptor.exceptions.DistValidationInterceptorException;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ValidationUtil {

    private ValidationUtil() {
    }

    public static void ifAnyElementIsNullThrowValidationException(List<String> list, Supplier<String> messageSupplier) throws DistValidationInterceptorException{
        if (list.stream().anyMatch(Objects::isNull)) {
            throw new DistValidationInterceptorException(messageSupplier.get());
        }
    }

    public static void ifAnyRegexIsInvalidThrowValidationException(List<String> list, Function<String, String> messageFunction) throws DistValidationInterceptorException {
        for (String element : list) {
            if (isRegexInvalid(element)) {
                throw new DistValidationInterceptorException(messageFunction.apply(element));
            }
        }
    }

    private static boolean isRegexInvalid(String regex) {
        try {
            Pattern.compile(regex);
        } catch (PatternSyntaxException ex) {
            return true;
        }
        return false;
    }


}
