package co.id.project.dhimas.onlineshop.model.response;

import co.id.project.dhimas.onlineshop.base.data.BaseDataResponse;
import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RecordBuilder
public record InventoryResponse(
        @NotBlank
        int id,
        @NotBlank
        int itemId,
        @NotNull
        int qty,
        String type
) implements BaseDataResponse {
}
