package co.id.project.dhimas.onlineshop.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorType {
    RESOURCE_NOT_FOUND(991, "RESOURCE_NOT_FOUND"),
    STOCK_NOT_ENOUGH(992, "STOCK_NOT_ENOUGH");

    private final int errorCode;
    private final String message;
}
