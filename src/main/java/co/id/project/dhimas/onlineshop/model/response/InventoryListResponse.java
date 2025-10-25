package co.id.project.dhimas.onlineshop.model.response;

import co.id.project.dhimas.onlineshop.base.data.BaseDataResponse;
import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@RecordBuilder
public record InventoryListResponse(
        @NotBlank
        List<Inventory> inventories,
        int page,
        int size,
        Long totalItems,
        int totalPages
) implements BaseDataResponse {

    @RecordBuilder
    public record Inventory(
            @NotBlank
            int id,
            @NotBlank
            int itemId,
            @NotNull
            int qty,
            String type
    ) {
    }
}
