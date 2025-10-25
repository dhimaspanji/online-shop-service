package co.id.project.dhimas.onlineshop.exception;

import co.id.project.dhimas.onlineshop.utils.ErrorType;

public class ResourceNotFoundException extends BaseException {

    public ResourceNotFoundException() {
        super(ErrorType.RESOURCE_NOT_FOUND);
    }

    public ResourceNotFoundException(String extraMessage) {
        super(ErrorType.RESOURCE_NOT_FOUND, extraMessage);
    }
}
