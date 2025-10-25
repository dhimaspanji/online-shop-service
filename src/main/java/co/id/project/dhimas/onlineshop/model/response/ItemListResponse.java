package co.id.project.dhimas.onlineshop.model.response;

import co.id.project.dhimas.onlineshop.base.data.BaseDataResponse;
import co.id.project.dhimas.onlineshop.model.entity.Item;
import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@RecordBuilder
public record ItemListResponse(
        @NotBlank
        List<Item> items,
        int page,
        int size,
        Long totalItems,
        int totalPages
) implements BaseDataResponse {

    @RecordBuilder
    public record Item(
            @NotBlank
            int id,
            @NotBlank
            String name,
            @NotNull @Min(0)
            int price,
            int remainingStock
    ) {}
}
