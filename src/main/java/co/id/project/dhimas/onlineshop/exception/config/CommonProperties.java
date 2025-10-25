package co.id.project.dhimas.onlineshop.exception.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Validated
@ConfigurationProperties("origin.error.common")
public record CommonProperties(
        @NotBlank
        @DefaultValue("GNR")
        String prefix,
        @Min(0)
        @Max(999)
        @DefaultValue("0")
        int serviceCode,
        Map<String, ErrorMapping> mappings
) {

    @Validated
    public record ErrorMapping(
            @NotBlank
            String message,
            @NotNull
            String status,
            @NotBlank
            String errorDesc,
            @Min(0)
            @Max(999)
            @DefaultValue("0")
            int errorCode
    ) {}
}
