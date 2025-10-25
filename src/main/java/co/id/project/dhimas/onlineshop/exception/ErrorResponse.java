package co.id.project.dhimas.onlineshop.exception;

import io.soabase.recordbuilder.core.RecordBuilder;

import java.time.ZonedDateTime;

@RecordBuilder
public record ErrorResponse(
        String errorCode,
        String errorDesc,
        ZonedDateTime timestamp
) { }
