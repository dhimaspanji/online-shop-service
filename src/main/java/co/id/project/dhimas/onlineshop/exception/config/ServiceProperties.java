package co.id.project.dhimas.onlineshop.exception.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("origin.error")
public record ServiceProperties(
        @NotBlank
        @DefaultValue("ORG")
        String prefix,
        @Min(0)
        @Max(999)
        int serviceCode
) {
}
