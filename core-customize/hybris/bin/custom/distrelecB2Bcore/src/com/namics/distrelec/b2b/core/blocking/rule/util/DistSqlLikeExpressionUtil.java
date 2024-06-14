package com.namics.distrelec.b2b.core.blocking.rule.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static java.lang.Boolean.FALSE;

public final class DistSqlLikeExpressionUtil {

    private static final Logger LOG = LoggerFactory.getLogger(DistSqlLikeExpressionUtil.class);

    private DistSqlLikeExpressionUtil() {}

    public static boolean isValueBlocked(String expression, String value) {
        String trimmedValue = value != null ? value.trim() : null;
        if (isWildcardIncluded(expression) && StringUtils.isNotEmpty(trimmedValue)) {
            return Optional.of(expression)
                           .map(DistSqlLikeExpressionUtil::getPattern)
                           .map(p -> p.matcher(trimmedValue))
                           .map(Matcher::find)
                           .orElse(FALSE);
        }
        return Objects.equals(expression, trimmedValue);
    }

    private static boolean isWildcardIncluded(String expression) {
        return expression != null && expression.contains(".*");
    }

    private static Pattern getPattern(String regex) {
        try {
            return Pattern.compile(regex);
        } catch (PatternSyntaxException ex) {
            LOG.warn("Blocking regex could not be compiled to pattern for string {}", regex);
            return null;
        }
    }
}
