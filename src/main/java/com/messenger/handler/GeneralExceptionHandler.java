package com.messenger.handler;

import com.messenger.exception.BusinessException;
import com.messenger.payload.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GeneralExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e,
                                                                 WebRequest request) {
        log.error("GeneralExceptionHandler | handleBusinessException | exception: " + e.getMessage());
        ErrorResponse errorResponse= new ErrorResponse();

        errorResponse.setMessage(e.getMessage());
        return new ResponseEntity<>(errorResponse, e.getHttpStatus());
    }
}
