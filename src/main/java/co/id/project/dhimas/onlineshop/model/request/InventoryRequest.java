package co.id.project.dhimas.onlineshop.model.request;

import co.id.project.dhimas.onlineshop.base.data.BaseDataRequest;
import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RecordBuilder
public record InventoryRequest(
        @NotNull
        int itemId,
        @NotNull
        @Min(0)
        int qty,
        @NotBlank
        String type
) implements BaseDataRequest {
}
