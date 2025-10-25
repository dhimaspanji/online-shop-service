package co.id.project.dhimas.onlineshop.model.response;

import co.id.project.dhimas.onlineshop.base.data.BaseDataResponse;
import io.soabase.recordbuilder.core.RecordBuilder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@RecordBuilder
public record OrdersListResponse(
        List<Orders> orders,
        int page,
        int size,
        Long totalItems,
        int totalPages
) implements BaseDataResponse {

    @RecordBuilder
    public record Orders(
            @NotBlank
            String orderNo,
            @NotNull
            int itemId,
            @NotNull
            int qty,
            @NotNull
            int price
    ) {
    }
}
