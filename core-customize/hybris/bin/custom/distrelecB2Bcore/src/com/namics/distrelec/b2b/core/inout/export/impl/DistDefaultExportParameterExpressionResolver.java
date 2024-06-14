package com.namics.distrelec.b2b.core.inout.export.impl;

import com.namics.distrelec.b2b.core.inout.export.DistExportParameterExpressionResolver;
import de.hybris.platform.core.Registry;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class DistDefaultExportParameterExpressionResolver implements DistExportParameterExpressionResolver {

    @Override
    public Object resolve(String expressionText) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("ctx", Registry.getApplicationContext());
        Expression expression = parser.parseExpression(expressionText);
        return expression.getValue(context);
    }
}
