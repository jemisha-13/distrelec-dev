package com.namics.distrelec.swagger;

import java.util.List;
import java.util.Optional;

import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springdoc.core.customizers.ParameterCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.hybris.platform.swaggerintegration.config.SwaggerIntegrationConfig;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.SpecVersion;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

@Configuration
public class SwaggerIntegrationOverrideConfig {

    @Bean("distrelecOpenApiFieldsParamCustomizer")
    public ParameterCustomizer openApiFieldsParamCustomizer() {
        return (param, methodParameter) -> {
            Optional.ofNullable(methodParameter.getParameterAnnotation(ApiFieldsParam.class)).ifPresent(apiFieldsParam -> {
                Optional.ofNullable(param.getSchema()).ifPresent(schema -> schema.setDefault(apiFieldsParam.defaultValue()));
                param.description(getParamDescription(apiFieldsParam));
                param.allowReserved(true);
            });
            return param;
        };
    }

    @Bean("distrelecOpenApiCustomizer")
    public OpenApiCustomiser openApiCustomiser() {
        return openApi -> openApi.getPaths().values().stream()
                                 .filter(pathItem -> pathItem.getOptions() == null)
                                 .forEach(pathItem -> pathItem.setOptions(getOperation(pathItem)));
    }

    @Bean("distrelecOpenApiRobotsTxtCustomiser")
    public OpenApiCustomiser openApiRobotsTxtCustomiser() {
        return openApi -> {
            PathItem robotsTxtPathItem = buildRobotsTxtPath();
            openApi.getPaths()
                   .addPathItem("/{baseSiteId}/robots.txt", robotsTxtPathItem);
        };
    }

    private PathItem buildRobotsTxtPath() {
        Operation operation = new Operation();
        operation.setTags(List.of("Robots"));

        Parameter baseSiteIdParam = new Parameter();
        baseSiteIdParam.setRequired(true);
        Schema<String> schema = new Schema<>();
        schema.setType("string");
        schema.setSpecVersion(SpecVersion.V30);
        baseSiteIdParam.setSchema(schema);
        baseSiteIdParam.setIn("path");
        baseSiteIdParam.setName("baseSiteId");
        baseSiteIdParam.setDescription("Base site identifier");

        operation.setParameters(List.of(baseSiteIdParam));

        MediaType plainTextMediaType = new MediaType();
        plainTextMediaType.setSchema(schema);

        Content content = new Content();
        content.addMediaType("plain/text", plainTextMediaType);

        ApiResponse response = new ApiResponse();
        response.setContent(content);

        ApiResponses apiResponses = new ApiResponses();
        apiResponses.addApiResponse("200", response);

        operation.setResponses(apiResponses);

        PathItem robotsTxtPathItem = new PathItem();
        robotsTxtPathItem.setGet(operation);
        return robotsTxtPathItem;
    }

    private Operation getOperation(PathItem pathItem) {
        if (pathItem.getGet() != null) {
            return pathItem.getGet();
        }

        if (pathItem.getPost() != null) {
            return pathItem.getPost();
        }

        if (pathItem.getPatch() != null) {
            return pathItem.getPatch();
        }

        if (pathItem.getDelete() != null) {
            return pathItem.getDelete();
        }

        if (pathItem.getHead() != null) {
            return pathItem.getHead();
        }

        return null;
    }

    private String getParamDescription(final ApiFieldsParam apiFieldsParam) {
        return SwaggerIntegrationConfig.FIELDS_DESCRIPTION + String.join(",", apiFieldsParam.examples());
    }

}
