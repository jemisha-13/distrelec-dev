package com.namics.distrelec.occ.core.swagger;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Parameter(name = "lang", description = "Language", schema = @Schema(type = "string"), in = ParameterIn.QUERY)
@Parameter(name = "curr", description = "Currency", schema = @Schema(type = "string"), in = ParameterIn.QUERY)
@Parameter(name = "channel", description = "Channel", schema = @Schema(type = "string"), in = ParameterIn.QUERY)
@Parameter(name = "country", description = "Country", schema = @Schema(type = "string"), in = ParameterIn.QUERY)
@Parameter(name = "format", description = "Response format", schema = @Schema(type = "string"), in = ParameterIn.QUERY)
public @interface CommonQueryParams {
}
