package co.id.project.dhimas.onlineshop.exception;

import co.id.project.dhimas.onlineshop.utils.ErrorType;

public class GeneralErrorException extends BaseException {

    public GeneralErrorException(ErrorType errorType) {
        super(errorType);
    }
}
