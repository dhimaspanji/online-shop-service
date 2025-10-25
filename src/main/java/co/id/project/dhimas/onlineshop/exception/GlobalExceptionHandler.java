package co.id.project.dhimas.onlineshop.exception;

import co.id.project.dhimas.onlineshop.exception.config.CommonProperties;
import co.id.project.dhimas.onlineshop.exception.config.ServiceProperties;
import co.id.project.dhimas.onlineshop.utils.ErrorType;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZonedDateTime;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ServiceProperties serviceProperties;
    private final CommonProperties commonProperties;

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex) {
        ErrorType type = ex.getErrorType();

        String paddedServiceCode = String.format("%03d", serviceProperties.serviceCode());

        String finalErrorCode = serviceProperties.prefix()
                + "-"
                + paddedServiceCode
                + "-"
                + type.getErrorCode();

        ErrorResponse body = ErrorResponseBuilder.builder()
                .errorCode(finalErrorCode)
                .errorDesc(type.getMessage())
                .timestamp(ZonedDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler({
            Exception.class,
            ConstraintViolationException.class,
            HttpRequestMethodNotSupportedException.class
    })
    public ResponseEntity<ErrorResponse> handleCommon(Exception ex) {

        String key = ex.getClass().getName();

        var mapping = commonProperties.mappings();

        CommonProperties.ErrorMapping cfg = resolveMapping(mapping, key);

        String paddedServiceCode = String.format("%03d", commonProperties.serviceCode());
        String finalErrorCode = commonProperties.prefix()
                + "-"
                + paddedServiceCode
                + "-"
                + cfg.errorCode();

        ErrorResponse body = ErrorResponseBuilder.builder()
                .errorCode(finalErrorCode)
                .errorDesc(cfg.errorDesc())
                .timestamp(ZonedDateTime.now())
                .build();

        HttpStatus status = HttpStatus.valueOf(cfg.status());

        return ResponseEntity.status(status).body(body);
    }

    private CommonProperties.ErrorMapping resolveMapping(
            Map<String, CommonProperties.ErrorMapping> mappings,
            String key
    ) {
        var m = mappings.get(key);

        if (m == null) {
            m = mappings.get("java.lang.Exception");
        }

        return m;
    }
}
