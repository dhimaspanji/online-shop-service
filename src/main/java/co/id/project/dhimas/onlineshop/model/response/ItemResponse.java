package co.id.project.dhimas.onlineshop.model.response;

import co.id.project.dhimas.onlineshop.base.data.BaseDataResponse;
import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RecordBuilder
public record ItemResponse(
        @NotBlank
        int id,
        @NotBlank
        String name,
        @NotNull @Min(0)
        int price,
        int remainingStock
) implements BaseDataResponse {
}
