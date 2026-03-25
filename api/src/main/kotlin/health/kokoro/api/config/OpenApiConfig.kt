package health.kokoro.api.config

import health.kokoro.api.error.ErrorResponse
import io.swagger.v3.core.converter.ModelConverters
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springdoc.core.customizers.OperationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.HandlerMethod

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        val errorSchema = ModelConverters.getInstance()
            .readAllAsResolvedSchema(ErrorResponse::class.java).schema

        return OpenAPI()
            .components(
                Components().addSchemas("ErrorResponse", errorSchema)
            )
    }

    @Bean
    fun globalErrorResponses(): OperationCustomizer {
        return OperationCustomizer { operation: Operation, _: HandlerMethod ->
            val apiResponses = operation.responses ?: ApiResponses()

            if (!apiResponses.containsKey("400")) {
                val schema = Schema<Any>().apply {
                    `$ref` = "#/components/schemas/ErrorResponse"
                }

                apiResponses.addApiResponse(
                    "400",
                    ApiResponse()
                        .description("Bad Request")
                        .content(
                            Content().addMediaType(
                                "application/json",
                                MediaType().schema(schema)
                            )
                        )
                )
            }
            operation.responses = apiResponses
            operation
        }
    }

    @Bean
    fun forceRequiredCustomizer(): OpenApiCustomizer {
        return OpenApiCustomizer { openApi ->
            openApi.components?.schemas?.values?.forEach { schema ->
                val properties = schema.properties ?: return@forEach
                properties.keys.forEach { propName ->
                    val prop = properties[propName]
                    val isNullable = prop?.nullable == true
                    val isAlreadyRequired = schema.required?.contains(propName) == true

                    if (!isNullable && !isAlreadyRequired) {
                        schema.addRequiredItem(propName)
                    }
                }
            }
        }
    }
}