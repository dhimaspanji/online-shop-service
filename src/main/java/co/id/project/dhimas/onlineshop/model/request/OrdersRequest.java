package co.id.project.dhimas.onlineshop.model.request;

import co.id.project.dhimas.onlineshop.base.data.BaseDataRequest;
import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.NotNull;

@RecordBuilder
public record OrdersRequest(
        @NotNull
        int itemId,
        @NotNull
        int qty
) implements BaseDataRequest {
}
